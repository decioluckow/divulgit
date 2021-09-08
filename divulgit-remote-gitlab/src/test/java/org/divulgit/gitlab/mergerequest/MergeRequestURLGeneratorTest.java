package org.divulgit.gitlab.mergerequest;

import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class MergeRequestURLGeneratorTest {

    public static final Remote REMOTE = Remote.builder().url("git.company.com").build();
    public static final Project PROJECT = Project.builder().externalId("250").build();
    public static final String PAGE_1 = "1";
    public static final String PAGE_5 = "5";
    public static final List<Integer> MR_IDS = Arrays.asList(5,7,9);

    private MergeRequestURLGenerator urlGenerator = new MergeRequestURLGenerator();

    @Test
    public void testSimpleCall() {
        ReflectionTestUtils.setField(urlGenerator, "pageSize", 50);

        String url = urlGenerator.build(REMOTE, PROJECT, Collections.emptyList(), PAGE_1);

        Assertions.assertEquals("https://git.company.com/api/v4/projects/250/merge_requests?per_page=50&page=1", url);
    }

    @Test
    public void testCallByIds() {
        ReflectionTestUtils.setField(urlGenerator, "pageSize", 50);

        String url = urlGenerator.build(REMOTE, PROJECT, MR_IDS, PAGE_5);

        Assertions.assertEquals("https://git.company.com/api/v4/projects/250/merge_requests?per_page=50&page=5&iids[]=5&iids[]=7&iids[]=9", url);
    }

}