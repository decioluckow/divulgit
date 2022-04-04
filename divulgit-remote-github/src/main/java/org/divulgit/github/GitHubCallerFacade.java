package org.divulgit.github;

import org.divulgit.annotation.ForRemote;
import org.divulgit.github.project.RepositoryCaller;
import org.divulgit.github.user.CurrentUserCaller;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.RemoteFacade;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteComment;
import org.divulgit.remote.model.RemoteMergeRequest;
import org.divulgit.remote.model.RemoteProject;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ForRemote(RemoteType.GITHUB)
public class GitHubCallerFacade implements RemoteFacade {

    @Autowired
    private CurrentUserCaller currentUserCaller;

    @Autowired
    private RepositoryCaller projectCaller;

    @Override
    public Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException {
        return currentUserCaller.retrieveCurrentUser(remote, token);
    }

    @Override
    public List<? extends RemoteProject> retrieveRemoteProjects(Remote remote, String token) throws RemoteException {
        return projectCaller.retrieveRepositories(remote, token);
    }
   
}
