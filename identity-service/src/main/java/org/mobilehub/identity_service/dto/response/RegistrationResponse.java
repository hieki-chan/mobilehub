package org.mobilehub.identity_service.dto.response;

import org.mobilehub.identity_service.dto.request.RegisterUserRequest;


public record RegistrationResponse (
    RegisterUserRequest  registration,
    boolean isValid,
    String message){

}
