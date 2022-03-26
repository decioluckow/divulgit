package org.divulgit.github.user;

import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.github.error.ErrorMapper;
import org.divulgit.github.error.ErrorMessage;
import org.divulgit.github.restcaller.GitLabRestCaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.remote.remote.model.RemoteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class CurrentUserCaller {

    @Autowired
    private GitLabRestCaller restCaller;

    @Autowired
    private UserURLGenerator urlGenerator;
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ErrorMapper errorMapper;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public Optional<RemoteUser> retrieveCurrentUser(Remote remote, String token) throws RemoteException {
        String url = urlGenerator.build(remote);
        ResponseEntity<String> response = restCaller.call(url, token);
        Optional<RemoteUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = handle200Response(response);
        } else if (response.getBody().contains("error_description")) {
            handleErrorResponse(response);
        }
        return authenticatedUser;
    }

    private Optional<RemoteUser> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return Optional.ofNullable((RemoteUser) userMapper.convertToUser(response.getBody()));
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }

    private Optional<GitHubUser> handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
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
