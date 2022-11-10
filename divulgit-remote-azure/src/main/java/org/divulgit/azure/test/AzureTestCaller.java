package org.divulgit.azure.test;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.azure.AzureURLBuilder;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AzureTestCaller {

    @Autowired
    private RestCaller azureRestCaller;

    @Autowired
    private AzureURLBuilder urlBuilder;
    
    public boolean test(Authentication authentication) throws RemoteException {
        final String url = urlBuilder.buildTestURL();
        ResponseEntity<String> response = azureRestCaller.call(url, authentication);
        return response.getStatusCode().is2xxSuccessful();
    }
}
