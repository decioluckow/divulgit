package br.com.decioluckow.divulgit.controller;

import br.com.decioluckow.divulgit.model.Origin;
import br.com.decioluckow.divulgit.model.User;
import br.com.decioluckow.divulgit.repository.OriginRepository;
import br.com.decioluckow.divulgit.restcaller.GitLabRestCaller;
import br.com.decioluckow.divulgit.security.UserAuthentication;
import br.com.decioluckow.divulgit.security.UserDetails;
import br.com.decioluckow.divulgit.tasks.TaskExecutor;
import br.com.decioluckow.divulgit.tasks.TaskUniqueKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mongodb.store.MongoDbMessageStore;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class RestOriginController {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private OriginRepository originRepos;

    @PostMapping("/in/origin/scan")
    public Map<String, String> scan(Authentication auth) {
        UserAuthentication userAuthentication = (UserAuthentication) auth;
        UserDetails userDetails = userAuthentication.getUserDetails();
        User user = userDetails.getUser();
        Origin origin = loadOrigin(user.getOriginId());
        TaskUniqueKey taskUniqueKey = taskExecutor.scanProjects(origin, user, userDetails.getOriginToken());
        return Map.of("taskKey", taskUniqueKey.getValue());
    }

    private Origin loadOrigin(String originId) {
        final Optional<Origin> origin = originRepos.findById(originId);
        if (!origin.isPresent()) {
            throw new RuntimeException("Origin " + originId + " não encontrada");
        }
        return origin.get();
    }
}