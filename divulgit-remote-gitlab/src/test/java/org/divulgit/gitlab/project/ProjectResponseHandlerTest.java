package org.divulgit.gitlab.project;

import org.divulgit.remote.exception.RemoteException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectResponseHandlerTest {

    private ProjectResponseHandler responseHandler = new ProjectResponseHandler();

    @Test
    public void testConvertProject() throws IOException, RemoteException {
        InputStream jsonResource = this.getClass().getResourceAsStream("projects.json");
        String json = new String(jsonResource.readAllBytes(), StandardCharsets.UTF_8);

        List<GitLabProject> authenticatedUser = responseHandler.handle200Response(ResponseEntity.ok(json));

        assertEquals(2, authenticatedUser.size());
    }

}