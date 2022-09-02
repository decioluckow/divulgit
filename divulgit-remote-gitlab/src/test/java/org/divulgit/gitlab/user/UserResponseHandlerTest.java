package org.divulgit.gitlab.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.divulgit.remote.exception.RemoteException;
import org.divulgit.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class UserResponseHandlerTest {

    private UserResponseHandler handler = new UserResponseHandler();

    @Test
    public void testConvertUser() throws IOException, RemoteException {
        String json = TestUtil.getResourceAsString(this, "user.json");

        GitLabUser authenticatedUser = handler.handle200Response(ResponseEntity.ok(json));

        assertEquals("john_smith", authenticatedUser.getUsername());
        assertEquals("John Smith", authenticatedUser.getName());
        assertEquals("1", authenticatedUser.getInternalId());
        assertEquals("http://localhost:3000/uploads/user/avatar/1/index.jpg", authenticatedUser.getAvatarURL());
    }
}
