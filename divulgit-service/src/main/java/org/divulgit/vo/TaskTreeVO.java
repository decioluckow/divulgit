package org.divulgit.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.divulgit.type.TaskState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class TaskTreeVO {

    private String id;
    private String description;
    private String username;
    private LocalDateTime registeredAt;
    private String currentState;
    private String currentStateDescription;
    private LocalDateTime currentStateMoment;
    @Builder.Default
    private SubTaskDetail subTaskDetail = new SubTaskDetail();

    @Data
    @NoArgsConstructor
    public static class SubTaskDetail {
        private String description;
        private Map<String, AtomicInteger> subTasks = new HashMap<>();

        public void initSubTaskValues() {
            subTasks.put(TaskState.WAITING.name(), new AtomicInteger());
            subTasks.put(TaskState.RUNNING.name(), new AtomicInteger());
            subTasks.put(TaskState.FINISHED.name(), new AtomicInteger());
            subTasks.put(TaskState.ERROR.name(), new AtomicInteger());
        }

        public void incrementSubTaskState(TaskState state) {
            AtomicInteger atomicInteger = subTasks.get(state.name());
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger();
                subTasks.put(state.name(), atomicInteger);
            }
            atomicInteger.incrementAndGet();
        }
    }
}
