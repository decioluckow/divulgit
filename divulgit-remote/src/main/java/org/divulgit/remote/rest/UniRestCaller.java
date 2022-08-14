package org.divulgit.remote.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Slf4j
public class UniRestCaller implements RestCaller {

    private Customizer customizer;
    private ErrorResponseHandler errorResponseHandler;

    public UniRestCaller(ErrorResponseHandler errorResponseHandler, Customizer customizer) {
        this.customizer = customizer;
        this.errorResponseHandler = errorResponseHandler;
    }

    public ResponseEntity<String> call(String url, Authentication authentication) throws RemoteException {
        log.debug("Invoking {}", url);
        GetRequest getRequest = Unirest.get(url);
        getRequest.header("accept", "application/json");
        customizer.apply(getRequest, authentication);
        try {
            HttpResponse<String> jsonResponse = getRequest.asString();
            ResponseEntity<String> responseEntity = ResponseEntity.status(jsonResponse.getStatus()).body(jsonResponse.getBody());
            if (this.errorResponseHandler.isErrorResponse(responseEntity)) {
                this.errorResponseHandler.handleErrorResponse(responseEntity);
            }
            return responseEntity;
        } catch (UnirestException e) {
            log.error(e.getMessage(), e.getCause());
            throw new RemoteException(e.getMessage(), e);
        }
    }

    public static interface Customizer {
        void apply(HttpRequest httpRequest, Authentication authentication);
    }
}
