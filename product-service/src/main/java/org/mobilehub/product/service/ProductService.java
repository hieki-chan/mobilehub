package org.mobilehub.product.service;

import org.mobilehub.product.client.CloudMediaServiceClient;
import org.mobilehub.product.dto.request.ProductSnapshotRequest;
import org.mobilehub.product.exception.VariantNotFoundException;
import org.mobilehub.product.repository.ProductVariantRepository;
import org.mobilehub.product.util.DiscountUtils;
import org.mobilehub.shared.contracts.media.MultipleImageResponse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
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
import org.mobilehub.shared.contracts.media.ImageUploadEvent;
import org.mobilehub.shared.contracts.media.ImageUploadedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String VARIANT_ID_PROP = "id";
    private static final String KEY_IMAGE_PROP = "isKey";

    private final ProductRepository productRepository;
    private final ProductSpecRepository productSpecRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final ProductVariantRepository productVariantRepository;

    private final ProductMapper productMapper;
    private final ProductSpecMapper productSpecMapper;
    private final ProductDiscountMapper productDiscountMapper;

    private final CloudMediaServiceClient cloudMediaServiceClient;
    //private final KafkaTemplate<String, Object> kafkaTemplate;

    // region ADMIN SERVICES
//    @Transactional
//    public ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> files) {
//        // product
//        Product product = productMapper.toProduct(request);
//        product.setDefaultVariant(product.getVariants().get(request.getDefaultVariantIndex()));
//        product.getVariants().forEach(v -> v.setProduct(product));
//        // product
//        Product savedProduct = productRepository.save(product);
//
//        // publish upload image event
//        if (files != null && !files.isEmpty()) {
//            var variants = savedProduct.getVariants();
//            @SuppressWarnings("unchecked")
//            Map<String, Object>[] propertiesMap = new HashMap[variants.size()];
//
//            // set props for each variant
//            for (int i = 0; i < variants.size(); i++) {
//                var images = request.getImageMap().get(i);
//
//                // set props for each file
//                for (int j = 0; j < images.size(); j++) {
//                    var properties = propertiesMap[images.get(j)];
//                    if(properties == null)
//                         properties = propertiesMap[images.get(j)] = new HashMap<>();
//
//                    properties.put(VARIANT_ID_PROP,  variants.get(i).getId());
//                    if(j == 0) properties.put(KEY_IMAGE_PROP, true);
//                }
//            }
//
//            ImageUploadEvent event = new ImageUploadEvent();
//            event.setPropertiesMap(propertiesMap);
//            event.setFiles(MediaConverter.convertToBase64(files));
//            event.setFolder("products/" + savedProduct.getId());
//
//            kafkaTemplate.send(ImageTopics.IMAGE_UPLOAD, event);
//        }
//
//        return productMapper.toProductResponse(savedProduct);
//    }
//
//    @Transactional
//    @KafkaListener(topics = ImageTopics.IMAGE_UPLOADED)
//    public void handleImageUploaded(ImageUploadedEvent event) {
//        Object v = event.getProperty(VARIANT_ID_PROP);
//        Long id = v instanceof Integer ? ((Integer) v).longValue() : (Long) v;
//
//        ProductVariant variant = productVariantRepository.findById(id)
//                .orElseThrow(() -> new ProductNotFoundException(id));
//
//
//        ProductImage image = new ProductImage();
//        image.setImageUrl(event.getImageUrl());
//        image.setPublicId(event.getPublicId());
//        image.setVariant(variant);
//
//        variant.getImages().add(image);
//
//        if(event.getProperty(KEY_IMAGE_PROP).equals(true)) {
//            variant.setKeyImage(image);
//        }
//
//        productVariantRepository.save(variant);
//    }
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, List<MultipartFile> files) {
        // product
        Product product = productMapper.toProduct(request);
        product.setDefaultVariant(product.getVariants().get(request.getDefaultVariantIndex()));
        product.getVariants().forEach(v -> v.setProduct(product));
        // product
        Product savedProduct = productRepository.save(product);

        System.out.println(files.size());

        // publish upload image event
        if (!files.isEmpty()) {
            var variants = savedProduct.getVariants();
            @SuppressWarnings("unchecked")
            Map<String, Object>[] propertiesMap = new HashMap[files.size()];

            // set props for each variant
            for (int i = 0; i < variants.size(); i++) {
                var images = request.getImageMap().get(i);

                // set props for each file
                for (int j = 0; j < images.size(); j++) {
                    var properties = propertiesMap[images.get(j)];
                    if(properties == null)
                        properties = propertiesMap[images.get(j)] = new HashMap<>();

                    properties.put(VARIANT_ID_PROP,  variants.get(i).getId());
                    if(j == 0) properties.put(KEY_IMAGE_PROP, true);
                }
            }

            ImageUploadEvent event = new ImageUploadEvent(
                    MediaConverter.convertToBase64(files),
                    "products/" + savedProduct.getId(),
                    propertiesMap);

            MultipleImageResponse response = cloudMediaServiceClient.uploadImages(event);
            for (ImageUploadedEvent uploadedEvent : response.uploadedEvents()) {
                handleImageUploaded(uploadedEvent);
            }
        }

        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional
    public void handleImageUploaded(ImageUploadedEvent event) {
        Object v = event.getProperty(VARIANT_ID_PROP);
        Long id = v instanceof Integer ? ((Integer) v).longValue() : (Long) v;

        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));


        ProductImage image = new ProductImage();
        image.setImageUrl(event.imageUrl());
        image.setPublicId(event.publicId());
        image.setVariant(variant);

        variant.getImages().add(image);

        if(event.getProperty(KEY_IMAGE_PROP) != null && event.getProperty(KEY_IMAGE_PROP).equals(true)) {
            variant.setKeyImage(image);
        }

        productVariantRepository.save(variant);
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
        var product = getProduct_Internal(id, true);
        return productMapper.toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductPreviewResponse getProductPreview(Long id)
    {
        var product = getProduct_Internal(id, true);
        return  productMapper.toProductPreviewResponse(product);
    }


    @Transactional
    public Page<AdminProductResponse> getProductsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Product> pageData = productRepository.findAll(pageable);

        return pageData.map(productMapper::toAdminProductResponse);
    }

    @Transactional
    public AdminProductDetailsResponse getProductDetailForAdmin(Long productId) {
        var product = getProduct_Internal(productId, false);
        var response =  productMapper.toAdminProductDetailsResponse(product);
        response.setDefaultVariantId(product.getDefaultVariant().getId());
        return response;
    }

    // endregion


    // region USER SERVICES
    @Transactional
    public Page<ProductResponse> getPagedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByStatus(ProductStatus.ACTIVE, pageable)
                .map(productMapper::toProductResponse);
    }

    @Transactional
    public ProductDetailsResponse getProductDetails(Long id)
    {
        var product = getProduct_Internal(id, true);
        var response =  productMapper.toProductDetailsResponse(product);
        response.setDefaultVariantId(product.getDefaultVariant().getId());
        return response;
    }

    @Transactional
    public Page<ProductResponse> searchProducts(int page, int size, String q, String priceRange, List<String> brands) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Product> spec = (root, query, cb) ->
                cb.equal(root.get("status"), ProductStatus.ACTIVE);

        if (q != null && !q.isBlank()) {
            String term = q.toLowerCase();
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), "%" + term + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + term + "%")
                    )
            );
        }

        if (brands != null && !brands.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("brand").in(brands)
            );
        }

        if (!"all".equals(priceRange)) {
            spec = spec.and((root, query, cb) -> {
                return switch (priceRange) {
                    case "under5" -> cb.lt(root.get("defaultVariant").get("price"), 5000000);
                    case "5to10" -> cb.between(root.get("defaultVariant").get("price"), 5000000, 9999999);
                    case "10to20" -> cb.between(root.get("defaultVariant").get("price"), 10000000, 19999999);
                    case "20to30" -> cb.between(root.get("defaultVariant").get("price"), 20000000, 29999999);
                    case "over30" -> cb.ge(root.get("defaultVariant").get("price"), 30000000);
                    default -> null;
                };
            });
        }

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toProductResponse);
    }

    @Transactional
    public List<ProductResponse> searchProductsByName(String name, int limit) {
        if (name == null || name.isBlank()) return List.of();

        String term = "%" + name.toLowerCase() + "%";

        Specification<Product> spec = (root, query, cb) -> cb.and(
                cb.like(cb.lower(root.get("name")), term),
                cb.equal(root.get("status"), ProductStatus.ACTIVE)
        );

        Pageable pageable = PageRequest.of(0, limit);

        return productRepository.findAll(spec, pageable)
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    // endregion

    @Transactional
    public List<ProductCartResponse> getProductCarts(List<Long> productIds)
    {
        List<ProductCartResponse> productCartResponses = new ArrayList<>();
        for (var productId : productIds) {
            var product = getProduct_Internal(productId, false);
            var response =  productMapper.toProductCartResponse(product);
            productCartResponses.add(response);
        }
        return productCartResponses;
    }

    @Transactional
    public List<ProductSnapshotResponse> getProductsSnapshot(List<ProductSnapshotRequest> requests)
    {
        List<ProductSnapshotResponse> snapshotResponses = new ArrayList<>();
        for (ProductSnapshotRequest request : requests) {
            var product = getProduct_Internal(request.getProductId(), false);
            var variant = product.getVariants()
                    .stream()
                    .filter(v -> v.getId().equals(request.getVariantId()))
                    .findFirst()
                    .orElseThrow(() -> new VariantNotFoundException(request.getProductId(), request.getVariantId()));

            var response = ProductSnapshotResponse
                    .builder()
                    .productName(product.getName())
                    .productVariant(
                            String.format("%s-%d-%d", variant.getColor_label(), variant.getRam(), variant.getRam())
                    )
                    .thumbnailUrl(variant.getKeyImage().getImageUrl())
                    .price(variant.getPrice())
                    .discountedPrice(DiscountUtils.applyDiscount(variant.getPrice(), product.getDiscount()))
                    .status(product.getStatus())
                    .build();
            snapshotResponses.add(response);
        }
        return snapshotResponses;
    }

    @Transactional
    public boolean isProductVariantValid(Long productId, Long variantId) {
        var product = getProduct_Internal(productId, true);
        return isVariantValid_Internal(product, variantId);
    }

    private Product getProduct_Internal(Long id, boolean isActiveOnly)
    {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if(isActiveOnly && product != null && product.getStatus() != ProductStatus.ACTIVE)
            throw new ProductNotFoundException(id);

        return product;
    }

    private boolean isVariantValid_Internal(Product product, Long variantId)
    {
        return product.getVariants()
                .stream()
                .anyMatch(v -> v.getId().equals(variantId));
    }

    public List<ProductResponse> getDiscountedProducts() {
        return productRepository.findActiveDiscountProducts(LocalDateTime.now())
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);

        // send delete events
//        for(ProductImage image: product.getImages())
//        {
//            ImageDeleteEvent event = new ImageDeleteEvent();
//            event.setPublicId(image.getPublicId());
//
//            kafkaTemplate.send(ImageTopics.IMAGE_UPLOAD, event);
//        }
    }
}
