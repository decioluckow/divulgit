package org.divulgit.model;

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
    private int externalId;
    private String title;
    private String description;
    private String author;
    private State state;
    private List<Comment> comments;

    @Setter(AccessLevel.NONE)
    private long commentsTotal;

    @Setter(AccessLevel.NONE)
    private long commentsDiscussed;

    protected void calculateComments() {
        if (comments != null) {
            commentsTotal = comments.size();
            commentsDiscussed = comments.stream().filter(c -> c.isDiscussed()).count();
        } else {
            commentsTotal = 0;
            commentsDiscussed = 0;
        }
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

    public static enum State {
        OPENED, MERGED, CLOSED;
    }
}
