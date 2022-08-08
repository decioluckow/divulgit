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

    public List<AzureThread> retrieveThreads(
            Project project,
            MergeRequest mergeRequest,
            Authentication authentication) throws RemoteException {
        final List<AzureThread> loadedThreads = new ArrayList<>();
        retrieveThreads(project, mergeRequest, loadedThreads, authentication, AzureURLBuilder.INITIAL_PAGE);
        return loadedThreads;
    }

    private void retrieveThreads(
            Project project,
            MergeRequest mergeRequest,
            List<AzureThread> loadedThreads,
            Authentication authentication,
            int page) throws RemoteException {
        String organization = ((RemoteAuthentication) authentication).getUserDetails().getOrganization();
        String url = urlBuilder.buildPullRequestComments(organization, project, mergeRequest);
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            List<AzureThread> threads = responseHandler.handle200ResponseMultipleResult(response);
            loadedThreads.addAll(threads);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
            retrieveThreads(project, mergeRequest, loadedThreads, authentication, ++page);
        }
    }
}
