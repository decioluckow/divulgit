package org.divulgit.security;

import org.springframework.security.core.AuthenticationException;

public class RemoteNotFoundException extends AuthenticationException {

	private static final long serialVersionUID = -426717978604582074L;

	public RemoteNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RemoteNotFoundException(String msg) {
        super(msg);
    }
}
