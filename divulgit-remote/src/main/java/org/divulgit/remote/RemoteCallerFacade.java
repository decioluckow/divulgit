package org.divulgit.remote;

import org.divulgit.remote.remote.model.RemoteUser;

import java.util.Optional;

public interface RemoteCallerFacade {

    Optional<RemoteUser> retrieveRemoteUser(String token);
}
