package org.mobilehub.inventory_service.entity;

public enum ReservationStatus {
    PENDING,      // Mới tạo, đang giữ hàng
    CONFIRMED,    // Thanh toán thành công, đã trừ kho
    RELEASED,     // Thanh toán thất bại / hết hạn, đã trả hàng lại
    CANCELED      //Don bi huy thu cong
}
