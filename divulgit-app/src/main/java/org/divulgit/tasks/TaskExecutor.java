package org.divulgit.tasks;

import org.divulgit.model.Remote;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class TaskExecutor {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ExecutorService executorService;

    public TaskUniqueKey scanProjects(final Remote remote, final User user, final String token) {
        Task projectScanTask = (Task) context.getBean("projectScanTask", remote, user, token);
        executorService.execute(projectScanTask);
        return projectScanTask.uniqueKey();
    }

    public TaskUniqueKey scanMergeRequests(final Project project, final String token) {
        Task projectScanTask = (Task) context.getBean("mergeRequestScanTask", project, token);
        executorService.execute(projectScanTask);
        return projectScanTask.uniqueKey();
    }

}
