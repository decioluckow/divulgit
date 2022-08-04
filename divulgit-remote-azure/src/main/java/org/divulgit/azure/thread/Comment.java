package org.divulgit.azure.thread;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @Getter
    @Setter
    private String url;

    @Getter
    private ThreadContext threadContext;

    @Override
    public String getAuthor() {
        return azureAuthor.getUsername();
    }

    public MergeRequest.Comment.CommentBuilder toComment() {
        return MergeRequest.Comment.builder()
                .externalId(String.valueOf(externalId))
                .text(text)
                .author(azureAuthor.getUsername());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ThreadContext {
        private String filePath;
    }
}
