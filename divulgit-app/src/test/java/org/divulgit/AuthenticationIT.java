package org.divulgit;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.model.Remote;
import org.divulgit.repository.UserRepository;
import org.divulgit.security.CustomAuthenticationProvider;
import org.divulgit.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ContextConfiguration(classes = DivulgitApplication.class)
public class AuthenticationIT {

    private static final String CONNECTION_STRING = "mongodb://%s:%d";

    private MongodExecutable mongodExecutable;

    private ClientAndServer mockServer;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private GitLabURLBuilder urlBuilder;

    @BeforeEach
    public void startServer() throws IOException {
        mockExternalServer();
        mockDatabase();
    }

    private void mockExternalServer() {
        mockServer = startClientAndServer(1080);
    }

    private void mockDatabase() throws IOException {
        String ip = "localhost";

        ImmutableMongodConfig mongodConfig = MongodConfig
                .builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(ip, 0, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
    }

    @Test
    public void sampleIntegrationTest() throws IOException {
        Mockito.when(urlBuilder.buildUserURL(Mockito.any(Remote.class))).thenReturn("http://localhost:" + mockServer.getLocalPort() + "/api/v4/user/");
        mockServer.when(HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/api/v4/user/"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(TestUtil.getResourceAsString(this, "user.json"))
                        .withDelay(TimeUnit.SECONDS, 1));
        String principal = "{\"username\":\"\",\"organization\":\"\", \"domain\":\"gitlab.com\", \"plataform\":\"GITLAB\"}";
        String credential = "xpto123token";
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(principal, credential);

        Authentication authenticateResponse = customAuthenticationProvider.authenticate(authenticationRequest);

        Assertions.assertTrue(authenticateResponse.isAuthenticated());
        Assertions.assertNotNull(userRepository.findById("john@example.com"));
    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();
    }
}