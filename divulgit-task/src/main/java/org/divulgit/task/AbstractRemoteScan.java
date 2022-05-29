package org.divulgit.task;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.divulgit.task.listener.NullScanListener;
import org.divulgit.task.listener.ScanListener;
import org.divulgit.type.TaskState;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public abstract class AbstractRemoteScan implements RemoteScan {

    private RemoteScan.UniqueId uniqueId;

    @Getter
    private Step currentStep;

    private ScanListener scanListener = new NullScanListener();

    protected AbstractRemoteScan() {
    }

    @Override
    public UniqueId uniqueId() {
        return uniqueId;
    }

    public abstract String mountTitle();

    public abstract String mountDetail();

    @Override
    public void addScanListener(ScanListener scanListener) {
        if (scanListener != null) this.scanListener = scanListener;
    }

    @Override
    public RemoteScan.UniqueId register() {
        Optional<UniqueId> uniqueId = this.scanListener.register(mountTitle(), mountDetail());
        if (! uniqueId.isPresent())
            uniqueId = Optional.of(new UniqueId(UUID.randomUUID().toString()));
        updateStep(Strings.EMPTY, TaskState.WAITING);
        this.uniqueId = uniqueId.get();
        return this.uniqueId;
    }

    @Override
    public void registerSubTask(RemoteScan.UniqueId uniqueId) {
        this.scanListener.subTask(uniqueId);
    }

    @Async
    @Override
    public void run() {
        updateStep(Strings.EMPTY, TaskState.RUNNING);
        execute();
        updateStep(Strings.EMPTY, TaskState.FINISHED);
    }

    protected abstract void execute();

    protected final void addErrorStep(final String description) {
        updateStep(description, TaskState.ERROR);

    }
    private void updateStep(final String description, final TaskState state) {
        log.info("Updating state to {}", state);
        LocalDateTime now = LocalDateTime.now();
        scanListener.updateStep(now, description, state);
        currentStep = Step.builder().dateTime(now).description(description).state(state).build();
    }
}
