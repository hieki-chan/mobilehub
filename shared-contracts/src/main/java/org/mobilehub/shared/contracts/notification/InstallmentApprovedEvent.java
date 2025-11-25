package org.mobilehub.shared.contracts.notification;

public record InstallmentApprovedEvent(
        Long applicationId,
        String userId,
        String planName,
        int tenorMonths,
        String userEmail
) {}
