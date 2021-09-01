package br.com.decioluckow.divulgit.restcaller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private UserMapper mapper = new UserMapper();

    @Test
    public void testConvertUser() throws JsonProcessingException {
        String json = "{ \"id\": 50, \"name\": \"Fulano de Tal\", \"username\": \"ftal\", \"state\": \"active\", \"avatar_url\": \"https://git.company.com/uploads/-/system/user/avatar/50/IMG_123456789.jpg\", \"web_url\": \"https://git.neogrid.com/dluckow\", \"created_at\": \"2016-10-04T15:22:12.787-03:00\", \"bio\": \"\", \"location\": \"\", \"skype\": \"\", \"linkedin\": \"\", \"twitter\": \"\", \"website_url\": \"\", \"organization\": null, \"last_sign_in_at\": \"2021-08-23T15:35:56.120-03:00\", \"confirmed_at\": \"2016-10-04T15:22:12.692-03:00\", \"last_activity_on\": \"2021-08-23\", \"email\": \"decio.luckow@neogrid.com\", \"theme_id\": null, \"color_scheme_id\": 1, \"projects_limit\": 100, \"current_sign_in_at\": \"2021-08-28T14:44:11.332-03:00\", \"identities\": [ { \"provider\": \"ldapmain\", \"extern_uid\": \"cn=decio heinzelmann luckow,ou=neogrid-mercador,ou=usuarios,dc=mercador,dc=local\" } ], \"can_create_group\": true, \"can_create_project\": true, \"two_factor_enabled\": false, \"external\": false }";
        GitLabUser authenticatedUser = mapper.convertFrom(json);
        assertEquals("ftal", authenticatedUser.getUsername());
        assertEquals("Fulano de Tal", authenticatedUser.getName());
        assertEquals("50", authenticatedUser.getInternalId());
        assertEquals("https://git.company.com/uploads/-/system/user/avatar/50/IMG_123456789.jpg", authenticatedUser.getAvatarURL());
    }

}