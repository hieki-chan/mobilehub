package org.mobilehub.product.repository;

import feign.Param;
import org.mobilehub.product.entity.Product;
import org.mobilehub.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>
{
    @Query("SELECT p FROM Product p JOIN p.discount d WHERE d.startDate <= :now AND d.endDate >= :now")
    List<Product> findActiveDiscountProducts(@Param("now") LocalDateTime now);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}
