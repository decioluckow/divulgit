package org.divulgit.task.listener;

import lombok.extern.slf4j.Slf4j;
import org.divulgit.model.Remote;
import org.divulgit.model.Task;
import org.divulgit.model.User;
import org.divulgit.repository.TaskRepository;
import org.divulgit.task.RemoteScan;
import org.divulgit.type.TaskState;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class PersistenceScanListener implements ScanListener {

    private TaskRepository taskRepository;
    private Remote remote;
    private User user;

    private Task task;

    public PersistenceScanListener(TaskRepository taskRepository, Remote remote, User user) {
        this.taskRepository = taskRepository;
        this.remote = remote;
        this.user = user;
    }

    @Override
    public Optional<RemoteScan.UniqueId> register(String title, String detail) {
        task = new Task(title, detail, remote.getId(), user.getId());
        taskRepository.save(task);
        return Optional.of(new RemoteScan.UniqueId(task.getId()));
    }

    @Override
    public void updateStep(LocalDateTime moment, String description, TaskState state) {
        task.setCurrentStep(Task.TaskStep.builder().dateTime(moment).description(description).state(state).build());
        log.info("Saving {} with state {}", task.getId(), state);
        taskRepository.save(task);
    }

    @Override
    public void subTask(RemoteScan.UniqueId uniqueId) {
        task.addSubTasks(uniqueId.getValue());
        taskRepository.save(task);
    }
}
