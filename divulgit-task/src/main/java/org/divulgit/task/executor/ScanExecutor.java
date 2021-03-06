package org.divulgit.task.executor;

import java.util.List;
import java.util.Optional;

import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.mergerequest.MergeRequestIdsRemoteScan;
import org.divulgit.task.mergerequest.MergeRequestRemoteScan;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ScanExecutor {

    private ApplicationContext context;

    public ScanExecutor(ApplicationContext context) {
        this.context = context;
    }

    public RemoteScan.UniqueId scanRemoteForProjects(Remote remote, User user, String token) {
        RemoteScan remoteScan = (RemoteScan) context.getBean("projectRemoteScan", remote, user, token);
        remoteScan.register();
        remoteScan.run();
        return remoteScan.uniqueId();
    }

    public RemoteScan.UniqueId scanProjectForMergeRequests(Remote remote, User user, Project project, Optional<Integer> scanFrom, String token) {
        RemoteScan remoteScan = MergeRequestRemoteScan.build(remote, user, project, scanFrom, token);
        remoteScan.register();
        remoteScan.run();
        return remoteScan.uniqueId();
    }

    public RemoteScan.UniqueId scanProjectForMergeRequests(Remote remote, User user, Project project, List<String> requestedMergeRequestIds, String token) {
        RemoteScan remoteScan = MergeRequestIdsRemoteScan.build(remote, user, project, requestedMergeRequestIds, token);
        remoteScan.register();
        remoteScan.run();
        return remoteScan.uniqueId();
    }
}
