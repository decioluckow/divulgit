package org.divulgit.gitlab.comments;

import org.divulgit.gitlab.GitLabURLBuilder;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentURLGeneratorTest {

    private static final Remote REMOTE = Remote.builder().url("git.company.com").build();
    private static final Project PROJECT = Project.builder().externalId("1234").build();
    private static final MergeRequest MERGE_REQUEST = MergeRequest.builder().externalId(4321).build();

    public static final int PAGE_1 = 1;

    @Test
    public void testURL() {
        GitLabURLBuilder urlBuilder = new GitLabURLBuilder();

        String url = urlBuilder.buildCommentURL(REMOTE, PROJECT, MERGE_REQUEST, PAGE_1);

        assertEquals("https://git.company.com/api/v4/projects/1234/merge_requests/4321/notes?page=1", url);
    }
}
