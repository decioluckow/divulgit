package org.divulgit.task;


import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Task implements Runnable {

    private List<Step> steps = new ArrayList<Step>();

    protected Task() {
        steps.add(Step.builder().state(State.WAITING).build());
    }

    public abstract UniqueKey uniqueKey();

    @Override
    public final void run() {
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

    public State getLastStepState() {
        return getLastStep().getState();
    }

    public LocalDateTime getLastStepMoment() {
        return getLastStep().getDateTime();
    }

    private Step getLastStep() {
        return steps.get(steps.size() - 1);
    }

    public static enum State {
        WAITING, RUNNING, FINISHED, ERROR;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Step {

        private String description;
        private LocalDateTime dateTime;
        private Task.State state;
    }

    @Getter
    @AllArgsConstructor
    public static class UniqueKey {
        private String taskUniqueKey;
    }


}
