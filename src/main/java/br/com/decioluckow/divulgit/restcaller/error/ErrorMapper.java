package br.com.decioluckow.divulgit.restcaller.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

@Component
public class ErrorMapper {

    public ErrorMessage convertFrom(String json) throws JsonProcessingException {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        final ObjectMapper objectMapper = builder.build();
        return objectMapper.readValue(json, ErrorMessage.class);
    }
}
