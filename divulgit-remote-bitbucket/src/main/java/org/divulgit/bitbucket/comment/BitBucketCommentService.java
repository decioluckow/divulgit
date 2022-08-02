package org.divulgit.bitbucket.comment;
import org.divulgit.annotation.ForRemote;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteComment;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class BitBucketCommentService {
	
	@Autowired
	@ForRemote(RemoteType.BITBUCKET)
	private BitBucketPullRequestCommentCaller bitBucketPullRequestCommentCaller;
	


	public List<? extends RemoteComment> retrieveComments(Remote remote, User user, Project project, MergeRequest mergeRequest,
														  Authentication authentication) throws RemoteException {
    	List<BitBucketComment> pullRequestComments = bitBucketPullRequestCommentCaller.retrieveComments(remote, user, project, mergeRequest, authentication);
    	//List<BitBucketComment> issueComments = issueCommentCaller.retrieveComments(remote, user, project, mergeRequest, authentication);
    	List<BitBucketComment> comments = new ArrayList<>();
    	comments.addAll(pullRequestComments);
    	//comments.addAll(issueComments);
    	return comments;
    }
}
