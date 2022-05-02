package org.divulgit.github.comment;

import java.util.List;

import org.divulgit.github.user.GitHubUser;
import org.divulgit.model.MergeRequest;
import org.divulgit.remote.model.RemoteComment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubComment implements RemoteComment {

    @JsonProperty("id")
    private String externalId;
    private GitHubUser user;
    @JsonProperty("body")
    private String text;
    @JsonProperty("html_url")
    private String url;
    
    @Override
    public String getAuthor() {
        return user.getUsername();
    }

    public MergeRequest.Comment.CommentBuilder toComment() {
        return MergeRequest.Comment.builder()
                .externalId(String.valueOf(externalId))
                .text(text)
                .url(url)
                .author(user.getUsername());
    }
}
