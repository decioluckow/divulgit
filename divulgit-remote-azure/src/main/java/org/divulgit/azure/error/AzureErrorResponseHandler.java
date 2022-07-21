package org.divulgit.azure.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.divulgit.annotation.ForRemote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ForRemote(RemoteType.AZURE)
public class AzureErrorResponseHandler implements ErrorResponseHandler {
	
    @Override
    public boolean isErrorResponse(ResponseEntity<String> response) {
        return response.getStatusCode().value() != 200;
    }

    @Override
    public void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        throw new RemoteException(response.getStatusCode().toString());
    }
}
