package org.divulgit.github.pullrequest.comment;

import java.util.List;

import org.divulgit.remote.exception.RemoteException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PullRequestCommentResponseHandler {
	
    public List<GitHubPullRequestComment> handle200ResponseMultipleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            final ObjectMapper objectMapper = builder.build();
            final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, GitHubPullRequestComment.class);
            return objectMapper.readValue(response.getBody(), collectionType);
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }
}
