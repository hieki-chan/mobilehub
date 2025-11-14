package org.mobilehub.payment_service.repository;

import org.mobilehub.payment_service.entity.PaymentRefund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, Long> { }
