package org.mobilehub.payment_service.repo;

import org.mobilehub.payment_service.entity.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> { }
