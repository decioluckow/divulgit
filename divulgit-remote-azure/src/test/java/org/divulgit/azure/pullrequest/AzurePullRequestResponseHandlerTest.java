package org.divulgit.azure.pullrequest;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AzurePullRequestResponseHandlerTest {

    @Test
    public void testMultipleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        AzurePullRequestResponseHandler handler = new AzurePullRequestResponseHandler();

        List<AzurePullRequest> azurePullRequests = handler.handle200ResponseMultipleResult(ResponseEntity.ok(json));

        //TODO continuar mapeamento

        assertEquals(3, azurePullRequests.size());
    }
}