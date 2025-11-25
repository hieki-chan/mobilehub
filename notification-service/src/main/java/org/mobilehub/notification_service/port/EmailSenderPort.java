package org.mobilehub.notification_service.port;

public interface EmailSenderPort {
    void send(String to, String subject, String body);
}
