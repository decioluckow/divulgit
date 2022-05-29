package org.divulgit.github.user;

import java.util.Optional;

import org.divulgit.annotation.ForRemote;
import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GitHubCurrentUserCaller {

    @Autowired
    private HeaderAuthRestCaller gitHubRestCaller;

    @Autowired
    private GitHubURLBuilder urlBuilder;
    
    @Autowired
    private GitHubUserResponseHandler responseHandler;

    @Autowired
    @ForRemote(RemoteType.GITHUB)
    private ErrorResponseHandler errorResponseHandler;

    public Optional<RemoteUser> retrieveCurrentUser(Remote remote, String token) throws RemoteException {
        String url = urlBuilder.buildUserURL(remote);
        ResponseEntity<String> response = gitHubRestCaller.call(url, token);
        Optional<RemoteUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = Optional.ofNullable(responseHandler.handle200Response(response));
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        return authenticatedUser;
    }
}
