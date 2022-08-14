package org.divulgit.azure.pullrequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.divulgit.security.RemoteAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AzurePullRequestsCaller {

    @Autowired
    private RestCaller azureRestCaller;

    @Autowired
    private AzurePullRequestResponseHandler responseHandler;

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
        final String organization = ((RemoteAuthentication) authentication).getUserDetails().getOrganization();
        final String url = urlBuilder.buildPullRequestsURL(organization, project);
        retrievePullRequests(remote, user, project, pullRequests, scanFrom, authentication, url);
        return pullRequests;
    }

    private void retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            List<AzurePullRequest> loadedPullRequests,
            Integer scanFrom,
            Authentication authentication,
            String url) throws RemoteException {
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        boolean stopScan = false;
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            List<AzurePullRequest> pullRequests = responseHandler.handle200ResponseMultipleResult(response);
            for (AzurePullRequest pullRequest : pullRequests) {
                if (pullRequest.getExternalId() >= scanFrom) {
                	loadedPullRequests.add(pullRequest);
                } else {
                    stopScan = true;
                }
            }
        }
        Optional<String> continuationURL = urlBuilder.buildContinuationURL(response, url);
        if (continuationURL.isPresent() && !stopScan) {
        	retrievePullRequests(remote, user, project, loadedPullRequests, scanFrom, authentication, continuationURL.get());
        }
    }

    private AzurePullRequest retrievePullRequest(
            Remote remote,
            User user,
            Project project,
            int pullRequestExternalId,
            Authentication authentication) throws RemoteException {
        String organization = ((RemoteAuthentication) authentication).getUserDetails().getOrganization();
        String url = urlBuilder.buildPullRequestURL(organization, pullRequestExternalId);
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        AzurePullRequest pullRequest = null;
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
        	pullRequest = responseHandler.handle200ResponseSingleResult(response);
        }
        return pullRequest;
    }
}
