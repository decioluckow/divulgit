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
public class AzureComment {

    @Getter
    private String id;

    @JsonProperty("author")
    @Getter
    private AzureAuthor azureAuthor;

    @Getter
    @JsonProperty("content")
    private String text;

    @Getter
    private CommentType commentType;
}
