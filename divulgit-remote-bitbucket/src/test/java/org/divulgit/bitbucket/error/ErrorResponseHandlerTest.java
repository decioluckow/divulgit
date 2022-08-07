package org.divulgit.bitbucket.error;
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
    void testErrorResourceNotFound() throws IOException {
        InputStream jsonResource = this.getClass().getResourceAsStream("errorResourceNotFound.json");
        String json = new String(jsonResource.readAllBytes(), StandardCharsets.UTF_8);
        ErrorResponseHandler errorHandler = new BitBucketErrorResponseHandler();
        try {
            errorHandler.handleErrorResponse(ResponseEntity.ok(json));
        } catch (RemoteException e) {
            assertEquals("Resource not found", e.getMessage());
        }
    }
}