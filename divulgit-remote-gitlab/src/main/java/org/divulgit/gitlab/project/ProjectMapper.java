package org.divulgit.gitlab.project;

import java.util.List;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Component
public class ProjectMapper {

    public List<GitLabProject> convertToProjects(String json) throws JsonProcessingException {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        final ObjectMapper objectMapper = builder.build();
        final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, GitLabProject.class);
        return objectMapper.readValue(json, collectionType);
    }
}
