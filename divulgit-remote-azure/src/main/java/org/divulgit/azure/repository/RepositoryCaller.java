package org.divulgit.azure.repository;

import java.util.ArrayList;
import java.util.List;

import org.divulgit.annotation.ForRemote;
import org.divulgit.azure.util.LinkHeaderUtil;
import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RepositoryCaller {

    @Autowired
    private HeaderAuthRestCaller gitHubRestCaller;

    @Autowired
    private AzureURLBuilder urlBuilder;
    
    @Autowired
    @ForRemote(RemoteType.GITHUB)
    private ErrorResponseHandler errorResponseHandler;
    
    @Autowired
    private RepositoryResponseHandler responseHandler;

    public List<AzureRepository> retrieveRepositories(final Remote remote, final String token) throws RemoteException {
        final List<AzureRepository> projects = new ArrayList<>();
        retrieveRepositories(remote, token, projects, AzureURLBuilder.INITIAL_PAGE);
        return projects;
    }

    private void retrieveRepositories(final Remote remote, final String token, final List<AzureRepository> projects, int page) throws RemoteException {
        String url = urlBuilder.buildRepository(remote, page);
        ResponseEntity<String> response = gitHubRestCaller.call(url, token);
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(responseHandler.handle200Response(response));
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
        	retrieveRepositories(remote, token, projects, ++page);
        }
    }


}
