package org.divulgit.remote.gitlab;

import org.divulgit.annotation.ForRemote;
import org.divulgit.gitlab.project.ProjectCaller;
import org.divulgit.gitlab.user.CurrentUserCaller;
import org.divulgit.model.Remote;
import org.divulgit.remote.RemoteFacade;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteProject;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ForRemote(RemoteType.GITLAB)
public class GitLabCallerFacade implements RemoteFacade {

    @Autowired
    private CurrentUserCaller currentUserCaller;

    @Autowired
    private ProjectCaller projectCaller;

    @Override
    public Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException {
        return currentUserCaller.retrieveCurrentUser(remote, token);
    }

    @Override
    public List<? extends RemoteProject> retrieveRemoteProjects(Remote remote, String token) throws RemoteException {
        return projectCaller.retrieveProjects(remote, token);
    }
}
