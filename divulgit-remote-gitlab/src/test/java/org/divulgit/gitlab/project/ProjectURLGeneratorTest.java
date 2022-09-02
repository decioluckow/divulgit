package org.divulgit.gitlab.project;

import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.model.Remote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ProjectURLGeneratorTest {
    public static final Remote REMOTE = Remote.builder().url("git.company.com").build();
    public static final int PAGE_1 = 1;

    private GitLabURLBuilder urlGenerator = new GitLabURLBuilder();

    @Test
    public void testSimpleCall() {
        ReflectionTestUtils.setField(urlGenerator, "pageSize", 50);

        String url = urlGenerator.buildProjectURL(REMOTE, PAGE_1);

        Assertions.assertEquals("https://git.company.com/api/v4/projects?membership=true&per_page=50&page=1", url);
    }
}
