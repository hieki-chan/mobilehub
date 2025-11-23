package org.mobilehub.order_service.repository;

import org.mobilehub.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
    Integer countByVariantId(Long variantId);
}
