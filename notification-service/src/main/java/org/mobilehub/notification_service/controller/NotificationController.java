package org.mobilehub.notification_service.controller;

import lombok.RequiredArgsConstructor;
import org.mobilehub.notification_service.dto.NotificationResponse;
import org.mobilehub.notification_service.dto.PageResponse;
import org.mobilehub.notification_service.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService svc;

    @GetMapping
    public PageResponse<NotificationResponse> myNotifications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(required=false) String status
    ) {
        String userId = jwt.getSubject();
        return svc.getUserNotifications(userId, page, size, status);
    }

    @PutMapping("/{id}/read")
    public NotificationResponse markRead(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id
    ) {
        String userId = jwt.getSubject();
        return svc.markRead(userId, id);
    }

    @GetMapping("/unread-count")
    public long unreadCount(@AuthenticationPrincipal Jwt jwt) {
        return svc.countUnread(jwt.getSubject());
    }
}
