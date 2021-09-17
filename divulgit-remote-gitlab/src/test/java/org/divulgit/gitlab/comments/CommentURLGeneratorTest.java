package org.divulgit.gitlab.comments;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentURLGeneratorTest {

    private static final Remote REMOTE = Remote.builder().url("git.company.com").build();
    private static final Project PROJECT = Project.builder().externalId("1234").build();
    private static final MergeRequest MERGE_REQUEST = MergeRequest.builder().externalId(4321).build();

    @Test
    public void testURL() {
        CommentURLGenerator urlGenerator = new CommentURLGenerator();

        String url = urlGenerator.build(REMOTE, PROJECT, MERGE_REQUEST, "1");

        assertEquals("https://git.company.com/api/v4/projects/1234/merge_requests/4321/notes?page=1", url);
    }
}
