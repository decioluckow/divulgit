package org.divulgit.github.pullrequest;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.github.util.LinkHeaderUtil;
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
public class PullRequestsCaller {

    @Autowired
    private RestCaller gitHubRestCaller;

    @Autowired
    private PullRequestResponseHandler responseHandler;

    @Autowired
    private GitHubURLBuilder urlBuilder;

    public List<GitHubPullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            List<Integer> externalIds,
            Authentication authentication) throws RemoteException {
        log.info("Retrieving pull requests for {} ids", externalIds.size());
        final List<GitHubPullRequest> pullRequests = new ArrayList<>();
        for (Integer id : externalIds) {
            pullRequests.add(retrievePullRequest(remote, user, project, id, authentication));
        }
        return pullRequests;
    }

    public List<GitHubPullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            Integer scanFrom,
            Authentication authentication) throws RemoteException {
    	log.info("Retrieving pull requests from number {}", scanFrom);
        final List<GitHubPullRequest> pullRequests = new ArrayList<>();
        retrievePullRequests(remote, user, project, pullRequests, scanFrom, authentication, GitHubURLBuilder.INITIAL_PAGE);
        return pullRequests;
    }

    private void retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            List<GitHubPullRequest> loadedPullRequests,
            Integer scanFrom,
            Authentication authentication,
            int page) throws RemoteException {
        final String url = urlBuilder.buildPullRequestsURL(remote, user, project, page);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
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
        }
        if (LinkHeaderUtil.hasNextPage(response) && !stopScan) {
        	retrievePullRequests(remote, user, project, loadedPullRequests, scanFrom, authentication, ++page);
        }
    }

    private GitHubPullRequest retrievePullRequest(
            Remote remote,
            User user,
            Project project,
            Integer externalId,
            Authentication authentication) throws RemoteException {
        String url = urlBuilder.buildPullRequestURL(remote, user, project, externalId);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        GitHubPullRequest pullRequest = null;
        if (response.getStatusCode().is2xxSuccessful()) {
        	pullRequest = responseHandler.handle200ResponseSingleResult(response);
        }
        return pullRequest;
    }
}
