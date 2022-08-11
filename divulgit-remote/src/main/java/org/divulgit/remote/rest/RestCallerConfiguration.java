package org.divulgit.remote.rest;

import org.divulgit.annotation.ForRemote;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
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
            ((headers, authentication) -> headers.add("Private-Token", (String) authentication.getCredentials())));
    }

    @Bean
    public HeaderAuthRestCaller gitHubRestCaller(RestTemplateCustomizer restTemplateCustomizer) {
        return new HeaderAuthRestCaller(restTemplateCustomizer,
                ((headers, authentication) -> headers.add("Authorization","token " + (String) authentication.getCredentials())));
    }

    @Bean
    public HeaderAuthRestCaller azureRestCaller(RestTemplateCustomizer restTemplateCustomizer) {
        return new HeaderAuthRestCaller(restTemplateCustomizer,
                ((headers, authentication) -> {
                    String base64Token = Base64.getEncoder().encodeToString((":" + (String) authentication.getCredentials()).getBytes(StandardCharsets.UTF_8)); ;
                    headers.add("Authorization", "Basic " + base64Token);
                }));
    }

    @Bean
    public RestCaller bitBucketRestCaller(@ForRemote(RemoteType.BITBUCKET) ErrorResponseHandler errorResponseHandler) {
        return new UniRestCaller(errorResponseHandler,
                ((httpHequest, authentication) -> {
                    httpHequest.basicAuth(authentication.getPrincipal().toString(), authentication.getCredentials().toString());
                }));
    }
}
