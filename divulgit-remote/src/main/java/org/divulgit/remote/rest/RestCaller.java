package org.divulgit.remote.rest;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestCaller {

    //TODO criar uma classe generica de rest caller baseada em TokenHeader

    @Autowired
    private RestTemplateCustomizer restTemplateCustomizer;

    public ResponseEntity<String> call(String url, String token) {

        log.debug("Invoking {}", url);
        RestTemplate restTemplate = new RestTemplate();
        restTemplateCustomizer.customize(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","token " + token);
        headers.add("Private-Token",token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        log.debug("Invoked, response size: {}", exchange.getBody().getBytes().length);
        return exchange;
    }
}
