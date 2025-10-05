package org.mobilehub.product_service.repository;

import org.mobilehub.product_service.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    //@Query("SELECT d FROM ProductDiscount d WHERE d.productId = :productId ORDER BY d.startDate DESC LIMIT 1")
    //Optional<ProductDiscount> findLatestByProductId(Long productId);
}
