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

    @Test
    public void testSmallPlainError() {
        var errorHandler = new BitBucketErrorResponseHandler();

        Exception exception = assertThrows(RemoteException.class, () -> {
            errorHandler.handleErrorResponse(ResponseEntity.ok("Mussum Ipsum, cacilds vidis litro abertis."));
        });

        String expectedMessage = "Mussum Ipsum, cacilds vidis litro abertis.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testLargePlainError() {
        var errorHandler = new BitBucketErrorResponseHandler();

        Exception exception = assertThrows(RemoteException.class, () -> {
            errorHandler.handleErrorResponse(ResponseEntity.ok("Mussum Ipsum, cacilds vidis litro abertis. Mauris nec dolor in eros commodo tempor. Aenean aliquam molestie leo, vitae iaculis nisl."));
        });

        String expectedMessage = "Mussum Ipsum, cacilds vidis litro abertis. Mauris nec dolor in eros commodo tempor. Aenean aliquam m...";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }


}