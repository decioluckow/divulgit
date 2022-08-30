package org.divulgit.azure.pullrequest;

import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Slf4j
@Component
public class AzureLastPullRequestCaller {

    @Autowired
    private RestCaller azureRestCaller;

    @Autowired
    private AzurePullRequestResponseHandler responseHandler;

    @Autowired
    private AzureURLBuilder urlBuilder;

    public int retrieveLastPullRequestExternalId(
            Remote remote,
            User user,
            Project project,
            Authentication authentication) throws RemoteException {
        log.info("Retrieving last pull request id for project {}", project.getId());
        String organization = ((RemoteAuthentication) authentication).getUserDetails().getOrganization();
        String url = urlBuilder.buildPullRequestsURL(organization, project);
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        int lastMergeRequestId = 0;
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            List<AzurePullRequest> pullRequests = responseHandler.handle200ResponseMultipleResult(response);
            if (!pullRequests.isEmpty()) {
                lastMergeRequestId = pullRequests.get(0).getExternalId();
            }
        }
        return lastMergeRequestId;
    }
}
