package br.com.decioluckow.divulgit.tasks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TaskStep {

    private String description;
    private LocalDateTime dateTime;
    private TaskState state;
}
