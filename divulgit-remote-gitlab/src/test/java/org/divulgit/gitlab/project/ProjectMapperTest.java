package org.divulgit.gitlab.project;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectMapperTest {

    private ProjectMapper mapper = new ProjectMapper();

    @Test
    public void testConvertProject() throws IOException {
        InputStream jsonResource = this.getClass().getResourceAsStream("projects.json");
        String json = new String(jsonResource.readAllBytes(), StandardCharsets.UTF_8);

        List<GitLabProject> authenticatedUser = mapper.convertToProjects(json);

        assertEquals(2, authenticatedUser.size());
    }

}