package org.divulgit.gitlab.user;

import org.divulgit.gitlab.project.ProjectMapperTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private UserMapper mapper = new UserMapper();

    @Test
    public void testConvertUser() throws IOException {
        InputStream jsonResource = this.getClass().getResourceAsStream("user.json");
        String json = new String(jsonResource.readAllBytes(), StandardCharsets.UTF_8);

        GitLabUser authenticatedUser = mapper.convertToUser(json);

        assertEquals("john_smith", authenticatedUser.getUsername());
        assertEquals("John Smith", authenticatedUser.getName());
        assertEquals("1", authenticatedUser.getInternalId());
        assertEquals("http://localhost:3000/uploads/user/avatar/1/index.jpg", authenticatedUser.getAvatarURL());
    }

}