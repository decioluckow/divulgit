package org.divulgit.gitlab.restcaller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Component
public class GitLabRestCaller {

    @Autowired
    private RestTemplateCustomizer restTemplateCustomizer;

    public ResponseEntity<String> call(String url, String token) {
        log.debug("Invoking {}", url);
        RestTemplate restTemplate = new RestTemplate();
        restTemplateCustomizer.customize(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Private-Token",token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        log.debug("Invoked, response size: {}", exchange.getBody().getBytes().length);
        return exchange;
    }
}
