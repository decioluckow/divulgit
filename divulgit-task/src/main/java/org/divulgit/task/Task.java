package org.divulgit.task;


import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Task implements Runnable {

    private List<TaskStep> steps = new ArrayList<TaskStep>();

    protected Task() {
        steps.add(TaskStep.builder().state(TaskState.WAITING).build());
    }

    public abstract TaskUniqueKey uniqueKey();

    @Override
    public final void run() {
        addStep(Strings.EMPTY, TaskState.RUNNING);
        execute();
        addStep(Strings.EMPTY, TaskState.FINISHED);
    }

    protected abstract void execute();

    private void addStep(final String description, final TaskState state) {
        steps.add(TaskStep.builder().dateTime(LocalDateTime.now()).description(description).state(state).build());
    }

    protected final void addRunningStep(final String description) {
        addStep(description, TaskState.RUNNING);
    }

    protected final void addErrorStep(final String description) {
        addStep(description, TaskState.ERROR);
    }

    public List<TaskStep> getSteps() {
        return ImmutableList.copyOf(steps);
    }

    public TaskState getLastStepState() {
        return getLastStep().getState();
    }

    public LocalDateTime getLastStepMoment() {
        return getLastStep().getDateTime();
    }

    private TaskStep getLastStep() {
        return steps.get(steps.size() - 1);
    }
}
