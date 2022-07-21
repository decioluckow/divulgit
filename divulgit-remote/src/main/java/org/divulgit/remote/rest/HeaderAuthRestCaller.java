package org.divulgit.remote.rest;

import java.util.Collections;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeaderAuthRestCaller {

    private RestTemplateCustomizer restTemplateCustomizer;
    private HeaderAuthFiller headerAuthFiller;

    public HeaderAuthRestCaller(RestTemplateCustomizer restTemplateCustomizer, HeaderAuthFiller headerAuthFiller) {
        this.restTemplateCustomizer = restTemplateCustomizer;
        this.headerAuthFiller = headerAuthFiller;
    }

    public ResponseEntity<String> call(String url, Authentication authentication) {
        log.debug("Invoking {}", url);

        RestTemplate restTemplate = new RestTemplate();
        restTemplateCustomizer.customize(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headerAuthFiller.fill(headers, authentication);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        log.debug("Invoked, response size: {}", exchange.getBody().getBytes().length);
        return exchange;
    }

    public static interface HeaderAuthFiller {

        void fill(HttpHeaders headers, Authentication authentication);
    }
}
