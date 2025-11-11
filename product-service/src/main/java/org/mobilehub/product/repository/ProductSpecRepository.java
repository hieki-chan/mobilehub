package org.mobilehub.product.repository;

import org.mobilehub.product.entity.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long> {
}

