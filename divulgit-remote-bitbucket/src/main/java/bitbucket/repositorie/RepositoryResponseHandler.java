package bitbucket.repositorie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class RepositoryResponseHandler {
	
    @Autowired
    private RepositoryMapper mapper;

    //TODO avaliar esse métod, mapper já vi que faz a mesma coisa dos dois método abaixo
    public List<BitBucketRepository> handle200Response(ResponseEntity<String> response) throws RemoteException {
        try {
            return mapper.convertToProjects(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }

    public BitBucketRepository handle200ResponseSingleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            ObjectMapper objectMapper = builder.build();
            return objectMapper.readValue(response.getBody(), BitBucketRepository.class);
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }

    public List<BitBucketRepository> handle200ResponseMultipleResult(ResponseEntity<String> response) throws RemoteException {
        try {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            final ObjectMapper objectMapper = builder.build();
            final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, BitBucketRepository.class);
            return objectMapper.readValue(JSONUtil.extractContent("values",response.getBody()), collectionType);
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() + "]");
            throw new RemoteException(message, e);
        }
    }
}
