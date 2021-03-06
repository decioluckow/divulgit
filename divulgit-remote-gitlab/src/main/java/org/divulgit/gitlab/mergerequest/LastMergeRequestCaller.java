package org.divulgit.gitlab.mergerequest;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LastMergeRequestCaller {

    @Autowired
    private HeaderAuthRestCaller gitLabRestCaller;

    @Autowired
    private GitLabURLBuilder urlBuilder;

    @Autowired
    private MergeRequestResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.GITLAB)
    private ErrorResponseHandler errorResponseHandler;

    public int retrieveLastMergeRequestExternalId(
            Remote remote,
            Project project,
            String token) throws RemoteException {
        log.info("Retrieving last pull request id for project {}", project.getId());
        String url = urlBuilder.buildMergeRequestURL(remote, project);
        ResponseEntity<String> response = gitLabRestCaller.call(url, token);
        int lastMergeRequestId = 0;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitLabMergeRequest> remoteMergeRequests = responseHandler.handle200ResponseMultipleResult(response);
            if (! remoteMergeRequests.isEmpty()) {
                lastMergeRequestId = remoteMergeRequests.get(0).getExternalId();
            }
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        return lastMergeRequestId;
    }
}
