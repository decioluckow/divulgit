package org.divulgit.bitbucket;
import org.divulgit.bitbucket.comment.BitBucketCommentService;
import org.divulgit.bitbucket.repositorie.BitBucketRepositoryCaller;
import org.divulgit.bitbucket.pullrequest.BitBucketLastPullRequestCaller;
import org.divulgit.bitbucket.pullrequest.BitBucketPullRequestsCaller;
import org.divulgit.bitbucket.test.BitBucketTestCaller;
import org.divulgit.bitbucket.user.BitBucketCurrentUserCaller;
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
import org.springframework.security.core.Authentication;
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
    private BitBucketRepositoryCaller bitBucketRepositoryCaller;

    @Autowired
    private BitBucketLastPullRequestCaller bitBucketLastPullRequestCaller;

    @Autowired
    private BitBucketPullRequestsCaller bitBucketPullRequestsCaller;

    @Autowired
    private BitBucketCommentService commentService;

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
        return bitBucketRepositoryCaller.retrieveRepositories(remote, authentication);
    }

    @Override
    public int retrieveLastMergeRequestExternalId(Remote remote, User user, Project project, Authentication authentication) throws RemoteException {
        return bitBucketLastPullRequestCaller.retrieveLastPullRequestExternalId(remote, user, project, authentication);
    }


    @Override
    public List<? extends RemoteMergeRequest> retrieveMergeRequests(Remote remote, User user, Project project, Integer scanFrom,
                                                                    Authentication authentication) throws RemoteException {
        return bitBucketPullRequestsCaller.retrievePullRequests(remote, user, project, scanFrom, authentication);
    }

    @Override
    public List<? extends RemoteComment> retrieveComments(Remote remote, User user, Project project, MergeRequest mergeRequest,
                                                          Authentication authentication) throws RemoteException {
        return commentService.retrieveComments(remote, user, project, mergeRequest, authentication);
    }
}