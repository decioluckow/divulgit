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

    //TODO avaliar esse método pois acho que não vai rolar pois o message está como subnível de error no json de erros
    @Override
    public boolean isErrorResponse(ResponseEntity<String> response) {
        return ! response.getStatusCode().is2xxSuccessful();
    }

    @Override
    //TODO fazer tratasmento para casos onde nao retorna Json com detalhes do erro
    public void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        try {
            final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            final ObjectMapper objectMapper = builder.build();
            ErrorMessage errorMessage =  objectMapper.readValue(response.getBody(), ErrorMessage.class);
            //TODO pode ser que eu nao tenha a resposta do erro no Json, avaliar ver se tem ou nao, pode ysar o status code como auxilio
            throw new RemoteException(errorMessage.getError().getMessage());
        } catch (JsonProcessingException e) {
            String message = "Error on converting json to Object";
            log.error(message + "[json: " + response.getBody() +"]");
            throw new RemoteException(message, e);
        }
    }
}
