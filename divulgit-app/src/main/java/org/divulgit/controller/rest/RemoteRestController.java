package org.divulgit.controller.rest;

import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.security.UserAuthentication;
import org.divulgit.security.UserDetails;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.ScanExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
public class RemoteRestController {

    @Autowired
    private ScanExecutor taskExecutor;

    @Autowired
    private RemoteRepository remoteRepos;

    @PostMapping("/in/rest/remote/scan")
    public RemoteScan.UniqueKey scan(Authentication auth) {
        UserAuthentication userAuthentication = (UserAuthentication) auth;
        UserDetails userDetails = userAuthentication.getUserDetails();
        User user = userDetails.getUser();
        Remote remote = loadRemote(user.getRemoteId());
        return taskExecutor.scanRemoteForProjects(remote, user, userDetails.getRemoteToken());
    }

    private Remote loadRemote(String remoteId) {
        final Optional<Remote> remote = remoteRepos.findById(remoteId);
        if (!remote.isPresent()) {
            throw new RuntimeException("Remote " + remoteId + " não encontrada");
        }
        return remote.get();
    }
}