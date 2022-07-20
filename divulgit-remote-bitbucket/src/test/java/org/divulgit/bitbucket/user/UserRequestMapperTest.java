package org.divulgit.bitbucket.user;
import bitbucket.user.BitBucketUser;
import bitbucket.user.UserResponseHandler;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.test.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRequestMapperTest {

    @Test
    public void testSingleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "singleResponse.json");
        UserResponseHandler handler = new UserResponseHandler();
        BitBucketUser userRequest = handler.handle200ResponseSingleResult(ResponseEntity.ok(json));

        assertEquals("5c4c6b3c5f6f554665c13317", userRequest.getInternalId());
        assertEquals("Wesley Eduardo de Oliveira Melo", userRequest.getName());
        assertEquals("wesleyeduardocr7", userRequest.getUsername());
        assertEquals("https://secure.gravatar.com/avatar/64e5828e1a6e7310b1fbf67f536b9bdd?d=https%3A%2F%2Favatar" +
                "-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FWM-6.png", userRequest.getAvatarURL());
    }

    @Test
    public void testMultipleResponse() throws IOException, RemoteException {
        String json = TestUtils.getResourceAsString(this, "multipleResponse.json");
        UserResponseHandler handler = new UserResponseHandler();

        List<BitBucketUser> userRequests = handler.handle200ResponseMultipleResult(ResponseEntity.ok(json));
        assertEquals(2, userRequests.size());

        BitBucketUser firstUserRequest = userRequests.get(0);
        assertEquals("5c4c6b3c5f6f554665c13317", firstUserRequest.getInternalId());
        assertEquals("Wesley Eduardo de Oliveira Melo", firstUserRequest.getName());
        assertEquals("wesleyeduardocr7", firstUserRequest.getUsername());
        assertEquals("https://secure.gravatar.com/avatar/64e5828e1a6e7310b1fbf67f536b9bdd?d=https%3A%2F%2Favatar" +
                "-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FWM-6.png", firstUserRequest.getAvatarURL());

        BitBucketUser secondUserRequest = userRequests.get(1);
        assertEquals("5c4c6b3c5f6f554665c13317", secondUserRequest.getInternalId());
        assertEquals("Décio Luckwov", secondUserRequest.getName());
        assertEquals("decioluckow", secondUserRequest.getUsername());
        assertEquals("https://secure.gravatar.com/avatar/64e5828e1a6e7310b1fbf67f536b9bdd?d=https%3A%2F%2Favatar" +
                "-management--avatars.us-west-2.prod.public.atl-paas.net%2Finitials%2FWM-6.png", secondUserRequest.getAvatarURL());
    }
}