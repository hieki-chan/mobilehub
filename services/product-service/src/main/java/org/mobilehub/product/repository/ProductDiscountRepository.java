package org.mobilehub.product.repository;

import org.mobilehub.product.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    //@Query("SELECT d FROM ProductDiscount d WHERE d.productId = :productId ORDER BY d.startDate DESC LIMIT 1")
    //Optional<ProductDiscount> findLatestByProductId(Long productId);
}
