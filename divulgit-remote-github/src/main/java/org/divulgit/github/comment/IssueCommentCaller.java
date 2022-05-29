package org.divulgit.github.comment;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.annotation.ForRemote;
import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.util.LinkHeaderUtil;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IssueCommentCaller {

    @Autowired
    private HeaderAuthRestCaller gitHubRestCaller;

    @Autowired
    private GitHubCommentResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.GITHUB)
    private ErrorResponseHandler errorResponseHandler;

    @Autowired
    private GitHubURLBuilder urlBuilder;

    public List<GitHubComment> retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            String token) throws RemoteException {
        final List<GitHubComment> comments = new ArrayList<>();
        retrieveComments(remote, user, project, mergeRequest, comments, token, GitHubURLBuilder.INITIAL_PAGE);
        return comments;
    }

    private void retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            List<GitHubComment> loadedComments,
            String token,
            int page) throws RemoteException {
        final String url = urlBuilder.buildIssueComment(remote, user, project, mergeRequest, page);
        ResponseEntity<String> response = gitHubRestCaller.call(url, token);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitHubComment> comments = responseHandler.handle200ResponseMultipleResult(response);
            loadedComments.addAll(comments);
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveComments(remote, user, project, mergeRequest, loadedComments, token, ++page);
        }
    }
}
