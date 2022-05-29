package org.divulgit.gitlab.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.gitlab.project.GitLabProject;
import org.divulgit.remote.exception.RemoteException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserResponseHandler {

    public GitLabUser handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            ObjectMapper objectMapper = builder.build();
            return objectMapper.readValue(response.getBody(), GitLabUser.class);
        } catch (JsonProcessingException e) {
            throw parseException(response, e);
        }
    }

    private RemoteException parseException(ResponseEntity<String> response, JsonProcessingException e) throws RemoteException {
        String message = "Error on converting json to Object";
        log.error(message + "[json: " + response.getBody() + "]");
        return new RemoteException(message, e);
    }
}
