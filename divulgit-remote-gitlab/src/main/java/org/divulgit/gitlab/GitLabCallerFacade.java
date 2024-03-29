package org.divulgit.gitlab;
import java.util.List;
import java.util.Optional;
import org.divulgit.annotation.ForRemote;
import org.divulgit.gitlab.comments.GitLabCommentService;
import org.divulgit.gitlab.mergerequest.LastMergeRequestCaller;
import org.divulgit.gitlab.mergerequest.MergeRequestCaller;
import org.divulgit.gitlab.project.ProjectCaller;
import org.divulgit.gitlab.test.GitLabTestCaller;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@ForRemote(RemoteType.GITLAB)
public class GitLabCallerFacade implements RemoteFacade {

    @Autowired
    private GitLabTestCaller testCaller;

    @Autowired
    private GitLabCurrentUserCaller currentUserCaller;

    @Autowired
    private ProjectCaller projectCaller;
    
    @Autowired
    private MergeRequestCaller mergeRequestCaller;

    @Autowired
    private LastMergeRequestCaller lastMergeRequestCaller;
    
    @Autowired
    private GitLabCommentService commentService;

    @Override
    public boolean testAPI(Remote remote, Authentication authentication) throws RemoteException {
        return testCaller.test(remote, authentication);
    }

    @Override
    public Optional<RemoteUser> retrieveRemoteUser(Remote remote, Authentication authentication) throws RemoteException {
        return currentUserCaller.retrieveCurrentUser(remote, authentication);
    }

    @Override
    public List<? extends RemoteProject> retrieveRemoteProjects(Remote remote, Authentication authentication) throws RemoteException {
        return projectCaller.retrieveProjects(remote, authentication);
    }

    @Override
    public int retrieveLastMergeRequestExternalId(Remote remote, User user, Project project, Authentication authentication) throws RemoteException {
        return lastMergeRequestCaller.retrieveLastMergeRequestExternalId(remote, project, authentication);
    }

    @Override
    public List<? extends RemoteMergeRequest> retrieveMergeRequests(Remote remote, User user, Project project, Integer scanFrom,
                                                                    Authentication authentication) throws RemoteException {
    	return mergeRequestCaller.retrieveMergeRequests(remote, project, scanFrom, authentication);
    }

    @Override
    public List<? extends RemoteMergeRequest> retrieveMergeRequests(Remote remote, User user, Project project, List<Integer> requestedMergeRequestExternalIds, Authentication authentication) throws RemoteException {
        return mergeRequestCaller.retrieveMergeRequests(remote, project, requestedMergeRequestExternalIds, authentication);
    }

    @Override
    public List<? extends RemoteComment> retrieveComments(Remote remote, User user, Project project, MergeRequest mergeRequest,
                                                          Authentication authentication) throws RemoteException {
    	return commentService.retrieveComments(remote, project, mergeRequest, authentication);
    }
}
