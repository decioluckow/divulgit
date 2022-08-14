package org.divulgit.azure.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.model.Remote;
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
public class AzureRepositoryCaller {

    @Autowired
    private RestCaller azureRestCaller;

    @Autowired
    private AzureURLBuilder urlBuilder;

    @Autowired
    private AzureRepositoryResponseHandler responseHandler;

    public List<AzureRepository> retrieveRepositories(final Remote remote, final Authentication authentication) throws RemoteException {
        String organization = ((RemoteAuthentication) authentication).getUserDetails().getOrganization();
        String url = urlBuilder.buildRepository(organization);
        final List<AzureRepository> projects = new ArrayList<>();
        retrieveRepositories(remote, authentication, projects, url);
        return projects;
    }

    private void retrieveRepositories(final Remote remote, final Authentication authentication, final List<AzureRepository> projects, String url) throws RemoteException {
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            projects.addAll(responseHandler.handle200Response(response));
        }
        Optional<String> continuationURL = urlBuilder.buildContinuationURL(response, url);
        if (continuationURL.isPresent()) {
            retrieveRepositories(remote, authentication, projects, continuationURL.get());
        }
    }
}
