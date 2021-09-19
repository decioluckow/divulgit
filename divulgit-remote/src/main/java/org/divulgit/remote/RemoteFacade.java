package org.divulgit.remote;

import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.remote.model.RemoteUser;

import java.util.Optional;

public interface RemoteCallerFacade {

    Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException;
}
