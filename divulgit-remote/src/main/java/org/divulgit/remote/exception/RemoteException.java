package org.divulgit.remote.exception;

public class RemoteException extends Exception {

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
