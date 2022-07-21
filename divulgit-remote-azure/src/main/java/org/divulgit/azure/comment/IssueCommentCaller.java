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
public class IssueCommentCaller {

    @Autowired
    private HeaderAuthRestCaller azureRestCaller;

    @Autowired
    private AzureCommentResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.AZURE)
    private ErrorResponseHandler errorResponseHandler;

    @Autowired
    private AzureURLBuilder urlBuilder;

    public List<AzureComment> retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        final List<AzureComment> comments = new ArrayList<>();
        retrieveComments(remote, user, project, mergeRequest, comments, authentication, AzureURLBuilder.INITIAL_PAGE);
        return comments;
    }

    private void retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            List<AzureComment> loadedComments,
            Authentication authentication,
            int page) throws RemoteException {
        final String url = urlBuilder.buildIssueComment(remote, user, project, mergeRequest, page);
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
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
