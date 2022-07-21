package org.divulgit.github.test;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.github.GitHubURLBuilder;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GitHubTestCaller {

    @Autowired
    private HeaderAuthRestCaller gitHubRestCaller;

    @Autowired
    private GitHubURLBuilder urlBuilder;
    
    public boolean test(Remote remote, Authentication authentication) throws RemoteException {
        final String url = urlBuilder.buildTestURL(remote);
        ResponseEntity<String> response = gitHubRestCaller.call(url, authentication);
        return response.getStatusCode().is2xxSuccessful();
    }
}
