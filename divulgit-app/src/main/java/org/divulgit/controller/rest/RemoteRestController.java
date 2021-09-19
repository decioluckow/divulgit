package org.divulgit.controller.rest;

import org.divulgit.controller.helper.EntityLoader;
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
    private EntityLoader loader;

    @PostMapping("/in/rest/remote/scan")
    public RemoteScan.UniqueKey scan(Authentication authentication) {
        User user = loader.loadUser(authentication);
        Remote remote = loader.loadRemote(user.getRemoteId());
        String remoteToken = ((UserAuthentication) authentication).getRemoteToken();
        return taskExecutor.scanRemoteForProjects(remote, user, remoteToken);
    }
}
