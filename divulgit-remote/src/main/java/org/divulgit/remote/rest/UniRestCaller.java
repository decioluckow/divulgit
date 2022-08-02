package org.divulgit.remote.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.response.Response;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UniRestCaller implements RestCaller {

    private HeaderAuthFiller headerAuthFiller;
    private ErrorResponseHandler errorResponseHandler;

    public UniRestCaller(ErrorResponseHandler errorResponseHandler, HeaderAuthFiller headerAuthFiller) {
        this.headerAuthFiller = headerAuthFiller;
        this.errorResponseHandler = errorResponseHandler;
    }

    public ResponseEntity<String> call(String url, Authentication authentication) throws RemoteException {
        log.debug("Invoking {}", url);
        GetRequest getRequest = Unirest.get(url);
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headerAuthFiller.fill(headers, authentication);
        getRequest.headers(headers);
        try {
            HttpResponse<String> jsonResponse = getRequest.asString();
            ResponseEntity<String> responseEntity = ResponseEntity.status(jsonResponse.getStatus()).body(jsonResponse.getBody());
            if (this.errorResponseHandler.isErrorResponse(responseEntity)) {
                this.errorResponseHandler.handleErrorResponse(responseEntity);
            }
            return responseEntity;
        } catch (UnirestException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    public static interface HeaderAuthFiller {
        void fill(Map<String, String> headers, Authentication authentication);
    }
}
