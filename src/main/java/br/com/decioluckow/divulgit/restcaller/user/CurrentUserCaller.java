package br.com.decioluckow.divulgit.restcaller.user;

import br.com.decioluckow.divulgit.restcaller.GitLabRestCaller;
import br.com.decioluckow.divulgit.restcaller.error.ErrorMapper;
import br.com.decioluckow.divulgit.restcaller.error.ErrorMessage;
import br.com.decioluckow.divulgit.restcaller.exception.CallerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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

    public Optional<GitLabUser> retrieveCurrentUser(final String token) throws CallerException {
        ResponseEntity<String> response = restCaller.call("https://git.neogrid.com/api/v4/user/", token);
        Optional<GitLabUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = handle200Response(response);
        } else if (response.getBody().contains("error_description")) {
            handleErrorResponse(response);
        }
        return authenticatedUser;
    }

    private Optional<GitLabUser> handle200Response(ResponseEntity<String> response) throws CallerException {
        try {
            return Optional.ofNullable(userMapper.convertFrom(response.getBody()));
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new CallerException(message, e);
        }
    }

    private Optional<GitLabUser> handleErrorResponse(ResponseEntity<String> response) throws CallerException {
        try {
            ErrorMessage errorMessage = errorMapper.convertFrom(response.getBody());
            throw new CallerException(errorMessage.getErrorDescription());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new CallerException(message, e);
        }
    }
}
