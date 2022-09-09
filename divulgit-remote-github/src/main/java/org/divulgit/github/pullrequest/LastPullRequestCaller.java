package org.divulgit.github.pullrequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LastPullRequestCaller {

    @Autowired
    private RestCaller gitHubRestCaller;

    @Autowired
    private PullRequestResponseHandler responseHandler;

    @Autowired
    private GitHubURLBuilder urlBuilder;

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
            List<GitHubPullRequest> pullRequests = responseHandler.handle200ResponseMultipleResult(response);
            if (CollectionUtils.isNotEmpty(pullRequests)) {
                lastMergeRequestId = pullRequests.get(0).getExternalId();
            }
        }
        return lastMergeRequestId;
    }
}
