package org.divulgit.github.user;

import java.util.Optional;

import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.error.ErrorResponseHandler;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CurrentUserCaller {

    @Autowired
    private RestCaller restCaller;

    @Autowired
    private GitHubURLBuilder urlBuilder;
    
    @Autowired
    private GitHubUserMapper userMapper;

    @Autowired
    private ErrorResponseHandler errorResponseHandler;

    public Optional<RemoteUser> retrieveCurrentUser(Remote remote, String token) throws RemoteException {
        final String url = urlBuilder.buildUserURL(remote);
        ResponseEntity<String> response = restCaller.call(url, token);
        Optional<RemoteUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = handle200Response(response);
        } else if (response.getBody().contains("error_description")) {
        	errorResponseHandler.handleErrorResponse(response);
        }
        return authenticatedUser;
    }

    private Optional<RemoteUser> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return Optional.ofNullable((RemoteUser) userMapper.convert(response.getBody()));
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }

}
