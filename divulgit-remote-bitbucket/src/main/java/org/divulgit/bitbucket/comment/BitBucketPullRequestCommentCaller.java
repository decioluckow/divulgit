package org.divulgit.bitbucket.comment;

import org.apache.commons.lang3.StringUtils;
import org.divulgit.bitbucket.BitBucketURLBuilder;
import org.divulgit.bitbucket.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
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
    private RestCaller bitBucketRestCaller;

    @Autowired
    private BitBucketCommentResponseHandler responseHandler;

    public List<BitBucketComment> retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        final List<BitBucketComment> loadedComments = new ArrayList<>();
        String url = urlBuilder.buildPullRequestComment(remote, user, project, mergeRequest);
        retrieveComments(loadedComments, authentication, url);
        return loadedComments;
    }

    private void retrieveComments(List<BitBucketComment> loadedComments, Authentication authentication, String url) throws RemoteException {
        ResponseEntity<String> response = bitBucketRestCaller.call(url, authentication);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<BitBucketComment> comments = responseHandler.handle200ResponseMultipleResult(response);
            loadedComments.addAll(comments);
        }
        if (ResponseUtil.hasNextPage(response)) {
            retrieveComments(loadedComments, authentication, ResponseUtil.getNextPage(response));
        }
    }
}
