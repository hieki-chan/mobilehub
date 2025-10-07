package org.mobilehub.product.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mobilehub.product.dto.request.CreateDiscountRequest;
import org.mobilehub.product.dto.response.DiscountResponse;
import org.mobilehub.product.mapper.ProductDiscountMapper;
import org.mobilehub.product.repository.ProductDiscountRepository;
import org.mobilehub.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductDiscountService {

    private final ProductDiscountRepository discountRepository;
    private final ProductRepository productRepository;
    private final ProductDiscountMapper discountMapper;

    @Transactional
    public DiscountResponse createDiscount(CreateDiscountRequest createDiscountRequest,
                                           Long productId) throws IllegalArgumentException {
        var discount = discountMapper.toProductDiscount(createDiscountRequest);

        if (discount.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date must be today or in the future");
        }

        if (!discount.getEndDate().isAfter(discount.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        var savedDiscount =  discountRepository.save(discount);

        //Update Product with discount
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setDiscount(savedDiscount);
        productRepository.save(product);

        return discountMapper.toDiscountResponse(savedDiscount);
    }

    public void deleteDiscount(Long id) {
        discountRepository.deleteById(id);
    }
}

