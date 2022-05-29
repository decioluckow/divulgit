package org.divulgit.model;

import lombok.*;
import org.apache.tomcat.jni.Local;
import org.divulgit.type.TaskState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Document(collection = "task")
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    private String id;
    private String title;
    private String detail;
    private String remoteId;
    private String userId;
    private LocalDateTime registeredAt;
    private TaskStep currentStep;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<String> subTasks = new ArrayList<>();

    public Task(String title, String detail, String remoteId, String userId) {
        this.title = title;
        this.detail = detail;
        this.remoteId = remoteId;
        this.userId = userId;
        this.registeredAt = LocalDateTime.now();
    }

    public void addSubTasks(String subTask) {
        if (subTasks == null)
            subTasks = new ArrayList<>();
        subTasks.add(subTask);
    }

    public List<String> getSubTasks() {
        return Collections.unmodifiableList(subTasks);
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TaskStep {
        private String description;
        private LocalDateTime dateTime;
        private TaskState state;
    }
}
