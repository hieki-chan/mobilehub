package org.mobilehub.identity_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
