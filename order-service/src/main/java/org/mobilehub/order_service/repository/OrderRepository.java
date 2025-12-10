package org.mobilehub.order_service.repository;

import org.mobilehub.order_service.dto.response.MonthlyOrderCountResponse;
import org.mobilehub.order_service.dto.response.MonthlySalesResponse;
import org.mobilehub.order_service.dto.response.OrderStatusCountResponse;
import org.mobilehub.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserId(Long userId);

    @Query("SELECT new org.mobilehub.order_service.dto.response.MonthlySalesResponse(" +
            "EXTRACT(YEAR FROM o.createdAt), " +
            "EXTRACT(MONTH FROM o.createdAt), " +
            "SUM(oi.finalPrice * oi.quantity)) " +
            "FROM Order o JOIN o.items oi " +
            "GROUP BY EXTRACT(YEAR FROM o.createdAt), EXTRACT(MONTH FROM o.createdAt)")
    List<MonthlySalesResponse> findMonthlySales();


    @Query("SELECT new org.mobilehub.order_service.dto.response.MonthlyOrderCountResponse(" +
            "EXTRACT(YEAR FROM o.createdAt) AS year, EXTRACT(MONTH FROM o.createdAt) AS month, " +
            "COUNT(o.id)) " +
            "FROM Order o " +
            "GROUP BY EXTRACT(YEAR FROM o.createdAt), EXTRACT(MONTH FROM o.createdAt)")
    List<MonthlyOrderCountResponse> findMonthlyOrderCount();

    @Query("SELECT new org.mobilehub.order_service.dto.response.OrderStatusCountResponse(" +
            "o.status, COUNT(o.id)) " +
            "FROM Order o " +
            "GROUP BY o.status")
    List<OrderStatusCountResponse> countOrdersByStatus();

}
