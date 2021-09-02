package org.divulgit.controller;

import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.security.UserAuthentication;
import org.divulgit.security.UserDetails;
import org.divulgit.tasks.TaskExecutor;
import org.divulgit.tasks.TaskUniqueKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RemoteRepository originRepos;

    @PostMapping("/in/origin/scan")
    public Map<String, String> scan(Authentication auth) {
        UserAuthentication userAuthentication = (UserAuthentication) auth;
        UserDetails userDetails = userAuthentication.getUserDetails();
        User user = userDetails.getUser();
        Remote remote = loadOrigin(user.getRemoteId());
        TaskUniqueKey taskUniqueKey = taskExecutor.scanProjects(remote, user, userDetails.getOriginToken());
        return Map.of("taskKey", taskUniqueKey.getTaskUniqueKey());
    }

    private Remote loadOrigin(String originId) {
        final Optional<Remote> origin = originRepos.findById(originId);
        if (!origin.isPresent()) {
            throw new RuntimeException("Origin " + originId + " não encontrada");
        }
        return origin.get();
    }
}