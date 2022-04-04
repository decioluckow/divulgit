package org.divulgit.task;


import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRemoteScan implements RemoteScan {

    private List<Step> steps = new ArrayList<Step>();

    protected AbstractRemoteScan() {
        addStep(Strings.EMPTY, State.WAITING);
    }

    public abstract RemoteScan.UniqueKey uniqueKey();

    @Async
    @Override
    public void run() {
        addStep(Strings.EMPTY, State.RUNNING);
        execute();
        addStep(Strings.EMPTY, State.FINISHED);
    }

    protected abstract void execute();

    private void addStep(final String description, final State state) {
        steps.add(Step.builder().dateTime(LocalDateTime.now()).description(description).state(state).build());
    }

    protected final void addRunningStep(final String description) {
        addStep(description, State.RUNNING);
    }

    protected final void addErrorStep(final String description) {
        addStep(description, State.ERROR);
    }

    public List<Step> getSteps() {
        return ImmutableList.copyOf(steps);
    }

    public RemoteScan.State getLastStepState() {
        return getLastStep().getState();
    }

    public LocalDateTime getLastStepMoment() {
        return getLastStep().getDateTime();
    }

    private Step getLastStep() {
        return steps.get(steps.size() - 1);
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Step {
        private String description;
        private LocalDateTime dateTime;
        private State state;
    }

    @Getter
    @AllArgsConstructor
    public static class UniqueKey {
        private String taskUniqueKey;
    }
}
