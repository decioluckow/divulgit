package org.divulgit.github.comment;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.util.LinkHeaderUtil;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IssueCommentCaller {

    @Autowired
    private RestCaller gitHubRestCaller;

    @Autowired
    private GitHubCommentResponseHandler responseHandler;

    @Autowired
    private GitHubURLBuilder urlBuilder;

    public List<GitHubComment> retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        final List<GitHubComment> comments = new ArrayList<>();
        retrieveComments(remote, user, project, mergeRequest, comments, authentication, GitHubURLBuilder.INITIAL_PAGE);
        return comments;
    }

    private void retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            List<GitHubComment> loadedComments,
            Authentication authentication,
            int page) throws RemoteException {
        final String url = urlBuilder.buildIssueComment(remote, user, project, mergeRequest, page);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitHubComment> comments = responseHandler.handle200ResponseMultipleResult(response);
            loadedComments.addAll(comments);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveComments(remote, user, project, mergeRequest, loadedComments, authentication, ++page);
        }
    }
}
