package org.mobilehub.order_service.repository;

import org.mobilehub.order_service.dto.response.BestSellingProductResponse;
import org.mobilehub.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
    Integer countByVariantId(Long variantId);

    @Query("""
    SELECT new org.mobilehub.order_service.dto.response.BestSellingProductResponse(
        oi.productId,
        oi.productName,
        SUM(oi.quantity)
    )
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.status = 'DELIVERED'
    GROUP BY oi.productId, oi.productName
    ORDER BY SUM(oi.quantity) DESC
""")
    List<BestSellingProductResponse> findBestSellingProducts();


    @Query("""
    SELECT COALESCE(SUM(oi.quantity), 0)
    FROM OrderItem oi
    JOIN oi.order o
    WHERE o.status = 'DELIVERED'
""")
    Long getTotalSoldQuantity();
}
