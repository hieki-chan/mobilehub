package org.mobilehub.shared.contracts.notification;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentPaymentDueEvent(
        Long contractId,
        Long paymentId,
        String userId,
        LocalDate dueDate,
        BigDecimal amountDue,
        String currency,
        int installmentNo,
        int totalInstallments,
        String userEmail
) {}
