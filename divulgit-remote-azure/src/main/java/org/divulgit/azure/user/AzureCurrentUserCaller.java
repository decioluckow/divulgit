package org.divulgit.azure.user;

import java.util.Optional;

import org.divulgit.annotation.ForRemote;
import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.remote.rest.RestCaller;
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
public class AzureCurrentUserCaller {

    @Autowired
    private RestCaller azureRestCaller;

    @Autowired
    private AzureURLBuilder urlBuilder;
    
    @Autowired
    private AzureUserResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.AZURE)
    private ErrorResponseHandler errorResponseHandler;

    public Optional<RemoteUser> retrieveCurrentUser(Authentication authentication) throws RemoteException {
        String url = urlBuilder.buildUserURL();
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        Optional<RemoteUser> authenticatedUser = Optional.empty();
        if (response.getStatusCodeValue() == HttpStatus.OK.value()) {
            authenticatedUser = Optional.ofNullable(responseHandler.handle200Response(response));
        }
        return authenticatedUser;
    }
}
