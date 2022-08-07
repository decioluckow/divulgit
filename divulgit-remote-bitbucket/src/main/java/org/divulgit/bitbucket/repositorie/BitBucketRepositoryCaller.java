package org.divulgit.bitbucket.repositorie;
import org.apache.commons.lang3.StringUtils;
import org.divulgit.bitbucket.BitBucketURLBuilder;
import org.divulgit.bitbucket.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.RestCaller;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class BitBucketRepositoryCaller {

    @Autowired
    private RestCaller bitBucketRestCaller;

    @Autowired
    private BitBucketURLBuilder urlBuilder;
    
    @Autowired
    @ForRemote(RemoteType.BITBUCKET)
    private ErrorResponseHandler errorResponseHandler;
    
    @Autowired
    private BitBucketRepositoryResponseHandler bitBucketRepositoryResponseHandler;

    public List<BitBucketRepository> retrieveRepositories(final Remote remote, final Authentication authentication) throws RemoteException {
        final List<BitBucketRepository> projects = new ArrayList<>();
        String url = urlBuilder.buildRepository(remote, authentication.getPrincipal().toString());
        retrieveRepositories(authentication, projects, url);
        return projects;
    }

    private void retrieveRepositories(final Authentication authentication, final List<BitBucketRepository> projects, String url) throws RemoteException {
      ResponseEntity<String> response = bitBucketRestCaller.call(url, authentication);
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(bitBucketRepositoryResponseHandler.handle200ResponseMultipleResult(response));
        }
        if(ResponseUtil.hasNextPage(response)){
            retrieveRepositories(authentication, projects, ResponseUtil.getNextPage(response));
        }
    }
}
