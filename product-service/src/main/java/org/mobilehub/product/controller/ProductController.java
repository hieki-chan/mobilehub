package org.mobilehub.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.product.dto.request.CreateProductRequest;
import org.mobilehub.product.dto.request.ProductSnapshotRequest;
import org.mobilehub.product.dto.request.UpdateProductRequest;
import org.mobilehub.product.dto.response.*;
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
@RequestMapping()
@RequiredArgsConstructor
@Validated
@SuppressWarnings("unused")
public class ProductController {
    private final ProductService productService;

    // region Admin-side APIs
    @PostMapping(value = "/admin/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestPart("request") CreateProductRequest request,
            @RequestPart("files") MultipartFile[] files) {
        ProductResponse created = productService.createProduct(request, Arrays.stream(files).toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/admin/products")
    public ResponseEntity<Page<AdminProductResponse>> getProductsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    )
    {
        return ResponseEntity.ok(productService.getProductsForAdmin(page, size));
    }

    @GetMapping("/admin/products/{productId}/detail")
    public ResponseEntity<AdminProductDetailsResponse> getProductDetailForAdmin(@PathVariable Long productId)
    {
        return ResponseEntity.ok(productService.getProductDetailForAdmin(productId));
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest updateRequest
    ) {
        ProductResponse updated = productService.updateProduct(productId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }

    // endregion

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId)
    {
        return ResponseEntity.ok(productService.getProductResponse(productId));
    }

    @GetMapping("/products/{productId}/preview")
    public ResponseEntity<ProductPreviewResponse> getProductPreview(@PathVariable Long productId)
    {
        return ResponseEntity.ok(productService.getProductPreview(productId));
    }

    // region Client-side APIs
    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> getPagedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Page<ProductResponse> products = productService.getPagedProducts(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{productId}/details")
    public ResponseEntity<ProductDetailsResponse> getProductDetails(@PathVariable Long productId)
    {
        return ResponseEntity.ok(productService.getProductDetails(productId));
    }

    @GetMapping("/products/discounted")
    public ResponseEntity<List<ProductResponse>> getDiscountedProducts()
    {
        var discountedProducts = productService.getDiscountedProducts();
        return ResponseEntity.ok(discountedProducts);
    }

    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "all") String priceRange,
            @RequestParam(required = false) List<String> brands
    ) {
        Page<ProductResponse> products = productService.searchProducts(page, size, q, priceRange, brands);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/search/name")
    public ResponseEntity<List<ProductResponse>> searchProductsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<ProductResponse> products = productService.searchProductsByName(name, limit);
        return ResponseEntity.ok(products);
    }

    // endregion

    @GetMapping("/products/carts")
    public ResponseEntity<List<ProductCartResponse>> getProductCarts(
            @RequestParam List<Long> productIds
    ) {
        return ResponseEntity.ok(productService.getProductCarts(productIds));
    }

    @PostMapping("/products/snapshots")
    public ResponseEntity<List<ProductSnapshotResponse>> getProductsSnapshot(
            @RequestBody List<ProductSnapshotRequest> requests
    ) {
        return ResponseEntity.ok(productService.getProductsSnapshot(requests));
    }

    @GetMapping("/products/{productId}/validate")
    public ResponseEntity<Boolean> checkValidVariant(
            @PathVariable Long productId,
            @RequestParam Long variantId
    ) {
        return ResponseEntity.ok(productService.isProductVariantValid(productId, variantId));
    }
}
