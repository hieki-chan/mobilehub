package org.mobilehub.identity_service.dto.request;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String idToken;
}
