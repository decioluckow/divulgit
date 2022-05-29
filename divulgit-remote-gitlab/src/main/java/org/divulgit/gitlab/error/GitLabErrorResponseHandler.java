package org.divulgit.gitlab.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ForRemote(RemoteType.GITLAB)
public class GitLabErrorResponseHandler implements ErrorResponseHandler {
	
    @Override
    public boolean isErrorResponse(ResponseEntity<String> response) {
        return response.getBody().contains("error_description");
    }

    @Override
    public void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            ObjectMapper objectMapper = builder.build();
            ErrorMessage errorMessage = objectMapper.readValue(response.getBody(), ErrorMessage.class);
            throw new RemoteException("[" + errorMessage.getError() + "] " + errorMessage.getErrorDescription());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}
