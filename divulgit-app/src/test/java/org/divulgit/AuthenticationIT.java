package org.divulgit;

import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.divulgit.DivulgitApplication;
import org.divulgit.model.Remote;
import org.divulgit.repository.UserRepository;
import org.divulgit.security.CustomAuthenticationProvider;
import org.divulgit.util.TestUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

//@DataMongoTest()
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
            "spring.data.mongodb.host=localhost",
            "spring.data.mongodb.database=divulgit",
            "spring.data.mongodb.port=7070"
        })
@ContextConfiguration(classes = DivulgitApplication.class)
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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