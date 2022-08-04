package org.divulgit.azure.thread;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.azure.util.LinkHeaderUtil;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.divulgit.security.RemoteAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AzureThreadCaller {

	@Autowired
	private AzureURLBuilder urlBuilder;

    @Autowired
    private RestCaller azureRestCaller;

    @Autowired
    private AzureThreadResponseHandler responseHandler;

    public List<AzureThread> retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        final List<AzureThread> loadedComments = new ArrayList<>();
        retrieveComments(remote, user, project, mergeRequest, loadedComments, authentication, AzureURLBuilder.INITIAL_PAGE);
        return loadedComments;
    }

    private void retrieveComments(
            Remote remote,
            User user,
            Project project,
            MergeRequest mergeRequest,
            List<AzureThread> loadedComments,
            Authentication authentication,
            int page) throws RemoteException {
        String organization = ((RemoteAuthentication) authentication).getUserDetails().getOrganization();
        String url = urlBuilder.buildPullRequestComments(organization, project, mergeRequest);
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            List<AzureThread> comments = responseHandler.handle200ResponseMultipleResult(response);
            //TODO testar
            comments.stream()
                    .forEach(t -> t.getComments().stream()
                            .forEach(comment -> comment.setUrl(urlBuilder.buildPullRequestCommentWebURL(project, mergeRequest, comment))));
            loadedComments.addAll(comments);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveComments(remote, user, project, mergeRequest, loadedComments, authentication, ++page);
        }
    }
}
