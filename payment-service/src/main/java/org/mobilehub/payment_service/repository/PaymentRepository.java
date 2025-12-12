package org.mobilehub.payment_service.repository;

import org.mobilehub.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderCode(Long orderCode);
    Optional<Payment> findByProviderPaymentId(String providerPaymentId);
    Optional<Payment> findTopByOrderIdOrderByIdDesc(Long orderId);
}
