package org.divulgit.gitlab.mergerequest;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.remote.model.RemoteMergeRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import org.divulgit.util.DateUtil;

@Data
public class GitLabMergeRequest implements RemoteMergeRequest {

    private static final String STATE_OPENED = "opened";
    private static final String STATE_CLOSED = "closed";
    private static final String STATE_MERGED = "merged";

    @JsonProperty("iid")
    private int externalId;
    private String title;
    private String description;
    @JsonProperty("web_url")
    private String url;
    private Author author;
    private String state;
    @JsonProperty("created_at")
    private String createdAt;


    @Override
    public String getAuthor() {
        return author.getUsername();
    }

    @Data
    public static class Author {
        private String username;
    }

    public MergeRequest toMergeRequest(Project project) {
        return MergeRequest.builder()
                .projectId(project.getId())
                .externalId(externalId)
                .title(title)
                .description(description)
                .author(author.getUsername())
                .url(url)
                .createdAt(DateUtil.parseDateFromDateTime(createdAt))
                .state(convertState(state)).build();
    }

    public static MergeRequest.State convertState(String state) {
        switch (state) {
            case STATE_OPENED:
                return MergeRequest.State.OPENED;
            case STATE_MERGED:
                return MergeRequest.State.MERGED;
            case STATE_CLOSED:
                return MergeRequest.State.CLOSED;
            default:
                throw new RuntimeException("MergeRequest.State not found for value " + state);
        }
    }
}
