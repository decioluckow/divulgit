package org.divulgit.remote.rest;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
                ((headers, token) -> headers.add("Authorization", "token " + token)));
    }

    @Bean
    public HeaderAuthRestCaller azureRestCaller(RestTemplateCustomizer restTemplateCustomizer) {
        return new HeaderAuthRestCaller(restTemplateCustomizer,
                ((headers, token) -> {
                    String base64Token = Base64.getEncoder().encodeToString((":" + token).getBytes(StandardCharsets.UTF_8)); ;
                    headers.add("Authorization", "Basic " + base64Token);
                }));
    }


}
