package org.divulgit.github.comment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

@Service
public class GitHubCommentService {
	
	@Autowired
	private PullRequestCommentCaller pullRequestCommentCaller;
	
	@Autowired
	private IssueCommentCaller issueCommentCaller;
	
    public List<? extends RemoteComment> retrieveComments(
			Remote remote, User user, Project project, MergeRequest mergeRequest, Authentication authentication)
			throws RemoteException {
    	List<GitHubComment> pullRequestComments = pullRequestCommentCaller.retrieveComments(
				remote, user, project, mergeRequest, authentication);
    	List<GitHubComment> issueComments = issueCommentCaller.retrieveComments(
				remote, user, project, mergeRequest, authentication);
    	List<GitHubComment> comments = new ArrayList<>();
    	comments.addAll(pullRequestComments);
    	comments.addAll(issueComments);
		comments.stream().forEach(c -> c.setUrl(fixCommentURL(c.getUrl())));
    	return comments;
    }

	private String fixCommentURL(String url) {
		return StringUtils.isNotBlank(url) ? url.replace("#discussion_r","/files#r") : url;
	}
}
