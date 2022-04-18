package org.divulgit.gitlab.comments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import org.divulgit.gitlab.user.GitLabUser;
import org.divulgit.model.MergeRequest;
import org.divulgit.remote.model.RemoteComment;

import java.util.List;

@Data
public class GitLabComment implements RemoteComment {
	
    @JsonProperty("id")
    private String externalId;
    private GitLabUser author;
    @JsonProperty("body")
    private String text;
    private String url;
    private String type;
    private boolean system;
    
    @Override
    public String getAuthor() {
        return author.getUsername();
    }

    public MergeRequest.Comment toComment(List<String> hashTags) {
        return MergeRequest.Comment.builder()
                .externalId(externalId)
                .text(text)
                .author(author.getUsername())
                .hashTags(hashTags)
                .build();
    }
}
