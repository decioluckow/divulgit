package org.divulgit.remote.rest;

import org.divulgit.remote.exception.RemoteException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface RestCaller {

    ResponseEntity<String> call(String url, Authentication authentication) throws RemoteException;
}
