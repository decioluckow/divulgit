package org.divulgit.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.divulgit.task.listener.ScanListener;
import org.divulgit.type.TaskState;

import java.time.LocalDateTime;

public interface RemoteScan extends Runnable {

    UniqueId uniqueId();

    void addScanListener(ScanListener listener);

    RemoteScan.UniqueId register();

    void registerSubTask(RemoteScan.UniqueId uniqueId);

    void start();

    @Getter
    @AllArgsConstructor
    class UniqueId {
        private String value;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Step {
        private String description;
        private LocalDateTime dateTime;
        private TaskState state;
    }
}
