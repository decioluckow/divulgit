package org.divulgit.vo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectIdMaxDiscussion {
    private String id;
    private LocalDate maxDiscussion;
}