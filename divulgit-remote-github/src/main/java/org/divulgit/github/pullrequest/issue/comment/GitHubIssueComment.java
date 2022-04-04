package org.divulgit.github.pullrequest.issue.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.divulgit.github.user.GitHubUser;
import org.divulgit.model.MergeRequest;
import org.divulgit.remote.model.RemoteComment;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubIssueComment implements RemoteComment {

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

    public MergeRequest.Comment toComment(List<String> hashTags) {
        return MergeRequest.Comment.builder()
                .externalId(String.valueOf(externalId))
                .text(text)
                .url(url)
                .hashTags(hashTags)
                .author(user.getUsername()).build();
    }
}
