package org.divulgit.remote.rest.error;

import org.divulgit.remote.exception.RemoteException;
import org.springframework.http.ResponseEntity;

public interface ErrorResponseHandler {

    boolean isErrorResponse(ResponseEntity<String> response);
    void handleErrorResponse(ResponseEntity<String> response) throws RemoteException;
}
