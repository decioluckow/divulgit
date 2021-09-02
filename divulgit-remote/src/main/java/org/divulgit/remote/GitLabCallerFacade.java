package org.divulgit.remote;

import org.divulgit.annotation.ForRemote;
import org.divulgit.remote.remote.model.RemoteUser;
import org.divulgit.type.RemoteType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ForRemote(RemoteType.GITLAB)
public class GitLabCallerFacade implements RemoteCallerFacade {

    @Override
    public Optional<RemoteUser> retrieveRemoteUser(String token) {
        return null;
    }
}
