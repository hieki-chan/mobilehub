package org.mobilehub.payment_service.repo;

import org.mobilehub.payment_service.entity.PaymentCapture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCaptureRepository extends JpaRepository<PaymentCapture, Long> { }
