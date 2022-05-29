package org.divulgit.remote.rest;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestCallerConfiguration {

    @Bean
    public HeaderAuthRestCaller gitLabRestCaller(RestTemplateCustomizer restTemplateCustomizer) {
        return new HeaderAuthRestCaller(restTemplateCustomizer,
            ((headers, token) -> headers.add("Private-Token", token)));
    }

    @Bean
    public HeaderAuthRestCaller gitHubRestCaller(RestTemplateCustomizer restTemplateCustomizer) {
        return new HeaderAuthRestCaller(restTemplateCustomizer,
            ((headers, token) -> headers.add("Authorization","token " + token)));
    }
}
