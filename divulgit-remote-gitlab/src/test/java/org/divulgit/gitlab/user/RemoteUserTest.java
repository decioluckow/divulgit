package org.divulgit.gitlab.user;

import org.divulgit.remote.exception.RemoteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestPropertySource("/application-test.properties")
public class RemoteUserTest {

    @Autowired
    private CurrentUserCaller caller;

    @Test
    public void testConvertUser() throws IOException, RemoteException {

        Optional<GitLabUser> user = caller.retrieveCurrentUser("Tk9k3J1gF9DxQYYSLoEX");
        System.out.println(user);
    }

}