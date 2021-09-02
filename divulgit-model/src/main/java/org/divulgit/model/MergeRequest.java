package org.divulgit.model;

import org.divulgit.type.MergeRequestStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "mergeRequest")
public class MergeRequest {

    @Id
    private String id;

    private String projectId;
    private String externalId;
    private String title;
    private String author;
    private String externalState;
    private MergeRequestStatus status;
    private List<Comment> comments;

    @Setter(AccessLevel.NONE)
    private long commentsLength;

    @Setter(AccessLevel.NONE)
    private long commentsDiscussed;

    protected void calculateComments() {
        commentsLength = comments.size();
        commentsDiscussed = comments.stream().filter(c -> c.isDiscussed()).count();
    }

    @Getter
    @Setter
    @Builder
    public static class Comment {
        private String externalId;
        private String author;
        private String text;
        private List<String> hashTags;
        private boolean discussed;
    }
}
