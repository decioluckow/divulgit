package org.divulgit.github.pullrequest;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.error.ErrorResponseHandler;
import org.divulgit.github.util.LinkHeaderUtil;
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
public class PullRequestsCaller {

    @Autowired
    private RestCaller restCaller;

    @Autowired
    private PullRequestResponseHandler responseHandler;

    @Autowired
    private ErrorResponseHandler errorResponseHandler;

    @Autowired
    private GitHubURLBuilder urlBuilder;

    public List<GitHubPullRequest> retrievePullRequests(
            Remote remote,
            RemoteUser user,
            Project project,
            List<Integer> externalIds,
            String token) throws RemoteException {
        final List<GitHubPullRequest> pullRequests = new ArrayList<>();
        for (Integer id : externalIds) {
        	pullRequests.add(retrievePullRequest(remote, user, project, id, token));
        }
        return pullRequests;
    }

    public List<GitHubPullRequest> retrievePullRequests(
            Remote remote,
            RemoteUser user,
            Project project,
            Integer scanFrom,
            String token) throws RemoteException {
        final List<GitHubPullRequest> pullRequests = new ArrayList<>();
        retrievePullRequests(remote, user, project, pullRequests, scanFrom, token, GitHubURLBuilder.INITIAL_PAGE);
        return pullRequests;
    }

    private void retrievePullRequests(
            Remote remote,
            RemoteUser user,
            Project project,
            List<GitHubPullRequest> loadedPullRequests,
            Integer scanFrom,
            String token,
            int page) throws RemoteException {
        final String url = urlBuilder.buildPullRequestsURL(remote, user, project, page);
        ResponseEntity<String> response = restCaller.call(url, token);
        boolean stopScan = false;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitHubPullRequest> pullRequests = responseHandler.handle200ResponseMultipleResult(response);
            for (GitHubPullRequest pullRequest : pullRequests) {
                if (pullRequest.getExternalId() >= scanFrom) {
                	loadedPullRequests.add(pullRequest);
                } else {
                    stopScan = true;
                }
            }
        } else if (response.getBody().contains("message")) {
        	errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response) && !stopScan) {
        	retrievePullRequests(remote, user, project, loadedPullRequests, scanFrom, token, ++page);
        }
    }

    private GitHubPullRequest retrievePullRequest(
            Remote remote,
            RemoteUser user,
            Project project,
            Integer externalId,
            String token) throws RemoteException {
        String url = urlBuilder.buildPullRequestURL(remote, user, project, externalId);
        ResponseEntity<String> response = restCaller.call(url, token);
        GitHubPullRequest pullRequest = null;
        if (response.getStatusCode().is2xxSuccessful()) {
        	pullRequest = responseHandler.handle200ResponseSingleResult(response);
        } else if (response.getBody().contains("message")) {
        	errorResponseHandler.handleErrorResponse(response);
        }
        return pullRequest;
    }
}
