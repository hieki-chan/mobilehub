package org.mobilehub.notification_service.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.notification_service.enums.NotificationType;
import org.mobilehub.notification_service.port.EmailSenderPort;
import org.mobilehub.notification_service.service.NotificationService;
import org.mobilehub.shared.contracts.notification.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notiSvc;
    private final EmailSenderPort emailSender;

    // 1) Thanh toán thành công
    @KafkaListener(topics = NotificationTopics.PAYMENT_CAPTURED, groupId = "notification-service")
    public void onPaymentCaptured(PaymentCapturedEvent e) {
        log.info("[NOTI] payment captured order={}, userId={}, userEmail={}", 
                e.orderId(), e.userId(), e.userEmail() != null ? e.userEmail() : "null");

        // ✅ Luôn lưu notification vào DB trước
        notiSvc.create(
                e.userId(),
                "Thanh toán thành công",
                "Bạn đã thanh toán thành công đơn #" + e.orderId()
                        + " (" + e.amount() + " " + e.currency() + ").",
                NotificationType.PAYMENT,
                "ORDER",
                e.orderId().toString()
        );

        // ✅ Gửi email: FAIL thì log, KHÔNG throw để tránh kẹt consumer
        safeSendEmail(
                e.userEmail(),
                "MobileHub - Thanh toán thành công",
                "Chào bạn,\n\n"
                        + "Đơn hàng #" + e.orderId() + " đã được thanh toán thành công.\n"
                        + "Số tiền: " + e.amount() + " " + e.currency() + "\n\n"
                        + "Cảm ơn bạn đã mua hàng tại MobileHub!",
                "PAYMENT_CAPTURED",
                e.orderId() != null ? e.orderId().toString() : "null"
        );
    }

    // 2) Trả góp được duyệt
    @KafkaListener(topics = NotificationTopics.INSTALLMENT_APPROVED, groupId = "notification-service")
    public void onInstallmentApproved(InstallmentApprovedEvent e) {
        log.info("[NOTI] installment approved app={}", e.applicationId());

        notiSvc.create(
                e.userId(),
                "Trả góp được duyệt",
                "Yêu cầu trả góp #" + e.applicationId()
                        + " đã được duyệt. Gói: " + e.planName()
                        + ", kỳ hạn: " + e.tenorMonths() + " tháng.",
                NotificationType.INSTALLMENT,
                "APPLICATION",
                e.applicationId().toString()
        );

        safeSendEmail(
                e.userEmail(),
                "MobileHub - Duyệt trả góp thành công",
                "Chào bạn,\n\n"
                        + "Yêu cầu trả góp #" + e.applicationId() + " đã được duyệt.\n"
                        + "Gói: " + e.planName() + "\n"
                        + "Kỳ hạn: " + e.tenorMonths() + " tháng.\n\n"
                        + "Bạn có thể theo dõi hợp đồng và lịch thanh toán trong mục Trả góp.",
                "INSTALLMENT_APPROVED",
                e.applicationId() != null ? e.applicationId().toString() : "null"
        );
    }

    // 3) Nhắc đóng tiền đến kỳ
    @KafkaListener(topics = NotificationTopics.INSTALLMENT_PAYMENT_DUE, groupId = "notification-service")
    public void onInstallmentPaymentDue(InstallmentPaymentDueEvent e) {
        log.info("[NOTI] installment due payment={}", e.paymentId());

        notiSvc.create(
                e.userId(),
                "Đến hạn đóng tiền trả góp",
                "Kỳ #" + e.installmentNo() + "/" + e.totalInstallments()
                        + " của hợp đồng #" + e.contractId()
                        + " đến hạn vào " + e.dueDate()
                        + ". Số tiền cần đóng: " + e.amountDue() + " " + e.currency() + ".",
                NotificationType.INSTALLMENT,
                "CONTRACT",
                e.contractId().toString()
        );

        safeSendEmail(
                e.userEmail(),
                "MobileHub - Nhắc đóng tiền trả góp",
                "Chào bạn,\n\n"
                        + "Kỳ #" + e.installmentNo() + "/" + e.totalInstallments()
                        + " của hợp đồng #" + e.contractId()
                        + " đến hạn vào " + e.dueDate() + ".\n"
                        + "Số tiền cần đóng: " + e.amountDue() + " " + e.currency() + ".\n\n"
                        + "Vui lòng thanh toán đúng hạn để tránh phí phạt.",
                "INSTALLMENT_PAYMENT_DUE",
                e.paymentId() != null ? e.paymentId().toString() : "null"
        );
    }

    private void safeSendEmail(String to, String subject, String body, String eventType, String refId) {
        if (to == null || to.isBlank()) {
            log.warn("[NOTI][EMAIL] SKIP - missing email: eventType={}, refId={}, to={}", 
                    eventType, refId, to);
            return;
        }

        try {
            log.info("[NOTI][EMAIL] Sending to={}, subject={}, eventType={}, refId={}", 
                    to, subject, eventType, refId);
            emailSender.send(to, subject, body);
            log.info("[NOTI][EMAIL] SUCCESS - sent to={}, eventType={}, refId={}", to, eventType, refId);
        } catch (Exception ex) {
            // ✅ quan trọng: không throw, để Kafka commit offset
            log.error("[NOTI][EMAIL] FAILED to={}, eventType={}, refId={}, reason={}",
                    to, eventType, refId, ex.getMessage(), ex);
        }
    }
}
