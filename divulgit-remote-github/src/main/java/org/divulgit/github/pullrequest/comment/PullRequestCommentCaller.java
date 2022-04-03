package org.divulgit.github.pullrequest.comment;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.error.ErrorResponseHandler;
import org.divulgit.github.util.LinkHeaderUtil;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PullRequestCommentCaller {

	@Autowired
	private GitHubURLBuilder urlBuilder;
	
    @Autowired
    private RestCaller restCaller;

    @Autowired
    private PullRequestCommentResponseHandler responseHandler;

    @Autowired
    private ErrorResponseHandler errorResponseHandler;

    public List<GitHubPullRequestComment> retrieveComments(
            Remote remote,
            RemoteUser user,
            Project project,
            MergeRequest mergeRequest,
            String token) throws RemoteException {
        final List<GitHubPullRequestComment> loadedComments = new ArrayList<>();
        retrieveComments(remote, user, project, mergeRequest, loadedComments, token, GitHubURLBuilder.INITIAL_PAGE);
        return loadedComments;
    }

    private void retrieveComments(
            Remote remote,
            RemoteUser user,
            Project project,
            MergeRequest mergeRequest,
            List<GitHubPullRequestComment> loadedComments,
            String token,
            int page) throws RemoteException {
        String url = urlBuilder.buildPullRequestComment(remote, user, project, mergeRequest, page);
        ResponseEntity<String> response = restCaller.call(url, token);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitHubPullRequestComment> comments = responseHandler.handle200ResponseMultipleResult(response);
            loadedComments.addAll(comments);
        } else if (response.getBody().contains("message")) {
        	errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveComments(remote, user, project, mergeRequest, loadedComments, token, ++page);
        }
    }


}
