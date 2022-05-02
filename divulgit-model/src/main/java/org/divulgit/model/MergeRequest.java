package org.divulgit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Document(collection = "mergeRequest")
public class MergeRequest {

    @Id
    private String id;

    private String projectId;
    private int externalId;
    private String url;
    private String title;
    private String description;
    private String author;
    private State state;
    private List<Comment> comments;

    private int commentsTotal;

    private int commentsDiscussed;

    public List<Comment> getValidComments() {
        return comments.stream().filter(c -> c.isValid()).collect(Collectors.toList());
    }

    protected void calculateComments() {
        if (comments != null) {
            commentsTotal = (int) comments.stream().filter(c -> c.isValid()).count();
            commentsDiscussed = (int) comments.stream().filter(c -> c.isDiscussed() && c.isValid()).count();
        } else {
            comments = new ArrayList<>();
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
        private String url;
        private List<String> hashTags;
        private LocalDateTime discussedOn;
        private State state;

        public boolean isDiscussed() {
            return this.discussedOn != null;
        }

        public boolean isDeleted() {
            return this.state == State.DELETED;
        }

        public boolean isValid() {
            return this.state == State.VALID;
        }

        public static enum State {
            VALID, HIDDEN, DELETED;
        }
    }

    public static enum State {
        OPENED, MERGED, CLOSED;
    }
}
