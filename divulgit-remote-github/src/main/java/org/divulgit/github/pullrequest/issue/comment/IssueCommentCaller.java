package org.divulgit.github.pullrequest.issue.comment;

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
public class IssueCommentCaller {

    @Autowired
    private RestCaller restCaller;

    @Autowired
    private IssueCommentResponseHandler responseHandler;

    @Autowired
    private ErrorResponseHandler errorResponseHandler;

    @Autowired
    private GitHubURLBuilder urlBuilder;

    public List<GitHubIssueComment> retrieveComments(
            Remote remote,
            RemoteUser user,
            Project project,
            MergeRequest mergeRequest,
            String token) throws RemoteException {
        final List<GitHubIssueComment> comments = new ArrayList<>();
        retrieveComments(remote, user, project, mergeRequest, comments, token, GitHubURLBuilder.INITIAL_PAGE);
        return comments;
    }

    private void retrieveComments(
            Remote remote,
            RemoteUser user,
            Project project,
            MergeRequest mergeRequest,
            List<GitHubIssueComment> loadedComments,
            String token,
            int page) throws RemoteException {
        final String url = urlBuilder.buildIssueComment(remote, user, project, mergeRequest, page);
        ResponseEntity<String> response = restCaller.call(url, token);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitHubIssueComment> comments = responseHandler.handle200ResponseMultipleResult(response);
            loadedComments.addAll(comments);
        } else if (response.getBody().contains("message")) {
        	errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveComments(remote, user, project, mergeRequest, loadedComments, token, ++page);
        }
    }

}
