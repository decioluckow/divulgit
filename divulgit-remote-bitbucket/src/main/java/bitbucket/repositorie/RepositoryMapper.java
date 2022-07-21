package bitbucket.repositorie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class RepositoryMapper {
    public List<BitBucketRepository> convertToProjects(String json) throws JsonProcessingException {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        final ObjectMapper objectMapper = builder.build();
        final CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, BitBucketRepository.class);
        return objectMapper.readValue(json, collectionType);
    }
}
