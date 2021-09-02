package org.divulgit.gitlab.user;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.gitlab.error.ErrorMapper;
import org.divulgit.gitlab.error.ErrorMessage;
import org.divulgit.gitlab.restcaller.GitLabRestCaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
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
    private UserMapper userMapper;

    @Autowired
    private ErrorMapper errorMapper;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public Optional<GitLabUser> retrieveCurrentUser(final String token) throws RemoteException {
        ResponseEntity<String> response = restCaller.call("https://git.neogrid.com/api/v4/user/", token);
        Optional<GitLabUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = handle200Response(response);
        } else if (response.getBody().contains("error_description")) {
            handleErrorResponse(response);
        }
        return authenticatedUser;
    }

    private Optional<GitLabUser> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return Optional.ofNullable(userMapper.convertToUser(response.getBody()));
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }

    private Optional<GitLabUser> handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            ErrorMessage errorMessage = errorMapper.convertFrom(response.getBody());
            throw new RemoteException(errorMessage.getErrorDescription());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}
