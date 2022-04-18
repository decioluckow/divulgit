package org.divulgit.github.comment;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitHubCommentService {
	
	@Autowired
	private PullRequestCommentCaller pullRequestCommentCaller;
	
	@Autowired
	private IssueCommentCaller issueCommentCaller;
	
    public List<? extends RemoteComment> retrieveComments(Remote remote, User user, Project project, MergeRequest mergeRequest,
    		String token) throws RemoteException {   	
    	List<GitHubComment> pullRequestComments = pullRequestCommentCaller.retrieveComments(remote, user, project, mergeRequest, token);
    	List<GitHubComment> issueComments = issueCommentCaller.retrieveComments(remote, user, project, mergeRequest, token);
    	List<GitHubComment> comments = new ArrayList<>();
    	comments.addAll(pullRequestComments);
    	comments.addAll(issueComments);
    	return comments;
    }
}
