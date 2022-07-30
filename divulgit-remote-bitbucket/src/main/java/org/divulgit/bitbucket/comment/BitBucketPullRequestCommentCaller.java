package org.divulgit.bitbucket.comment;
import org.divulgit.bitbucket.BitBucketURLBuilder;
import org.divulgit.bitbucket.util.LinkHeaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.model.MergeRequest;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@ForRemote(RemoteType.BITBUCKET)
public class BitBucketPullRequestCommentCaller {

    @Autowired
    private BitBucketURLBuilder urlBuilder;

    @Autowired
    private HeaderAuthRestCaller bitBucketRestCaller;

    @Autowired
    private BitBucketCommentResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.BITBUCKET)
    private ErrorResponseHandler errorResponseHandler;

    public List<BitBucketComment> retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        final List<BitBucketComment> loadedComments = new ArrayList<>();
        retrieveComments(remote, user, project, mergeRequest, loadedComments, authentication, BitBucketURLBuilder.INITIAL_PAGE);
        return loadedComments;
    }

    private void retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            List<BitBucketComment> loadedComments,
            Authentication authentication,
            int page) throws RemoteException {
        String url = urlBuilder.buildPullRequestComment(remote, user, project, mergeRequest, page);
        ResponseEntity<String> response = bitBucketRestCaller.call(url, authentication);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<BitBucketComment> comments = responseHandler.handle200ResponseMultipleResult(response);
            loadedComments.addAll(comments);
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveComments(remote, user, project, mergeRequest, loadedComments, authentication, ++page);
        }
    }
}
