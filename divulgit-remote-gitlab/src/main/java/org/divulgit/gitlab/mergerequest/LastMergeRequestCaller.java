package org.divulgit.gitlab.mergerequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LastMergeRequestCaller {

    @Autowired
    private RestCaller gitLabRestCaller;

    @Autowired
    private GitLabURLBuilder urlBuilder;

    @Autowired
    private MergeRequestResponseHandler responseHandler;

    public int retrieveLastMergeRequestExternalId(
            Remote remote,
            Project project,
            Authentication authentication) throws RemoteException {
        log.info("Retrieving last pull request id for project {}", project.getId());
        String url = urlBuilder.buildMergeRequestURL(remote, project);
        ResponseEntity<String> response = gitLabRestCaller.call(url, authentication);
        int lastMergeRequestId = 0;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitLabMergeRequest> remoteMergeRequests = responseHandler.handle200ResponseMultipleResult(response);
            if (CollectionUtils.isNotEmpty(remoteMergeRequests)) {
                lastMergeRequestId = remoteMergeRequests.get(0).getExternalId();
            }
        }
        return lastMergeRequestId;
    }
}
