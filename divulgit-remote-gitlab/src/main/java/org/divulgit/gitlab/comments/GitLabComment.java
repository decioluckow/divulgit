package org.divulgit.gitlab.comments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.divulgit.model.MergeRequest;

import java.util.List;

@Data
public class GitLabComment {
    @JsonProperty("id")
    private String externalId;
    private Author author;
    @JsonProperty("body")
    private String text;
    private boolean system;

    @Data
    public static class Author {
        private String username;
    }

    public MergeRequest.Comment toComment() {
        return MergeRequest.Comment.builder()
                .externalId(externalId)
                .text(text)
                .author(author.getUsername())
                .build();
    }
}
