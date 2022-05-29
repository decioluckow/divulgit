package org.divulgit.task.listener;

import org.divulgit.task.RemoteScan;
import org.divulgit.type.TaskState;

import java.time.LocalDateTime;
import java.util.Optional;

public class NullScanListener implements ScanListener {

    @Override
    public Optional<RemoteScan.UniqueId> register(String title, String detail) {
        return Optional.empty();
    }

    @Override
    public void updateStep(LocalDateTime moment, String description, TaskState state) {
    }

    @Override
    public void subTask(RemoteScan.UniqueId uniqueId) {
    }
}
