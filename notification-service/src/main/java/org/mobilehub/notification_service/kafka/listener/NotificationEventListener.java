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

    // ============================================================
    // 1) PAYMENT CAPTURED
    // ============================================================
    @KafkaListener(
            topics = NotificationTopics.PAYMENT_CAPTURED,
            containerFactory = "paymentCapturedListenerFactory"  // ✅ Chỉ định factory
    )
    public void onPaymentCaptured(PaymentCapturedEvent e) {
        log.info("[NOTI] Received PaymentCapturedEvent: orderId={}, userId={}, amount={}, userEmail={}",
                e.orderId(), e.userId(), e.amount(), e.userEmail());

        notiSvc.create(
                e.userId(),
                "Thanh toán thành công",
                "Bạn đã thanh toán thành công đơn #" + e.orderId()
                        + " (" + e.amount() + " " + e.currency() + ").",
                NotificationType.PAYMENT,
                "ORDER",
                e.orderId().toString()
        );

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

    // ============================================================
    // 2) INSTALLMENT APPROVED
    // ============================================================
    @KafkaListener(
            topics = NotificationTopics.INSTALLMENT_APPROVED,
            containerFactory = "installmentApprovedListenerFactory"  // ✅ Chỉ định factory
    )
    public void onInstallmentApproved(InstallmentApprovedEvent e) {
        log.info("[NOTI] Received InstallmentApprovedEvent: applicationId={}, userId={}, planName={}, tenorMonths={}, userEmail={}",
                e.applicationId(), e.userId(), e.planName(), e.tenorMonths(), e.userEmail());

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

    // ============================================================
    // 3) INSTALLMENT PAYMENT DUE
    // ============================================================
    @KafkaListener(
            topics = NotificationTopics.INSTALLMENT_PAYMENT_DUE,
            containerFactory = "installmentPaymentDueListenerFactory"  // ✅ Chỉ định factory
    )
    public void onInstallmentPaymentDue(InstallmentPaymentDueEvent e) {
        log.info("[NOTI] Received InstallmentPaymentDueEvent: contractId={}, paymentId={}, userId={}, dueDate={}, amountDue={}",
                e.contractId(), e.paymentId(), e.userId(), e.dueDate(), e.amountDue());

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

    // ============================================================
    // HELPER
    // ============================================================
    private void safeSendEmail(String to, String subject, String body, String eventType, String refId) {
        if (to == null || to.isBlank()) {
            log.warn("[NOTI][EMAIL] SKIP - missing email: eventType={}, refId={}", eventType, refId);
            return;
        }

        try {
            log.info("[NOTI][EMAIL] Sending to={}, subject={}, eventType={}, refId={}",
                    to, subject, eventType, refId);
            emailSender.send(to, subject, body);
            log.info("[NOTI][EMAIL] SUCCESS - sent to={}, eventType={}, refId={}", to, eventType, refId);
        } catch (Exception ex) {
            log.error("[NOTI][EMAIL] FAILED to={}, eventType={}, refId={}, reason={}",
                    to, eventType, refId, ex.getMessage(), ex);
        }
    }
}