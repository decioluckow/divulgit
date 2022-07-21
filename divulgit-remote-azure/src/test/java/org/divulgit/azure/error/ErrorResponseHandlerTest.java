package org.divulgit.azure.error;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseHandlerTest {

    @Test
    void testError() throws IOException, RemoteException {
        ErrorResponseHandler errorHandler = new AzureErrorResponseHandler();

        try {
            errorHandler.handleErrorResponse(ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build());
        } catch (RemoteException e) {
            assertEquals("203 NON_AUTHORITATIVE_INFORMATION", e.getMessage());
        }
    }
}