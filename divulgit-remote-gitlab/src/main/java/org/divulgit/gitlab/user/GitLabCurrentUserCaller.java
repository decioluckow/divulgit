package org.divulgit.gitlab.user;

import java.util.Optional;

import org.divulgit.gitlab.GitLabURLBuilder;
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
public class GitLabCurrentUserCaller {

    @Autowired
    private RestCaller gitLabRestCaller;

    @Autowired
    private GitLabURLBuilder urlBuilder;
    
    @Autowired
    private UserResponseHandler responseHandler;

    public Optional<RemoteUser> retrieveCurrentUser(Remote remote, Authentication authentication) throws RemoteException {
        String url = urlBuilder.buildUserURL(remote);
        ResponseEntity<String> response = gitLabRestCaller.call(url, authentication);
        Optional<RemoteUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = Optional.ofNullable((RemoteUser) responseHandler.handle200Response(response));
        }
        return authenticatedUser;
    }
}
