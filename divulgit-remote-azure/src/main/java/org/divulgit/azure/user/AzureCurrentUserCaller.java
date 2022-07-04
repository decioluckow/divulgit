package org.divulgit.azure.user;

import java.util.Optional;

import org.divulgit.annotation.ForRemote;
import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AzureCurrentUserCaller {

    @Autowired
    private HeaderAuthRestCaller azureRestCaller;

    @Autowired
    private AzureURLBuilder urlBuilder;
    
    @Autowired
    private AzureUserResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.AZURE)
    private ErrorResponseHandler errorResponseHandler;

    public Optional<RemoteUser> retrieveCurrentUser(String token) throws RemoteException {
        String url = urlBuilder.buildUserURL();
        ResponseEntity<String> response = azureRestCaller.call(url, token);
        Optional<RemoteUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = Optional.ofNullable(responseHandler.handle200Response(response));
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        return authenticatedUser;
    }
}
