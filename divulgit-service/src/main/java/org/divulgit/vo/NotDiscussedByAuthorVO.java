package org.divulgit.vo;

import lombok.Getter;

@Getter
public class NotDiscussedByAuthorVO {
    private String projectId;
    private String username;
    private long mergeRequestCount = 0;
    private long commentCount = 0;

    public NotDiscussedByAuthorVO(String projectId, String username) {
        this.projectId = projectId;
        this.username = username;
    }

    public long incrementMergeRequestCount() {
        return ++mergeRequestCount;
    }

    public long incrementCommentCount(long amount) {
        commentCount += amount;
        return commentCount;
    }
}
