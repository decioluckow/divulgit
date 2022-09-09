package org.divulgit.gitlab.project;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectResponseHandlerTest {

    private ProjectResponseHandler responseHandler = new ProjectResponseHandler();

    @Test
    public void testConvertProject() throws IOException, RemoteException {
        String json = TestUtil.getResourceAsString(this,"projects.json");

        List<GitLabProject> authenticatedUser = responseHandler.handle200Response(ResponseEntity.ok(json));

        assertEquals(2, authenticatedUser.size());
    }
}
