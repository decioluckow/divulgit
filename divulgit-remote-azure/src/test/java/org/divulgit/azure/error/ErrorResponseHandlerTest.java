package org.divulgit.azure.error;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseHandlerTest {

    @Test
    void testError() throws IOException, RemoteException {
        InputStream jsonResource = this.getClass().getResourceAsStream("error.json");
        String json = new String(jsonResource.readAllBytes(), StandardCharsets.UTF_8);
        ErrorResponseHandler errorHandler = new GitHubErrorResponseHandler();

        try {
            errorHandler.handleErrorResponse(ResponseEntity.ok(json));
        } catch (RemoteException e) {
            assertEquals("Bad credentials", e.getMessage());
        }
    }
}