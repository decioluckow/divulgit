package org.divulgit.azure.pullrequest;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.annotation.ForRemote;
import org.divulgit.azure.util.LinkHeaderUtil;
import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PullRequestsCaller {

    @Autowired
    private HeaderAuthRestCaller gitHubRestCaller;

    @Autowired
    private PullRequestResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.GITHUB)
    private ErrorResponseHandler errorResponseHandler;

    @Autowired
    private AzureURLBuilder urlBuilder;

    public List<AzurePullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            List<Integer> externalIds,
            Authentication authentication) throws RemoteException {
    	log.info("Retrieving pull requests for {} ids", externalIds.size());
        final List<AzurePullRequest> pullRequests = new ArrayList<>();
        for (Integer id : externalIds) {
        	pullRequests.add(retrievePullRequest(remote, user, project, id, authentication));
        }
        return pullRequests;
    }

    public List<AzurePullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            Integer scanFrom,
            Authentication authentication) throws RemoteException {
    	log.info("Retrieving pull requests from number {}", scanFrom);
        final List<AzurePullRequest> pullRequests = new ArrayList<>();
        retrievePullRequests(remote, user, project, pullRequests, scanFrom, authentication, AzureURLBuilder.INITIAL_PAGE);
        return pullRequests;
    }

    private void retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            List<AzurePullRequest> loadedPullRequests,
            Integer scanFrom,
            Authentication authentication,
            int page) throws RemoteException {
        final String url = urlBuilder.buildPullRequestsURL(remote, user, project, page);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        boolean stopScan = false;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<AzurePullRequest> pullRequests = responseHandler.handle200ResponseMultipleResult(response);
            for (AzurePullRequest pullRequest : pullRequests) {
                if (pullRequest.getExternalId() >= scanFrom) {
                	loadedPullRequests.add(pullRequest);
                } else {
                    stopScan = true;
                }
            }
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response) && !stopScan) {
        	retrievePullRequests(remote, user, project, loadedPullRequests, scanFrom, authentication, ++page);
        }
    }

    private AzurePullRequest retrievePullRequest(
            Remote remote,
            User user,
            Project project,
            Integer externalId,
            Authentication authentication) throws RemoteException {
        String url = urlBuilder.buildPullRequestURL(remote, user, project, externalId);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        AzurePullRequest pullRequest = null;
        if (response.getStatusCode().is2xxSuccessful()) {
        	pullRequest = responseHandler.handle200ResponseSingleResult(response);
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        return pullRequest;
    }
}