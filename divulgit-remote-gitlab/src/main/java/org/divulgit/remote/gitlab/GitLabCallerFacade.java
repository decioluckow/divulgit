package org.divulgit.remote.gitlab;

import org.divulgit.annotation.ForRemote;
import org.divulgit.gitlab.user.CurrentUserCaller;
import org.divulgit.model.Remote;
import org.divulgit.remote.RemoteCallerFacade;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.remote.model.RemoteUser;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ForRemote(RemoteType.GITLAB)
public class GitLabCallerFacade implements RemoteCallerFacade {

    @Autowired
    private CurrentUserCaller caller;

    @Override
    public Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException {
        return caller.retrieveCurrentUser(remote, token);
    }
}
