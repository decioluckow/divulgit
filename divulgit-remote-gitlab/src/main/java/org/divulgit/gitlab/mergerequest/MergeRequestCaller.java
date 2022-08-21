package org.divulgit.gitlab.mergerequest;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.gitlab.util.LinkHeaderUtil;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class MergeRequestCaller {

    @Autowired
    private HeaderAuthRestCaller gitLabRestCaller;

    @Autowired
    private GitLabURLBuilder urlBuilder;

    @Autowired
    private MergeRequestResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.GITLAB)
    private ErrorResponseHandler errorResponseHandler;

    public List<GitLabMergeRequest> retrieveMergeRequests(
            Remote remote,
            Project project,
            List<Integer> requestedMergeRequestExternalIds,
            Authentication authentication) throws RemoteException {
        final List<GitLabMergeRequest> mergeRequests = new ArrayList<>();
        Integer emptyScanFrom = 0;
        retrieveMergeRequests(remote, project, mergeRequests, requestedMergeRequestExternalIds, emptyScanFrom, authentication, GitLabURLBuilder.INITIAL_PAGE);
        return mergeRequests;
    }

    public List<GitLabMergeRequest> retrieveMergeRequests(
            Remote remote,
            Project project,
            Integer scanFrom,
            Authentication authentication) throws RemoteException {
        final List<GitLabMergeRequest> mergeRequests = new ArrayList<>();
        List<Integer> emptyRequestedMergeRequestExternalIds = Collections.emptyList();
        retrieveMergeRequests(remote, project, mergeRequests, emptyRequestedMergeRequestExternalIds, scanFrom, authentication, GitLabURLBuilder.INITIAL_PAGE);
        return mergeRequests;
    }

    private void retrieveMergeRequests(
            Remote remote,
            Project project,
            List<GitLabMergeRequest> loadedMergeRequests,
            List<Integer> requestedMergeRequestExternalIds,
            Integer scanFrom,
            Authentication authentication,
            int page) throws RemoteException {
        String url = urlBuilder.buildMergeRequestURL(remote, project, requestedMergeRequestExternalIds, page);
        ResponseEntity<String> response = gitLabRestCaller.call(url, authentication);
        boolean stopScan = false;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<GitLabMergeRequest> remoteMergeRequests = responseHandler.handle200ResponseMultipleResult(response);
            for (GitLabMergeRequest remoteMergeRequest : remoteMergeRequests) {
                if (remoteMergeRequest.getExternalId() >= scanFrom) {
                    loadedMergeRequests.add(remoteMergeRequest);
                } else {
                    stopScan = true;
                }
            }
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response) && !stopScan) {
            retrieveMergeRequests(remote, project, loadedMergeRequests, requestedMergeRequestExternalIds, scanFrom, authentication, ++page);
        }
    }
}
