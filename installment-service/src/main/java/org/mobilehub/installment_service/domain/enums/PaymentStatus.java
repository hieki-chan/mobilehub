package org.mobilehub.installment_service.domain.enums;

public enum PaymentStatus {
    PLANNED,   // kế hoạch, chưa đến hạn / chưa trả
    PAID,      // đã thanh toán
    OVERDUE    // quá hạn
}