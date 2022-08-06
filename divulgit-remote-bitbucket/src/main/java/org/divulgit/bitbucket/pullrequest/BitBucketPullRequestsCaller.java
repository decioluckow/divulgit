package org.divulgit.bitbucket.pullrequest;
import org.apache.commons.lang3.StringUtils;
import org.divulgit.bitbucket.BitBucketURLBuilder;
import org.divulgit.bitbucket.util.LinkHeaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BitBucketPullRequestsCaller {

    @Autowired
    private RestCaller bitBucketRestCaller;

    @Autowired
    private BitBucketPullRequestResponseHandler bitBucketPullRequestResponseHandler;

    @Autowired
    private BitBucketURLBuilder urlBuilder;

    public List<BitBucketPullRequest> retrievePullRequests(
            Remote remote,
            User user,
            Project project,
            Integer scanFrom,
            Authentication authentication) throws RemoteException {
        log.info("Retrieving pull requests from number {}", scanFrom);
        final List<BitBucketPullRequest> pullRequests = new ArrayList<>();
        final String url = urlBuilder.buildPullRequestsURL(remote, user, project);
        retrievePullRequests(pullRequests, scanFrom, authentication, url);
        return pullRequests;
    }

    private void retrievePullRequests(List<BitBucketPullRequest> loadedPullRequests,Integer scanFrom,Authentication authentication,String url) throws RemoteException {
        ResponseEntity<String> response = bitBucketRestCaller.call(url, authentication);
        boolean stopScan = false;
        if (response.getStatusCode().is2xxSuccessful()) {
            List<BitBucketPullRequest> pullRequests = bitBucketPullRequestResponseHandler.handle200ResponseMultipleResult(response);
            for (BitBucketPullRequest pullRequest : pullRequests) {
                if (pullRequest.getExternalId() >= scanFrom) {
                    loadedPullRequests.add(pullRequest);
                } else {
                    stopScan = true;
                }
            }
        }
        String nextURL = LinkHeaderUtil.getNextPage(response);
        if (StringUtils.isNotEmpty(nextURL) && ! stopScan) {
            retrievePullRequests(loadedPullRequests, scanFrom, authentication, nextURL);
        }
    }
}
