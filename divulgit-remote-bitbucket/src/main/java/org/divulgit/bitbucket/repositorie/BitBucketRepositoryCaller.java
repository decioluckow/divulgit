package org.divulgit.bitbucket.repositorie;
import org.divulgit.bitbucket.BitBucketURLBuilder;
import org.divulgit.bitbucket.util.LinkHeaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.HeaderAuthRestCaller;
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
        retrieveRepositories(remote, authentication, projects, BitBucketURLBuilder.INITIAL_PAGE);
        return projects;
    }

    private void retrieveRepositories(final Remote remote, final Authentication authentication, final List<BitBucketRepository> projects, int page) throws RemoteException {
        //TODO validar se authentication.getPrincipal().toString() eh de fato o workspace
        String url = urlBuilder.buildRepository(remote, authentication.getPrincipal().toString(), page);
        ResponseEntity<String> response = bitBucketRestCaller.call(url, authentication);
        //TODO validar se fica essa faixa de status code tambem
        if (response.getStatusCode().is2xxSuccessful()) {
            projects.addAll(bitBucketRepositoryResponseHandler.handle200ResponseMultipleResult(response));
        } else if (errorResponseHandler.isErrorResponse(response)) {
            errorResponseHandler.handleErrorResponse(response);
        }
        if (LinkHeaderUtil.hasNextPage(response)) {
        	retrieveRepositories(remote, authentication, projects, ++page);
        }
    }

}
