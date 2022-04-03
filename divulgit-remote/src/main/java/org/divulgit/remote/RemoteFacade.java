package org.divulgit.remote;

import org.divulgit.remote.model.RemoteProject;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;

import java.util.List;
import java.util.Optional;

public interface RemoteFacade {

    Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException;
    List<? extends RemoteProject> retrieveRemoteProjects(Remote remote, String token) throws RemoteException;
}
