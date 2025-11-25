package org.mobilehub.notification_service.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.notification_service.service.NotificationService;
import org.mobilehub.notification_service.enums.NotificationType;
import org.mobilehub.notification_service.port.EmailSenderPort;
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
        log.info("[NOTI] payment captured order={}", e.orderId());

        notiSvc.create(
                e.userId(),
                "Thanh toán thành công",
                "Bạn đã thanh toán thành công đơn #" + e.orderId()
                        + " (" + e.amount() + " " + e.currency() + ").",
                NotificationType.PAYMENT,
                "ORDER",
                e.orderId().toString()
        );

        if (e.userEmail() != null && !e.userEmail().isBlank()) {
            emailSender.send(
                    e.userEmail(),
                    "MobileHub - Thanh toán thành công",
                    "Chào bạn,\n\n"
                            + "Đơn hàng #" + e.orderId() + " đã được thanh toán thành công.\n"
                            + "Số tiền: " + e.amount() + " " + e.currency() + "\n\n"
                            + "Cảm ơn bạn đã mua hàng tại MobileHub!"
            );
        }
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

        if (e.userEmail() != null && !e.userEmail().isBlank()) {
            emailSender.send(
                    e.userEmail(),
                    "MobileHub - Duyệt trả góp thành công",
                    "Chào bạn,\n\n"
                            + "Yêu cầu trả góp #" + e.applicationId() + " đã được duyệt.\n"
                            + "Gói: " + e.planName() + "\n"
                            + "Kỳ hạn: " + e.tenorMonths() + " tháng.\n\n"
                            + "Bạn có thể theo dõi hợp đồng và lịch thanh toán trong mục Trả góp."
            );
        }
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

        if (e.userEmail() != null && !e.userEmail().isBlank()) {
            emailSender.send(
                    e.userEmail(),
                    "MobileHub - Nhắc đóng tiền trả góp",
                    "Chào bạn,\n\n"
                            + "Kỳ #" + e.installmentNo() + "/" + e.totalInstallments()
                            + " của hợp đồng #" + e.contractId()
                            + " đến hạn vào " + e.dueDate() + ".\n"
                            + "Số tiền cần đóng: " + e.amountDue() + " " + e.currency() + ".\n\n"
                            + "Vui lòng thanh toán đúng hạn để tránh phí phạt."
            );
        }
    }
}
