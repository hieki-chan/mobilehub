package org.mobilehub.payment_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    /** PAYMENT_SUCCEEDED | PAYMENT_FAILED | PAYMENT_CANCELED */
    private String type;

    /** Khóa để order-service tra cứu (gợi ý: dùng luôn Order.id làm orderCode) */
    private Long orderCode;

    /** Số tiền gốc đơn hàng (VND) */
    private Long amount;

    /** VND (đặt cứng nếu bạn chỉ dùng VND) */
    private String currency;

    /** payos */
    private String provider;

    /** "00" = thành công; khác "00" là thất bại/hủy */
    private String providerCode;

    /** Mã tham chiếu từ webhook (nếu có) */
    private String reference;

    /** Thời điểm phát sự kiện (UTC offset) */
    private OffsetDateTime at;
}
