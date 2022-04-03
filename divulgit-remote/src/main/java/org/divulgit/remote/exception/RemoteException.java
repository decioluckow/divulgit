package org.divulgit.remote.exception;

public class RemoteException extends Exception {

	private static final long serialVersionUID = -8218248675095617591L;

	public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
