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

        AzurePullRequest azurePullRequest0 = azurePullRequests.get(0);
        assertEquals(3, azurePullRequest0.getExternalId());
        assertEquals("Updated README.md", azurePullRequest0.getDescription());
        assertEquals("Updated README.md", azurePullRequest0.getTitle());
        assertEquals("decioluckow@outlook.com", azurePullRequest0.getAuthor());
        assertEquals("OPENED", azurePullRequest0.getState());

        AzurePullRequest azurePullRequest1 = azurePullRequests.get(1);
        assertEquals(2, azurePullRequest1.getExternalId());
        assertEquals("Updated README.md", azurePullRequest1.getDescription());
        assertEquals("Updated README.md", azurePullRequest1.getTitle());
        assertEquals("decioluckow@outlook.com", azurePullRequest1.getAuthor());
        assertEquals("OPENED", azurePullRequest1.getState());

        AzurePullRequest azurePullRequest2 = azurePullRequests.get(2);
        assertEquals(1, azurePullRequest2.getExternalId());
        assertEquals("[1] description do pull request", azurePullRequest2.getDescription());
        assertEquals("teste1 1'", azurePullRequest2.getTitle());
        assertEquals("decioluckow@outlook.com", azurePullRequest2.getAuthor());
        assertEquals("OPENED", azurePullRequest2.getState());
    }
}
