package org.divulgit.security;

import org.springframework.security.core.AuthenticationException;

public class RemoteNotFoundException extends AuthenticationException {

    public RemoteNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RemoteNotFoundException(String msg) {
        super(msg);
    }
}
