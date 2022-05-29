package org.divulgit.task.listener;

import org.divulgit.task.RemoteScan;
import org.divulgit.type.TaskState;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScanListener {

    Optional<RemoteScan.UniqueId> register(String title, String detail);

    void updateStep(LocalDateTime moment, String description, final TaskState state);

    void subTask(RemoteScan.UniqueId uniqueId);
}
