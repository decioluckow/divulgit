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
    public RestCaller gitLabRestCaller(@ForRemote(RemoteType.GITLAB) ErrorResponseHandler errorResponseHandler) {
        return new UniRestCaller(errorResponseHandler,
                ((httpHequest, authentication) -> {
                    httpHequest.header("Private-Token", authentication.getCredentials().toString());
                }));
    }

    @Bean
    public RestCaller gitHubRestCaller(@ForRemote(RemoteType.GITHUB) ErrorResponseHandler errorResponseHandler) {
        return new UniRestCaller(errorResponseHandler,
                ((httpHequest, authentication) -> {
                    httpHequest.header("Authorization", "token " + authentication.getCredentials().toString());
                }));
    }

    @Bean
    public RestCaller azureRestCaller(@ForRemote(RemoteType.AZURE) ErrorResponseHandler errorResponseHandler) {
        return new UniRestCaller(errorResponseHandler,
                ((httpHequest, authentication) -> {
                    httpHequest.basicAuth("", authentication.getCredentials().toString());
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
