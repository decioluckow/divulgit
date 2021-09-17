package org.divulgit.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectIdCommentsSum {
    private String id;
    private long commentsTotal;
    private long commentsDiscussed;
}