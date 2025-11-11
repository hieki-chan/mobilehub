package org.mobilehub.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mobilehub.product.dto.request.CreateDiscountRequest;
import org.mobilehub.product.dto.response.DiscountResponse;
import org.mobilehub.product.service.ProductDiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
public class ProductDiscountController {

    private final ProductDiscountService discountService;

    @PostMapping("create")
    public ResponseEntity<DiscountResponse> createDiscount(
            @Valid @RequestBody CreateDiscountRequest request,
            @RequestParam Long productId) {
        return ResponseEntity.ok(discountService.createDiscount(request, productId));
    }

//    @GetMapping("/product/{productId}")
//    public ResponseEntity<?> getActiveDiscount(@PathVariable Long productId) {
//        Optional<ProductDiscount> discount = discountService.getActiveDiscountByProductId(productId);
//        return discount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}
