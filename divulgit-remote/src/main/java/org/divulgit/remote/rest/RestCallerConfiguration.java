package org.divulgit.remote.rest;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestCallerConfiguration {

    @Bean
    public HeaderAuthRestCaller gitLabRestCaller(RestTemplateCustomizer restTemplateCustomizer) {
        return new HeaderAuthRestCaller(restTemplateCustomizer,
            ((headers, authentication) -> headers.add("Private-Token", (String) authentication.getCredentials())));
    }

    @Bean
    public HeaderAuthRestCaller gitHubRestCaller(RestTemplateCustomizer restTemplateCustomizer) {
        return new HeaderAuthRestCaller(restTemplateCustomizer,
            ((headers, authentication) -> headers.add("Authorization","token " + (String) authentication.getCredentials())));
    }

    //TODO wesley implementar um metodo igual aos outros aqui, mas adicionando a header do jeito que o bitbucket espera
}
