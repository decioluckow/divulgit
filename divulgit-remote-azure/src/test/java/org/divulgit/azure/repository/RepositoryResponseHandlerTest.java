package org.divulgit.azure.repository;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepositoryResponseHandlerTest {

    @Test
    public void testMultipleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        AzureRepositoryResponseHandler handler = new AzureRepositoryResponseHandler();

        List<AzureRepository> azureRepositories = handler.handle200Response(ResponseEntity.ok(json));

        assertEquals(2, azureRepositories.size());
        assertEquals("b2dd3659-1920-4a40-a7d8-7c869f7ff140", azureRepositories.get(0).getExternalId());
        assertEquals("test-project0", azureRepositories.get(0).getName());
        assertEquals("https://dev.azure.com/decioluckow-teste/test-project/_git/test-project0", azureRepositories.get(0).getUrl());
        assertEquals("b2dd3659-1920-4a40-a7d8-7c869f7ff141", azureRepositories.get(1).getExternalId());
        assertEquals("test-project1", azureRepositories.get(1).getName());
        assertEquals("https://dev.azure.com/decioluckow-teste/test-project/_git/test-project1", azureRepositories.get(1).getUrl());
    }
}
