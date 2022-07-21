package org.divulgit.azure.comment;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.annotation.ForRemote;
import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.azure.util.LinkHeaderUtil;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PullRequestCommentCaller {

	@Autowired
	private AzureURLBuilder urlBuilder;

    @Autowired
    private HeaderAuthRestCaller gitHubRestCaller;

    @Autowired
    private AzureCommentResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.GITHUB)
    private ErrorResponseHandler errorResponseHandler;

    public List<AzureComment> retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        final List<AzureComment> loadedComments = new ArrayList<>();
        retrieveComments(remote, user, project, mergeRequest, loadedComments, authentication, AzureURLBuilder.INITIAL_PAGE);
        return loadedComments;
    }

    private void retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            List<AzureComment> loadedComments,
            Authentication authentication,
            int page) throws RemoteException {
        String url = urlBuilder.buildPullRequestComment(remote, user, project, mergeRequest, page);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        if (response.getStatusCode().is2xxSuccessful()) {
            List<AzureComment> comments = responseHandler.handle200ResponseMultipleResult(response);
            loadedComments.addAll(comments);
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveComments(remote, user, project, mergeRequest, loadedComments, authentication, ++page);
        }
    }
}