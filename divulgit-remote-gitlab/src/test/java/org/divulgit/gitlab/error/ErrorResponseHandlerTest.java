package org.divulgit.gitlab.error;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.rest.error.ErrorResponseHandler;
import org.divulgit.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseHandlerTest {

    @Test
    void testError() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "error.json");
        ErrorResponseHandler errorHandler = new GitLabErrorResponseHandler();

        try {
            errorHandler.handleErrorResponse(ResponseEntity.ok(json));
        } catch (RemoteException e) {
            assertEquals("[insufficient_scope] The request requires higher privileges than provided by the access token.", e.getMessage());
        }
    }
}
