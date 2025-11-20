package org.mobilehub.customer_service.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public final class UserAccess {
    public static Long getPrincipalId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static void validateUserAccess(Long pathUserId) {
        Long principalId = getPrincipalId();
        if (!principalId.equals(pathUserId)) {
            throw new AccessDeniedException("You have no permission to access other customer data");
        }
    }
}
