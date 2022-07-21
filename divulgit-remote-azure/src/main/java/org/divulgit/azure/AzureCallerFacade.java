package org.divulgit.azure;

import java.util.List;
import java.util.Optional;

import org.divulgit.annotation.ForRemote;
import org.divulgit.azure.comment.GitHubCommentService;
import org.divulgit.azure.repository.RepositoryCaller;
import org.divulgit.azure.pullrequest.LastPullRequestCaller;
import org.divulgit.azure.pullrequest.PullRequestsCaller;
import org.divulgit.azure.test.AzureTestCaller;
import org.divulgit.azure.user.AzureCurrentUserCaller;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@ForRemote(RemoteType.AZURE)
public class AzureCallerFacade implements RemoteFacade {

    @Autowired
    private AzureTestCaller testCaller;

    @Autowired
    private AzureCurrentUserCaller currentUserCaller;

    @Autowired
    private RepositoryCaller projectCaller;

    @Autowired
    private LastPullRequestCaller lastPullRequestCaller;

    @Autowired
    private PullRequestsCaller pullRequestCaller;
    
    @Autowired
    private GitHubCommentService commentService;

    @Override
    public boolean testAPI(Remote remote, Authentication authentication) throws RemoteException {
        return testCaller.test(authentication);
    }

    @Override
    public Optional<RemoteUser> retrieveRemoteUser(Remote remote, Authentication authentication) throws RemoteException {
        return currentUserCaller.retrieveCurrentUser(authentication);
    }

    @Override
    public List<? extends RemoteProject> retrieveRemoteProjects(Remote remote, Authentication authentication) throws RemoteException {
        return projectCaller.retrieveRepositories(remote, authentication);
    }

    @Override
    public int retrieveLastMergeRequestExternalId(Remote remote, User user, Project project, Authentication authentication) throws RemoteException {
        return lastPullRequestCaller.retrieveLastPullRequestExternalId(remote, user, project, authentication);
    }


    @Override
    public List<? extends RemoteMergeRequest> retrieveMergeRequests(Remote remote, User user, Project project, Integer scanFrom,
                                                                    Authentication authentication) throws RemoteException {
    	return pullRequestCaller.retrievePullRequests(remote, user, project, scanFrom, authentication);
    }
    
    @Override
    public List<? extends RemoteComment> retrieveComments(Remote remote, User user, Project project, MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
    	return commentService.retrieveComments(remote, user, project, mergeRequest, authentication);
    }
}
