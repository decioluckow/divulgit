package org.divulgit.bitbucket.error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.divulgit.annotation.ForRemote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.type.RemoteType;
import org.divulgit.util.JSONUtil;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ForRemote(RemoteType.BITBUCKET)
public class BitBucketErrorResponseHandler implements ErrorResponseHandler {

    public static final int MAX_ERROR_MESSAGE_LENGTH = 100;

    @Override
    public boolean isErrorResponse(ResponseEntity<String> response) {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleErrorResponse(ResponseEntity<String> response) throws RemoteException {
        if (JSONUtil.isValid(response.getBody()))
            handleJSONErrorResponse(response);
        else
            handlePlainErrorResponse(response);
    }

    public void handleJSONErrorResponse(ResponseEntity<String> response) throws RemoteException {
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

    public void handlePlainErrorResponse(ResponseEntity<String> response) throws RemoteException {
        String limitedMessage = StringUtils.substring(Strings.nullToEmpty(response.getBody()), 0, MAX_ERROR_MESSAGE_LENGTH);
        limitedMessage = limitedMessage.length() < MAX_ERROR_MESSAGE_LENGTH ? limitedMessage : limitedMessage.trim() + "...";
        throw new RemoteException(limitedMessage);
    }
}
