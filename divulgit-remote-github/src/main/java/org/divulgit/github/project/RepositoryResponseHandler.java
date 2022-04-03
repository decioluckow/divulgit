package org.divulgit.github.project;

import java.util.List;

import org.divulgit.remote.exception.RemoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RepositoryResponseHandler {
	
    @Autowired
    private RepositoryMapper mapper;

    public List<GitHubRepository> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertToProjects(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}
