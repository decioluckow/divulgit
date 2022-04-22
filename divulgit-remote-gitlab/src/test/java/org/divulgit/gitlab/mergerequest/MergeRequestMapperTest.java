package org.divulgit.gitlab.mergerequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

public class MergeRequestMapperTest {

    private MergeRequestMapper mapper = new MergeRequestMapper();

    @Test
    public void testConvertProject() throws IOException {
        InputStream jsonResource = this.getClass().getResourceAsStream("mergerequests.json");
        String json = new String(jsonResource.readAllBytes(), StandardCharsets.UTF_8);

        List<GitLabMergeRequest> mergeRequests = mapper.convertoToMergeRequests(json);

        assertEquals(2, mergeRequests.size());
        GitLabMergeRequest mergeRequest0 = mergeRequests.get(0);
        assertEquals("NFE-0000 correção de bug", mergeRequest0.getTitle());
        assertEquals(1234, mergeRequest0.getExternalId());
        assertEquals("opened", mergeRequest0.getState());
        GitLabMergeRequest mergeRequest1 = mergeRequests.get(1);
        assertEquals("NFE-0001 melhoria", mergeRequest1.getTitle());
        assertEquals(4321, mergeRequest1.getExternalId());
        assertEquals("merged", mergeRequest1.getState());
    }
}