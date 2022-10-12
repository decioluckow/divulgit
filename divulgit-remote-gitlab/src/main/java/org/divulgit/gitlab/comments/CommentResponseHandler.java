package org.divulgit.gitlab.comments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.remote.exception.RemoteException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CommentResponseHandler {
	
    public List<GitLabComment> handle200ResponseMultipleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            final ObjectMapper objectMapper = builder.build();
            final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, GitLabComment.class);
            return objectMapper.readValue(response.getBody(), collectionType);
        } catch (JsonProcessingException e) {
            throw parseException(response, e);
        }
    }

    public GitLabComment handle200ResponseSingleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            ObjectMapper objectMapper = builder.build();
            return objectMapper.readValue(response.getBody(), GitLabComment.class);
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
