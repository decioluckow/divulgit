package org.divulgit.gitlab.restcaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class GitLabRestCaller {

    @Autowired
    private RestTemplateCustomizer restTemplateCustomizer;

    //getForEntity
    //https://www.baeldung.com/spring-resttemplate-json-list

    public ResponseEntity<String> call(final String url, final String token) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplateCustomizer.customize(restTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Private-Token",token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

    }
}
