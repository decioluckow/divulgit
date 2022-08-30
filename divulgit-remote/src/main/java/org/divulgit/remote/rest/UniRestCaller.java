package org.divulgit.remote.rest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

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
            ResponseEntity<String> responseEntity = new ResponseEntity(
                    jsonResponse.getBody(), convertHeaders(jsonResponse), jsonResponse.getStatus());
            if (this.errorResponseHandler.isErrorResponse(responseEntity)) {
                this.errorResponseHandler.handleErrorResponse(responseEntity);
            }
            return responseEntity;
        } catch (UnirestException e) {
            log.error(e.getMessage(), e.getCause());
            throw new RemoteException(e.getMessage(), e);
        }
    }

    private HttpHeaders convertHeaders(HttpResponse<String> jsonResponse) {
        HttpHeaders headers = new HttpHeaders();
        jsonResponse.getHeaders().forEach((key, values) -> headers.put(key, values));
        return headers;
    }

    public static interface Customizer {
        void apply(HttpRequest httpRequest, Authentication authentication);
    }
}
