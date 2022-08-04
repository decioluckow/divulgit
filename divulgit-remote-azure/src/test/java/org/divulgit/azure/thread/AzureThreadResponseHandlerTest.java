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

        Comment azureThread0 = azureThreads.get(0).getComments().get(0);
        assertEquals("1", azureThread0.getExternalId());
        assertEquals("comment 1", azureThread0.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/11/comments/1", azureThread0.getUrl());

        Comment azureThread1 = azureThreads.get(1).getComments().get(0);
        assertEquals("1", azureThread1.getExternalId());
        assertEquals("comment 2", azureThread1.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/12/comments/1", azureThread1.getUrl());

        Comment azureThread2 = azureThreads.get(2).getComments().get(0);
        assertEquals("1", azureThread2.getExternalId());
        assertEquals("comment 5", azureThread2.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/13/comments/1", azureThread2.getUrl());

        Comment azureThread3 = azureThreads.get(3).getComments().get(0);
        assertEquals("1", azureThread3.getExternalId());
        assertEquals("Décio Heinzelmann Luckow joined as a reviewer", azureThread3.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/14/comments/1", azureThread3.getUrl());

        Comment azureThread4 = azureThreads.get(4).getComments().get(0);
        assertEquals("1", azureThread4.getExternalId());
        assertEquals("Décio Heinzelmann Luckow voted 5", azureThread4.getText());
        assertEquals("https://dev.azure.com/decioluckow-teste/_apis/git/repositories/e81d5900-0c3a-4685-9529-abef68c973a9/pullRequests/4/threads/15/comments/1", azureThread4.getUrl());
    }
}