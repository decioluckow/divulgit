package org.divulgit.gitlab.error;

import org.divulgit.annotation.ForRemote;
import org.divulgit.type.RemoteType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@ForRemote(RemoteType.GITLAB)
public class ErrorMapper {

    public ErrorMessage convertFrom(String json) throws JsonProcessingException {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        final ObjectMapper objectMapper = builder.build();
        return objectMapper.readValue(json, ErrorMessage.class);
    }
}
