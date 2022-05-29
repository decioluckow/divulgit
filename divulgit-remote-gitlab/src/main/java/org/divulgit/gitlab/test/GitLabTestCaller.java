package org.divulgit.gitlab.test;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GitLabTestCaller {

    @Autowired
    private HeaderAuthRestCaller gitLabRestCaller;

    @Autowired
    private GitLabURLBuilder urlBuilder;

    public boolean test(Remote remote, String token) throws RemoteException {
        String url = urlBuilder.buildTestURL(remote);
        ResponseEntity<String> response = gitLabRestCaller.call(url, token);
        return response.getStatusCode().is2xxSuccessful();
    }
}
