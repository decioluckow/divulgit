package org.divulgit.gitlab.mergerequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.divulgit.model.MergeRequest;
import org.divulgit.remote.remote.model.RemoteMergeRequest;

@Data
public class GitLabMergeRequest implements RemoteMergeRequest {
    @JsonProperty("iid")
    private int externalId;
    private String title;
    private Author author;
    private String state;

    @Override
    public String getAuthor() {
        return author.getUsername();
    }

    @Data
    public static class Author {
        private String username;
    }
}
