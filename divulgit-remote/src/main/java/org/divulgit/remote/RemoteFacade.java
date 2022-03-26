package org.divulgit.remote;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.remote.model.RemoteUser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RemoteFacade {

    Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException;
}
