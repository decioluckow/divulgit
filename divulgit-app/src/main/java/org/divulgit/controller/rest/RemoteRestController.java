package org.divulgit.controller.rest;

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
public class RemoteRestController {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private RemoteRepository remoteRepos;

    @PostMapping("/in/remote/scan")
    public Map<String, String> scan(Authentication auth) {
        UserAuthentication userAuthentication = (UserAuthentication) auth;
        UserDetails userDetails = userAuthentication.getUserDetails();
        User user = userDetails.getUser();
        Remote remote = loadRemote(user.getRemoteId());
        TaskUniqueKey taskUniqueKey = taskExecutor.scanRemoteForProjects(remote, user, userDetails.getRemoteToken());
        return Map.of("taskKey", taskUniqueKey.getTaskUniqueKey());
    }

    private Remote loadRemote(String remoteId) {
        final Optional<Remote> remote = remoteRepos.findById(remoteId);
        if (!remote.isPresent()) {
            throw new RuntimeException("Remote " + remoteId + " não encontrada");
        }
        return remote.get();
    }
}