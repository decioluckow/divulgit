package org.divulgit.bitbucket.pullrequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.util.JSONUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class BitBucketPullRequestResponseHandler {

    public BitBucketPullRequest handle200ResponseSingleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            ObjectMapper objectMapper = builder.build();
            return objectMapper.readValue(response.getBody(), BitBucketPullRequest.class);
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }

    public List<BitBucketPullRequest> handle200ResponseMultipleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            final ObjectMapper objectMapper = builder.build();
            final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, BitBucketPullRequest.class);
            return objectMapper.readValue(JSONUtil.extractContent("values",response.getBody()), collectionType);
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }
}
