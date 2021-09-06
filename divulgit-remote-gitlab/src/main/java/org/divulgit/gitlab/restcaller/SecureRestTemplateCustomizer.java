package org.divulgit.gitlab.restcaller;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.net.URL;
import java.util.Arrays;


@Component
@EnableConfigurationProperties(SecureRestTemplateProperties.class)
@Slf4j
public class SecureRestTemplateCustomizer implements RestTemplateCustomizer {

    private final SecureRestTemplateProperties properties;

    private final SSLContext sslContext;

    @Autowired
    public SecureRestTemplateCustomizer(SecureRestTemplateProperties properties) {
        this.properties = properties;
        this.sslContext = loadSSLContext();
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        final HttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .build();

        final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        log.info("Registered SSL truststore {} for client requests", properties.getTrustStore());
        restTemplate.setRequestFactory(requestFactory);
    }

    private SSLContext loadSSLContext() {
        try {
            return SSLContextBuilder.create()
                    .loadTrustMaterial(new URL(properties.getTrustStore()),
                            properties.getTrustStorePassword())
                    .setProtocol(properties.getProtocol())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to setup client SSL context", e);
        } finally {
            // it's good security practice to zero out passwords,
            // which is why they're char[]
            Arrays.fill(properties.getTrustStorePassword(), (char) 0);
        }
    }
}