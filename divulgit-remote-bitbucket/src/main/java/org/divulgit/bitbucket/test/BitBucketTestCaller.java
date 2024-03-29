package org.divulgit.bitbucket.test;
import org.divulgit.bitbucket.BitBucketURLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class BitBucketTestCaller {

    @Autowired
    private RestCaller bitBucketRestCaller;

    @Autowired
    private BitBucketURLBuilder urlBuilder;

    public boolean test(Remote remote, Authentication authentication) throws RemoteException {
        final String url = urlBuilder.buildTestURL(remote);
        ResponseEntity<String> response = bitBucketRestCaller.call(url, authentication);
        return response.getStatusCode().is2xxSuccessful();
    }
}
