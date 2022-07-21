package org.divulgit.azure.pullrequest;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
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

import java.util.List;

@Slf4j
@Component
public class LastPullRequestCaller {

    @Autowired
    private HeaderAuthRestCaller gitHubRestCaller;

    @Autowired
    private PullRequestResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.GITHUB)
    private ErrorResponseHandler errorResponseHandler;

    @Autowired
    private AzureURLBuilder urlBuilder;

    public int retrieveLastPullRequestExternalId(
            Remote remote,
            User user,
            Project project,
            Authentication authentication) throws RemoteException {
        log.info("Retrieving last pull request id for project {}", project.getId());
        String url = urlBuilder.buildPullRequestsURL(remote, user, project);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        int lastMergeRequestId = 0;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<AzurePullRequest> pullRequests = responseHandler.handle200ResponseMultipleResult(response);
            if (!pullRequests.isEmpty()) {
                lastMergeRequestId = pullRequests.get(0).getExternalId();
            }
        } else if (errorResponseHandler.isErrorResponse(response)) {
        	errorResponseHandler.handleErrorResponse(response);
        }
        return lastMergeRequestId;
    }
}
