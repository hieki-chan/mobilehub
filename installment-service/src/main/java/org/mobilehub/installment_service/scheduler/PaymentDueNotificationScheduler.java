package org.mobilehub.installment_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.installment_service.client.IdentityServiceClient;
import org.mobilehub.installment_service.domain.entity.InstallmentContract;
import org.mobilehub.installment_service.domain.entity.InstallmentPayment;
import org.mobilehub.installment_service.domain.enums.PaymentStatus;
import org.mobilehub.installment_service.messaging.NotificationEventPublisher;
import org.mobilehub.installment_service.repository.InstallmentPaymentRepository;
import org.mobilehub.shared.contracts.notification.InstallmentPaymentDueEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentDueNotificationScheduler {

    private final InstallmentPaymentRepository paymentRepo;
    private final NotificationEventPublisher notificationPublisher;
    private final IdentityServiceClient identityServiceClient;

    /**
     * Chạy mỗi ngày lúc 9:00 sáng để kiểm tra các khoản thanh toán đến hạn trong 3 ngày tới
     * Cron: giây phút giờ ngày tháng thứ
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkPaymentsDue() {
        log.info("[SCHEDULER] Checking payments due...");
        
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);
        
        // Tìm các khoản thanh toán đến hạn trong 3 ngày tới và chưa thanh toán
        List<InstallmentPayment> duePayments = paymentRepo.findByDueDateBetweenAndStatus(
                today, 
                threeDaysLater, 
                PaymentStatus.PLANNED
        );
        
        log.info("[SCHEDULER] Found {} payments due between {} and {}", 
                duePayments.size(), today, threeDaysLater);
        
        for (InstallmentPayment payment : duePayments) {
            sendPaymentDueNotification(payment);
        }
    }

    /**
     * Gửi thông báo đến hạn thanh toán
     */
    private void sendPaymentDueNotification(InstallmentPayment payment) {
        try {
            InstallmentContract contract = payment.getContract();
            Long userId = contract.getApplication().getUserId();
            
            // Lấy email từ identity-service
            String userEmail = identityServiceClient.getUserEmail(userId);
            
            // Tính tổng số kỳ thanh toán
            int totalInstallments = contract.getApplication().getTenorMonths();
            
            InstallmentPaymentDueEvent event = new InstallmentPaymentDueEvent(
                    contract.getId(),
                    payment.getId(),
                    userId.toString(),
                    payment.getDueDate(),
                    BigDecimal.valueOf(payment.getAmount()),
                    "VND",
                    payment.getPeriodNumber(),
                    totalInstallments,
                    userEmail
            );
            
            notificationPublisher.publishInstallmentPaymentDue(event);
            
            log.info("[SCHEDULER] Sent payment due notification: contractId={}, paymentId={}, userId={}", 
                    contract.getId(), payment.getId(), userId);
                    
        } catch (Exception e) {
            log.error("[SCHEDULER] Failed to send payment due notification for paymentId={}", 
                    payment.getId(), e);
        }
    }
}
