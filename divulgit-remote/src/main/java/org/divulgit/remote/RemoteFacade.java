package org.divulgit.remote;

import org.divulgit.remote.model.RemoteComment;
import org.divulgit.remote.model.RemoteMergeRequest;
import org.divulgit.remote.model.RemoteProject;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;

import java.util.List;
import java.util.Optional;

public interface RemoteFacade {

    Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException;
    
    List<? extends RemoteProject> retrieveRemoteProjects(Remote remote, String token) throws RemoteException;
    
    List<? extends RemoteMergeRequest> retrieveMergeRequests(Remote remote, User user, Project project, Integer scanFrom, String token) throws RemoteException;
    
    List<? extends RemoteComment> retrieveComments(Remote remote, User user, Project project, MergeRequest mergeRequest, String token) throws RemoteException;
}
