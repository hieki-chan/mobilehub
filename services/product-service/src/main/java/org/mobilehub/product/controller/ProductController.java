package org.mobilehub.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.product.dto.request.CreateProductRequest;
import org.mobilehub.product.dto.request.UpdateProductRequest;
import org.mobilehub.product.dto.response.ProductCartResponse;
import org.mobilehub.product.dto.response.ProductDetailResponse;
import org.mobilehub.product.dto.response.ProductPreviewResponse;
import org.mobilehub.product.dto.response.ProductResponse;
import org.mobilehub.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final ProductService productService;

    // region Admin-side APIs
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestPart("request") CreateProductRequest request,
            @RequestPart("files") MultipartFile[] files) {
        ProductResponse created = productService.createProduct(request, Arrays.stream(files).toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{productId}/cart")
    public ResponseEntity<ProductCartResponse> getProductCart(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductCartResponse(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest updateRequest
    ) {
        ProductResponse updated = productService.updateProduct(productId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    )
    {
        return ResponseEntity.ok(productService.getProducts(page, size));
    }

    // endregion

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId)
    {
        return ResponseEntity.ok(productService.getProductResponse(productId));
    }

    @GetMapping("/{productId}/preview")
    public ResponseEntity<ProductPreviewResponse> getProductPreview(@PathVariable Long productId)
    {
        return ResponseEntity.ok(productService.getProductPreview(productId));
    }

    @GetMapping("/{productId}/detail")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable Long productId)
    {
        return ResponseEntity.ok(productService.getProductDetail(productId));
    }

    // region Client-side APIs
    @GetMapping("/discounted")
    public ResponseEntity<List<ProductResponse>> getDiscountedProducts()
    {
        var discountedProducts = productService.getDiscountedProducts();
        return ResponseEntity.ok(discountedProducts);
    }
    // endregion
}
