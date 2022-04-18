package org.divulgit.remote.gitlab;

import org.divulgit.annotation.ForRemote;
import org.divulgit.gitlab.comments.CommentCaller;
import org.divulgit.gitlab.mergerequest.MergeRequestCaller;
import org.divulgit.gitlab.project.ProjectCaller;
import org.divulgit.gitlab.user.GitLabCurrentUserCaller;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
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
@ForRemote(RemoteType.GITLAB)
public class GitLabCallerFacade implements RemoteFacade {

    @Autowired
    private GitLabCurrentUserCaller currentUserCaller;

    @Autowired
    private ProjectCaller projectCaller;
    
    @Autowired
    private MergeRequestCaller mergeRequestCaller;
    
    @Autowired
    private CommentCaller commentCaller;

    @Override
    public Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException {
        return currentUserCaller.retrieveCurrentUser(remote, token);
    }

    @Override
    public List<? extends RemoteProject> retrieveRemoteProjects(Remote remote, String token) throws RemoteException {
        return projectCaller.retrieveProjects(remote, token);
    }
    
    @Override
    public List<? extends RemoteMergeRequest> retrieveMergeRequests(Remote remote, User user, Project project, Integer scanFrom,
    		String token) throws RemoteException {
    	return mergeRequestCaller.retrieveMergeRequests(remote, project, scanFrom, token);
    }
    
    @Override
    public List<? extends RemoteComment> retrieveComments(Remote remote, User user, Project project, MergeRequest mergeRequest,
    		String token) throws RemoteException {
    	return commentCaller.retrieveComments(remote, project, mergeRequest, token);
    }
}
