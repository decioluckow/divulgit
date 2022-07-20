package bitbucket.pullrequest;
import bitbucket.BitBucketURLBuilder;
import bitbucket.util.LinkHeaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PullRequestsCaller {

    @Autowired
    private HeaderAuthRestCaller bitBucketRestCaller;

    @Autowired
    private PullRequestResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.BITBUCKET)
    private ErrorResponseHandler errorResponseHandler;

    @Autowired
    private BitBucketURLBuilder urlBuilder;

    public List<BitBucketPullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            List<Integer> externalIds,
            String token) throws RemoteException {
    	log.info("Retrieving pull requests for {} ids", externalIds.size());
        final List<BitBucketPullRequest> pullRequests = new ArrayList<>();
        for (Integer id : externalIds) {
        	pullRequests.add(retrievePullRequest(remote, user, project, id, token));
        }
        return pullRequests;
    }

    public List<BitBucketPullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            Integer scanFrom,
            String token) throws RemoteException {
    	log.info("Retrieving pull requests from number {}", scanFrom);
        final List<BitBucketPullRequest> pullRequests = new ArrayList<>();
        retrievePullRequests(remote, user, project, pullRequests, scanFrom, token, BitBucketURLBuilder.INITIAL_PAGE);
        return pullRequests;
    }

    private void retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            List<BitBucketPullRequest> loadedPullRequests,
            Integer scanFrom,
            String token,
            int page) throws RemoteException {
        final String url = urlBuilder.buildPullRequestsURL(remote, user, project, page);
        ResponseEntity<String> response = bitBucketRestCaller.call(url, token);
        boolean stopScan = false;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<BitBucketPullRequest> pullRequests = responseHandler.handle200ResponseMultipleResult(response);
            for (BitBucketPullRequest pullRequest : pullRequests) {
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
        	retrievePullRequests(remote, user, project, loadedPullRequests, scanFrom, token, ++page);
        }
    }

    private BitBucketPullRequest retrievePullRequest(
            Remote remote,
            User user,
            Project project,
            Integer externalId,
            String token) throws RemoteException {
        String url = urlBuilder.buildPullRequestURL(remote, user, project, externalId);
        ResponseEntity<String> response = bitBucketRestCaller.call(url, token);
        BitBucketPullRequest pullRequest = null;
        if (response.getStatusCode().is2xxSuccessful()) {
        	pullRequest = responseHandler.handle200ResponseSingleResult(response);
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        return pullRequest;
    }
}
