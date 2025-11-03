package org.mobilehub.product.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mobilehub.product.dto.response.*;
import org.mobilehub.product.entity.*;
import org.mobilehub.product.mapper.ProductDiscountMapper;
import org.mobilehub.product.mapper.ProductSpecMapper;
import org.mobilehub.product.repository.ProductDiscountRepository;
import org.mobilehub.product.repository.ProductSpecRepository;
import org.mobilehub.shared.common.converter.MediaConverter;
import org.mobilehub.product.dto.request.CreateProductRequest;
import org.mobilehub.product.dto.request.UpdateProductRequest;
import org.mobilehub.product.exception.ProductNotFoundException;
import org.mobilehub.product.mapper.ProductMapper;
import org.mobilehub.product.repository.ProductRepository;
import org.mobilehub.shared.contracts.media.ImageDeleteEvent;
import org.mobilehub.shared.contracts.media.ImageTopics;
import org.mobilehub.shared.contracts.media.ImageUploadEvent;
import org.mobilehub.shared.contracts.media.ImageUploadedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductDiscountRepository productDiscountRepository;

    private final ProductMapper productMapper;
    private final ProductSpecMapper productSpecMapper;
    private final ProductDiscountMapper productDiscountMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // region ADMIN SERVICES
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> files) {
        // product
        Product product = productMapper.toProduct(request);

//        // spec
//        var productSpec = productSpecMapper.toProductSpec(request.getSpec());
//        var savedSpec = productSpecRepository.save(productSpec);
//        product.setSpec(savedSpec);
//
//        // discount
//        var productDiscount = productDiscountMapper.toProductDiscount(request.getDiscount());
//        var savedDiscount = productDiscountRepository.save(productDiscount);
//        product.setDiscount(savedDiscount);

        // product
        Product savedProduct = productRepository.save(product);

        // publish upload image event
        if (files != null && !files.isEmpty()) {
            ImageUploadEvent event = new ImageUploadEvent();
            event.setProductId(savedProduct.getId());
            event.setFiles(MediaConverter.convertToBase64(files));
            event.setFolder("products/" + savedProduct.getId());

            kafkaTemplate.send(ImageTopics.IMAGE_UPLOAD, event);
        }

        return productMapper.toProductResponse(savedProduct);
    }

    public ProductResponse updateProduct(Long id, UpdateProductRequest updateRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productMapper.updateProduct(product, updateRequest);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductResponse(updatedProduct);
    }

    public ProductResponse getProductResponse(Long id)
    {
        var product = getProduct(id);
        var response =  productMapper.toProductResponse(product);
        response.setImageUrl(product.getMainImageUrl());
        return response;
    }

    public ProductPreviewResponse getProductPreview(Long id)
    {
        var product = getProduct(id);
        var response =  productMapper.toProductPreviewResponse(product);
        response.setImageUrl(product.getMainImageUrl());
        return response;
    }

    public ProductDetailResponse getProductDetail(Long id)
    {
        var product = getProduct(id);
        var response =  productMapper.toProductDetailResponse(product);
        response.setMainImageUrl(
                product.getMainImageUrl()
        );

        response.setOtherImageUrls(
                product.getOtherImageUrls()
        );
        return response;
    }

    public Page<AdminProductResponse> getProductsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Product> pageData = productRepository.findAll(pageable);

        return pageData.map(productMapper::toAdminProductResponse);
    }

    public AdminProductDetailResponse getProductDetailForAdmin(Long productId) {
        return productMapper.toAdminProductDetailResponse(getProduct(productId));
    }

    // endregion


    public ProductCartResponse getProductCartResponse(Long productId) {
        var product = getProduct(productId);
        return productMapper.toProductCartResponse(product);
    }

    public Product getProduct(Long id)
    {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<ProductResponse> getDiscountedProducts() {
        return productRepository.findActiveDiscountProducts(LocalDateTime.now())
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    public Page<ProductResponse> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Product> pageData = productRepository.findAll(pageable);

        return pageData.map(productMapper::toProductResponse);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);

        // send delete events
        for(ProductImage image: product.getImages())
        {
            ImageDeleteEvent event = new ImageDeleteEvent();
            event.setPublicId(image.getPublicId());

            kafkaTemplate.send(ImageTopics.IMAGE_UPLOAD, event);
        }
    }

    @Transactional
    @KafkaListener(topics = ImageTopics.IMAGE_UPLOADED)
    public void handleImageUploaded(ImageUploadedEvent event) {
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(event.getProductId()));
        product.getImages().add(productMapper.toProductImage(event));
        productRepository.save(product);

        System.out.println("Image saved: " + event.getPublicId());
    }
}
