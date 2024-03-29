package org.divulgit.github.user;

import java.util.Optional;

import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GitHubCurrentUserCaller {

    @Autowired
    private RestCaller gitHubRestCaller;

    @Autowired
    private GitHubURLBuilder urlBuilder;
    
    @Autowired
    private GitHubUserResponseHandler responseHandler;

    public Optional<RemoteUser> retrieveCurrentUser(Remote remote, Authentication authentication) throws RemoteException {
        String url = urlBuilder.buildUserURL(remote);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        Optional<RemoteUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = Optional.ofNullable(responseHandler.handle200Response(response));
        }
        return authenticatedUser;
    }
}
