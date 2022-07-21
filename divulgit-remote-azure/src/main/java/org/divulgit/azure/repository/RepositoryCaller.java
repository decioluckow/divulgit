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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RepositoryCaller {

    @Autowired
    private HeaderAuthRestCaller azureRestCaller;

    @Autowired
    private AzureURLBuilder urlBuilder;
    
    @Autowired
    @ForRemote(RemoteType.AZURE)
    private ErrorResponseHandler errorResponseHandler;
    
    @Autowired
    private RepositoryResponseHandler responseHandler;

    public List<AzureRepository> retrieveRepositories(final Remote remote, final Authentication authentication) throws RemoteException {
        final List<AzureRepository> projects = new ArrayList<>();
        retrieveRepositories(remote, authentication, projects, AzureURLBuilder.INITIAL_PAGE);
        return projects;
    }

    private void retrieveRepositories(final Remote remote, final Authentication authentication, final List<AzureRepository> projects, int page) throws RemoteException {
        String url = urlBuilder.buildRepository(remote, page);
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            projects.addAll(responseHandler.handle200Response(response));
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
        	retrieveRepositories(remote, authentication, projects, ++page);
        }
    }
}
