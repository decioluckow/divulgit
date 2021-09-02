package org.divulgit.gitlab.comments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GitLabComment {
    @JsonProperty("id")
    private String externalId;
    @JsonProperty("author.username")
    private String author;
    @JsonProperty("body")
    private String text;
    private boolean system;
    private List<String> hashTags;
}
