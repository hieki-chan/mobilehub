package org.mobilehub.shared.contracts.notification;

public final class NotificationTopics {
    private NotificationTopics() {}

    public static final String PAYMENT_CAPTURED = "payment.captured";
    public static final String INSTALLMENT_APPROVED = "installment.approved";
    public static final String INSTALLMENT_PAYMENT_DUE = "installment.payment.due";
}
