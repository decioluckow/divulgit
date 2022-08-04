package org.divulgit.azure.thread;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.divulgit.azure.util.Link;
import org.divulgit.model.MergeRequest;
import org.divulgit.remote.model.RemoteComment;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements RemoteComment {

    @Getter
    @JsonProperty("id")
    private String externalId;

    @JsonProperty("author")
    private AzureAuthor azureAuthor;

    @Getter
    @JsonProperty("content")
    private String text;

    @Getter
    private CommentType commentType;

    @JsonProperty("_links")
    private Links links;

    @Override
    public String getUrl() {
        return links.getSelf().getHref();
    }

    @Override
    public String getAuthor() {
        return azureAuthor.getUsername();
    }

    public MergeRequest.Comment.CommentBuilder toComment() {
        return MergeRequest.Comment.builder()
                .externalId(String.valueOf(externalId))
                .text(text)
                .url(getUrl())
                .author(azureAuthor.getUsername());
    }

    public static class Links {
        @Getter
        private Link self;
    }
}
