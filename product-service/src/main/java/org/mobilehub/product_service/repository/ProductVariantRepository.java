package org.mobilehub.product_service.repository;

import org.mobilehub.product_service.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>
{
}
