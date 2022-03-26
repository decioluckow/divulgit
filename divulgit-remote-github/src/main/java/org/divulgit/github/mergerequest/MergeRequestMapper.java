package org.divulgit.github.mergerequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.divulgit.github.user.GitHubUser;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MergeRequestMapper {

    public List<GitHubMergeRequest> convertToMergeRequests(String json) throws JsonProcessingException {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        final ObjectMapper objectMapper = builder.build();
        final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, GitHubMergeRequest.class);
        return objectMapper.readValue(json, collectionType);
    }

    public GitHubMergeRequest convertToMergeRequest(String json) throws JsonProcessingException {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        ObjectMapper objectMapper = builder.build();
        return objectMapper.readValue(json, GitHubMergeRequest.class);
    }
}
