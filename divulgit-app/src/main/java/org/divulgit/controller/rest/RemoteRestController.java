package org.divulgit.controller.rest;

import org.divulgit.controller.helper.EntityLoader;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.executor.ScanExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RemoteRestController {

    @Autowired
    private ScanExecutor taskExecutor;

    @Autowired
    private EntityLoader loader;

    @PostMapping("/in/rest/remote/scan")
    public RemoteScan.UniqueId scan(Authentication authentication) {
        User user = loader.loadUser(authentication);
        Remote remote = loader.loadRemote(user.getRemoteId());
        return taskExecutor.scanRemoteForProjects(remote, user, authentication);
    }
}
