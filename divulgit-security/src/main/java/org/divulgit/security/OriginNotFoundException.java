package org.divulgit.security;

import org.springframework.security.core.AuthenticationException;

public class OriginNotFoundException extends AuthenticationException {

    public OriginNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public OriginNotFoundException(String msg) {
        super(msg);
    }
}
