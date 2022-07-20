package bitbucket.test;
import bitbucket.BitBucketURLBuilder;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class BitBucketTestCaller {

    @Autowired
    private HeaderAuthRestCaller bitBucketRestCaller;

    @Autowired
    private BitBucketURLBuilder urlBuilder;
    
    public boolean test(Remote remote, String token) throws RemoteException {
        final String url = urlBuilder.buildTestURL(remote);
        ResponseEntity<String> response = bitBucketRestCaller.call(url, token);
        return response.getStatusCode().is2xxSuccessful();
    }
}
