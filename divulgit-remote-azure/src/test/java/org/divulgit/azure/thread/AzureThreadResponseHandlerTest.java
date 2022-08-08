package org.divulgit.azure.thread;

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

        AzureThread azureThread0 = azureThreads.get(0);
        AzureComment azureComment0 = azureThread0.getComments().get(0);
        assertEquals("1", azureComment0.getExternalId());
        assertEquals("comment 1", azureComment0.getText());
        assertEquals(CommentType.TEXT, azureComment0.getCommentType());
        assertEquals("/teste.txt", azureThread0.getThreadContext().getFilePath());

        AzureThread azureThread1 = azureThreads.get(1);
        AzureComment azureComment1 = azureThread1.getComments().get(0);
        assertEquals("1", azureComment1.getExternalId());
        assertEquals("comment 2", azureComment1.getText());
        assertEquals(CommentType.TEXT, azureComment1.getCommentType());
        assertEquals("/teste.txt", azureThread1.getThreadContext().getFilePath());

        AzureThread azureThread2 = azureThreads.get(2);
        AzureComment azureComment2 = azureThread2.getComments().get(0);
        assertEquals("1", azureComment2.getExternalId());
        assertEquals("comment 5", azureComment2.getText());
        assertEquals(CommentType.TEXT, azureComment2.getCommentType());
        assertEquals("/teste.txt", azureThread2.getThreadContext().getFilePath());

        AzureThread azureThread3 = azureThreads.get(3);
        AzureComment azureComment3 = azureThread3.getComments().get(0);
        assertEquals("1", azureComment3.getExternalId());
        assertEquals("Décio Heinzelmann Luckow joined as a reviewer", azureComment3.getText());
        assertEquals(CommentType.SYSTEM, azureComment3.getCommentType());
        assertNull(azureThread3.getThreadContext());

        AzureThread azureThread4 = azureThreads.get(4);
        AzureComment azureComment4 = azureThread4.getComments().get(0);
        assertEquals("1", azureComment4.getExternalId());
        assertEquals("Décio Heinzelmann Luckow voted 5", azureComment4.getText());
        assertEquals(CommentType.SYSTEM, azureComment4.getCommentType());
        assertNull(azureThread4.getThreadContext());
    }
}