package org.mobilehub.product_service.repository;

import org.mobilehub.product_service.dto.response.BrandDistributionResponse;
import org.mobilehub.product_service.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("""
    SELECT new org.mobilehub.product_service.dto.response.BrandDistributionResponse(
        p.spec.brand,
        SUM(v.sold),
        SUM(v.price * v.sold)
    )
    FROM ProductVariant v
    JOIN v.product p
    GROUP BY p.spec.brand
""")
    List<BrandDistributionResponse> getBrandDistributionData();
}

