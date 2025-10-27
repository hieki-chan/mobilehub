package org.mobilehub.product.service;

import org.springframework.transaction.annotation.Transactional;
//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mobilehub.product.dto.response.ProductCartResponse;
import org.mobilehub.product.mapper.ProductSpecMapper;
import org.mobilehub.product.repository.ProductSpecRepository;
import org.mobilehub.shared.common.converter.MediaConverter;
import org.mobilehub.product.dto.request.CreateProductRequest;
import org.mobilehub.product.dto.request.UpdateProductRequest;
import org.mobilehub.product.dto.response.ProductDetailResponse;
import org.mobilehub.product.dto.response.ProductPreviewResponse;
import org.mobilehub.product.dto.response.ProductResponse;
import org.mobilehub.product.entity.Product;
import org.mobilehub.product.entity.ProductImage;
import org.mobilehub.product.entity.ProductStatus;
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

    private final ProductMapper productMapper;
    private final ProductSpecMapper productSpecMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> files) {
        var productSpec = productSpecMapper.toProductSpec(request);
        productSpecRepository.save(productSpec);

        Product product = productMapper.toProduct(request);
        product.setSpec(productSpec);
        Product savedProduct = productRepository.save(product);

        // publish upload image event
        ImageUploadEvent event = new ImageUploadEvent();
        event.setProductId(product.getId());
        event.setFiles(MediaConverter.convertToBase64(files));
        event.setFolder(String.format("products/%s",  product.getId().toString()));

        kafkaTemplate.send(ImageTopics.IMAGE_UPLOAD, event);

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
        response.setImageUrl(product.getMainImage());
        return response;
    }

    @Transactional(readOnly = true)
    public ProductPreviewResponse getProductPreview(Long id)
    {
        var product = getProduct(id);
        var response =  productMapper.toProductPreviewResponse(product);
        response.setImageUrl(product.getMainImage());
        return response;
    }

    public ProductDetailResponse getProductDetail(Long id)
    {
        var product = getProduct(id);
        var response =  productMapper.toProductDetailResponse(product);
        response.setMainImageUrl(
                product.getImages().stream()
                        .filter(ProductImage::isMain)
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(null)
        );

        response.setOtherImageUrls(
                product.getImages().stream()
                        .filter(img -> !img.isMain())
                        .map(ProductImage::getImageUrl)
                        .toList()
        );
        return response;
    }


    public ProductCartResponse getProductCartResponse(Long productId) {
        var product = getProduct(productId);
        return productMapper.toProductCartResponse(product);
    }

    public Product getProduct(Long id)
    {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
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
