package org.divulgit.gitlab.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.divulgit.remote.exception.RemoteException;
import org.gitlab.gitlab.user.GitLabUser;
import org.gitlab.gitlab.user.UserResponseHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class UserMapperTest {

    private UserResponseHandler handler = new UserResponseHandler();

    @Test
    public void testConvertUser() throws IOException, RemoteException {
        InputStream jsonResource = this.getClass().getResourceAsStream("user.json");
        String json = new String(jsonResource.readAllBytes(), StandardCharsets.UTF_8);

        GitLabUser authenticatedUser = handler.handle200Response(ResponseEntity.ok(json));

        assertEquals("john_smith", authenticatedUser.getUsername());
        assertEquals("John Smith", authenticatedUser.getName());
        assertEquals("1", authenticatedUser.getInternalId());
        assertEquals("http://localhost:3000/uploads/user/avatar/1/index.jpg", authenticatedUser.getAvatarURL());
    }

}