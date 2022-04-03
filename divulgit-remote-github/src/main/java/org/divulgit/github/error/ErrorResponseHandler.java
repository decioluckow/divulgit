package org.divulgit.github.error;

import java.util.Optional;

import org.divulgit.github.project.GitHubRepository;
import org.divulgit.remote.exception.RemoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ErrorResponseHandler {
	
	@Autowired
	private GitHubErrorMapper errorMapper;
	
    public Optional<GitHubRepository> handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            ErrorMessage errorMessage = errorMapper.convertFrom(response.getBody());
            throw new RemoteException(errorMessage.getMessage());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}
