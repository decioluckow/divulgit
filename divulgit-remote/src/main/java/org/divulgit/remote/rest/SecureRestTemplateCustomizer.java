package org.divulgit.remote.rest;

import java.net.URL;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class SecureRestTemplateCustomizer implements RestTemplateCustomizer {

    @Value("${http.client.ssl.trust-store:classpath:/trustedCerts.jks}")
    private Resource keyStore;

    @Value("${http.client.ssl.trust-store-password:changeit}")
    private String keyStorePassword;

    private SSLContext sslContext;

    @PostConstruct
    public void init() {
        this.sslContext = loadSSLContext();
    }

    @SneakyThrows
    @Override
    public void customize(RestTemplate restTemplate) {
        final HttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .build();

        final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        log.info("Registered SSL truststore {} for client requests", keyStore.getURL());
        restTemplate.setRequestFactory(requestFactory);
    }

    private SSLContext loadSSLContext() {
        try {
            return SSLContextBuilder.create()
                    .loadTrustMaterial(keyStore.getURL(), keyStorePassword.toCharArray())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to setup client SSL context", e);
        } finally {
            // it's good security practice to zero out passwords,
            // which is why they're char[]
            keyStorePassword = null;
        }
    }
}