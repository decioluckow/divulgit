package org.divulgit.github.error;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.divulgit.annotation.ForRemote;
import org.divulgit.github.project.GitHubRepository;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ForRemote(RemoteType.GITHUB)
public class GitHubErrorResponseHandler implements ErrorResponseHandler {
	
    @Override
    public boolean isErrorResponse(ResponseEntity<String> response) {
        return response.getBody().contains("message");
    }

    @Override
    public void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            final ObjectMapper objectMapper = builder.build();
            ErrorMessage errorMessage =  objectMapper.readValue(response.getBody(), ErrorMessage.class);
            throw new RemoteException(errorMessage.getMessage());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}
