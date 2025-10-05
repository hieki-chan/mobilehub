package org.mobilehub.product_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.product_service.dto.request.CreateProductRequest;
import org.mobilehub.product_service.dto.request.UpdateProductRequest;
import org.mobilehub.product_service.dto.response.ProductResponse;
import org.mobilehub.product_service.entity.Product;
import org.mobilehub.product_service.service.ProductService;
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

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestPart("request") CreateProductRequest request,
            @RequestPart("files") MultipartFile[] files) {
        ProductResponse created = productService.createProduct(request, Arrays.stream(files).toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest updateRequest
    ) {
        ProductResponse updated = productService.updateProduct(productId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@RequestParam Long productId)
    {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getDiscountProduct()
    {
        return ResponseEntity.ok(null);
    }
}
