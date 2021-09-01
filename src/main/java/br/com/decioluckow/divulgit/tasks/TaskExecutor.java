package br.com.decioluckow.divulgit.tasks;

import br.com.decioluckow.divulgit.model.Origin;
import br.com.decioluckow.divulgit.model.Project;
import br.com.decioluckow.divulgit.model.User;
import br.com.decioluckow.divulgit.tasks.project.ProjectScanTask;
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

    public TaskUniqueKey scanProjects(final Origin origin, final User user, final String token) {
        Task projectScanTask = (Task) context.getBean("projectScanTask", origin, user, token);
        executorService.execute(projectScanTask);
        return projectScanTask.uniqueKey();
    }

    public TaskUniqueKey scanMergeRequests(final Project project, final String token) {
        Task projectScanTask = (Task) context.getBean("mergeRequestScanTask", project, token);
        executorService.execute(projectScanTask);
        return projectScanTask.uniqueKey();
    }

}
