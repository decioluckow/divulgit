package org.divulgit.bitbucket.user;
import org.divulgit.bitbucket.BitBucketURLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteUser;
import org.divulgit.remote.rest.RestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Optional;
@Slf4j
@Component
public class BitBucketCurrentUserCaller {

    @Autowired
    private RestCaller bitBucketRestCaller;

    @Autowired
    private BitBucketURLBuilder urlBuilder;
    
    @Autowired
    private BitBucketUserResponseHandler bitBucketUserResponseHandler;


    public Optional<RemoteUser> retrieveCurrentUser(Remote remote, Authentication authentication) throws RemoteException {
        String url = urlBuilder.buildUserURL(remote);
        ResponseEntity<String> response = bitBucketRestCaller.call(url, authentication);
        Optional<RemoteUser> authenticatedUser = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            authenticatedUser = Optional.ofNullable(bitBucketUserResponseHandler.handle200ResponseSingleResult(response));
        }
        return authenticatedUser;
    }
}
