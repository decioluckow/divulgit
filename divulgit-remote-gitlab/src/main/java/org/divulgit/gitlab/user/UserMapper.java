package org.divulgit.gitlab.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public GitLabUser convertToUser(String json) throws JsonProcessingException {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        ObjectMapper objectMapper = builder.build();
        return objectMapper.readValue(json, GitLabUser.class);
    }
}
