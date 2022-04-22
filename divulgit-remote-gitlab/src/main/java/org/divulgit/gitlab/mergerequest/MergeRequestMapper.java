package org.divulgit.gitlab.mergerequest;

import java.util.List;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Component
public class MergeRequestMapper {

    public List<GitLabMergeRequest> convertoToMergeRequests(String json) throws JsonProcessingException {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        final ObjectMapper objectMapper = builder.build();
        final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, GitLabMergeRequest.class);
        return objectMapper.readValue(json, collectionType);
    }
}
