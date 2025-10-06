package org.mobilehub.product_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mobilehub.product_service.converter.MediaConverter;
import org.mobilehub.product_service.dto.request.CreateProductRequest;
import org.mobilehub.product_service.dto.request.UpdateProductRequest;
import org.mobilehub.product_service.dto.response.ProductDetailResponse;
import org.mobilehub.product_service.dto.response.ProductPreviewResponse;
import org.mobilehub.product_service.dto.response.ProductResponse;
import org.mobilehub.product_service.entity.Product;
import org.mobilehub.product_service.entity.ProductStatus;
import org.mobilehub.product_service.exception.ProductNotFoundException;
import org.mobilehub.product_service.mapper.ProductMapper;
import org.mobilehub.product_service.repository.ProductRepository;
import org.mobilehub.shared_contracts.media.ImageTopics;
import org.mobilehub.shared_contracts.media.ImageUploadEvent;
import org.mobilehub.shared_contracts.media.ImageUploadedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MediaConverter mediaConverter;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    //@Transactional
    public ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> files) {
        Product product = productMapper.toProduct(request);
        Product savedProduct = productRepository.save(product);

        // publish upload image event
        ImageUploadEvent event = new ImageUploadEvent();
        event.setProductId(product.getId());
        event.setFiles(mediaConverter.convertToBase64(files));

        kafkaTemplate.send(ImageTopics.IMAGE_UPLOAD, event);

        return productMapper.toProductResponse(savedProduct);
    }

    @KafkaListener(topics = ImageTopics.IMAGE_UPLOADED)
    public void handleImageUploaded(ImageUploadedEvent event) {
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(event.getProductId()));
        product.getImages().add(productMapper.toProductImage(event));
        productRepository.save(product);

       System.out.println("Image uploaded: " + event.getPublicId());
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
        return productMapper.toProductResponse(getProduct(id));
    }

    public ProductPreviewResponse getProductPreview(Long id)
    {
        return productMapper.toProductPreviewResponse(getProduct(id));
    }

    public ProductDetailResponse getProductDetail(Long id)
    {
        return productMapper.toProductDetailResponse(getProduct(id));
    }

    public Product getProduct(Long id)
    {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }
}
