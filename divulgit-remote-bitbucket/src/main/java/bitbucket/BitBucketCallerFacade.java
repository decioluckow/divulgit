package bitbucket;
import bitbucket.comment.BitBucketCommentService;
import bitbucket.repositorie.RepositoryCaller;
import bitbucket.pullrequest.LastPullRequestCaller;
import bitbucket.pullrequest.PullRequestsCaller;
import bitbucket.test.BitBucketTestCaller;
import bitbucket.user.BitBucketCurrentUserCaller;
import org.divulgit.annotation.ForRemote;
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
@ForRemote(RemoteType.BITBUCKET)
public class BitBucketCallerFacade implements RemoteFacade {

    @Autowired
    private BitBucketTestCaller testCaller;

    @Autowired
    private BitBucketCurrentUserCaller currentUserCaller;

    @Autowired
    private RepositoryCaller projectCaller;

    @Autowired
    private LastPullRequestCaller lastPullRequestCaller;

    @Autowired
    private PullRequestsCaller pullRequestCaller;
    
    @Autowired
    private BitBucketCommentService commentService;

    @Override
    public boolean testAPI(Remote remote, String token) throws RemoteException {
        return testCaller.test(remote, token);
    }

    @Override
    public Optional<RemoteUser> retrieveRemoteUser(Remote remote, String token) throws RemoteException {
        return currentUserCaller.retrieveCurrentUser(remote, token);
    }

    @Override
    public List<? extends RemoteProject> retrieveRemoteProjects(Remote remote, String token) throws RemoteException {
        return projectCaller.retrieveRepositories(remote, token);
    }

    @Override
    public int retrieveLastMergeRequestExternalId(Remote remote, User user, Project project, String token) throws RemoteException {
        return lastPullRequestCaller.retrieveLastPullRequestExternalId(remote, user, project, token);
    }

    @Override
    public List<? extends RemoteMergeRequest> retrieveMergeRequests(Remote remote, User user, Project project, Integer scanFrom,
    		String token) throws RemoteException {
    	return pullRequestCaller.retrievePullRequests(remote, user, project, scanFrom, token);
    }
    
    @Override
    public List<? extends RemoteComment> retrieveComments(Remote remote, User user, Project project, MergeRequest mergeRequest,
    		String token) throws RemoteException {
    	return commentService.retrieveComments(remote, user, project, mergeRequest, token);
    }
}
