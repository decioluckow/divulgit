package org.divulgit.bitbucket.error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.annotation.ForRemote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ForRemote(RemoteType.BITBUCKET)
public class BitBucketErrorResponseHandler implements ErrorResponseHandler {

    @Override
    public boolean isErrorResponse(ResponseEntity<String> response) {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            final ObjectMapper objectMapper = builder.build();
            ErrorMessage errorMessage =  objectMapper.readValue(response.getBody(), ErrorMessage.class);
            log.error(errorMessage.getError().getMessage().concat(" - ").concat(errorMessage.getError().getDetail()));
            throw new RemoteException(errorMessage.getError().getMessage());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}