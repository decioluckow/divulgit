package org.divulgit.azure.thread;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CommentType {
    TEXT, SYSTEM;

    @JsonCreator
    public static CommentType getEnumFromValue(String value) {
        for (CommentType commentType : values()) {
            if (commentType.name().toLowerCase().equals(value)) {
                return commentType;
            }
        }
        throw new IllegalArgumentException("Invalid value " + value);
    }
}
