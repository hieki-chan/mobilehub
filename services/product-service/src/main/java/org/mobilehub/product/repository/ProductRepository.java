package org.mobilehub.product.repository;

import feign.Param;
import org.mobilehub.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    @Query("SELECT p FROM Product p JOIN p.discount d WHERE d.startDate <= :now AND d.endDate >= :now")
    List<Product> findActiveDiscountProducts(@Param("now") LocalDateTime now);
}
