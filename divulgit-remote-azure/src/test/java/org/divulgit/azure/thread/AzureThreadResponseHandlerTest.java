package org.divulgit.azure.thread;

import org.divulgit.azure.repository.AzureRepository;
import org.divulgit.azure.repository.AzureRepositoryResponseHandler;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AzureThreadResponseHandlerTest {

    @Test
    public void testMultipleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        AzureThreadResponseHandler handler = new AzureThreadResponseHandler();

        List<AzureThread> azureThreads = handler.handle200ResponseMultipleResult(ResponseEntity.ok(json));

        assertEquals(5, azureThreads.size());

        Comment azureComment0 = azureThreads.get(0).getComments().get(0);
        assertEquals("1", azureComment0.getExternalId());
        assertEquals("comment 1", azureComment0.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/11/comments/1", azureComment0.getUrl());

        Comment azureComment1 = azureThreads.get(1).getComments().get(0);
        assertEquals("1", azureComment1.getExternalId());
        assertEquals("comment 2", azureComment1.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/12/comments/1", azureComment1.getUrl());

        Comment azureComment2 = azureThreads.get(2).getComments().get(0);
        assertEquals("1", azureComment2.getExternalId());
        assertEquals("comment 5", azureComment2.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/13/comments/1", azureComment2.getUrl());

        Comment azureComment3 = azureThreads.get(3).getComments().get(0);
        assertEquals("1", azureComment3.getExternalId());
        assertEquals("Décio Heinzelmann Luckow joined as a reviewer", azureComment3.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/14/comments/1", azureComment3.getUrl());

        Comment azureComment4 = azureThreads.get(4).getComments().get(0);
        assertEquals("1", azureComment4.getExternalId());
        assertEquals("Décio Heinzelmann Luckow voted 5", azureComment4.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/15/comments/1", azureComment4.getUrl());
    }
}