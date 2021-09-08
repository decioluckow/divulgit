package org.divulgit.task;

import org.divulgit.model.Remote;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.task.mergerequest.MergeRequestIdsRemoteScan;
import org.divulgit.task.mergerequest.MergeRequestRemoteScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Component
public class ScanExecutor {

    private ApplicationContext context;

    public ScanExecutor(ApplicationContext context) {
        this.context = context;
    }

    public RemoteScan.UniqueKey scanRemoteForProjects(Remote remote, User user, String token) {
        RemoteScan remoteScan = (RemoteScan) context.getBean("projectRemoteScan", remote, user, token);
        remoteScan.run();
        return remoteScan.uniqueKey();
    }

    public RemoteScan.UniqueKey scanProjectForMergeRequests(Remote remote, Project project, Optional<Integer> scanFrom, String token) {
        RemoteScan remoteScan = MergeRequestRemoteScan.build(remote, project, scanFrom, token);
        remoteScan.run();
        return remoteScan.uniqueKey();
    }

    public RemoteScan.UniqueKey scanProjectForMergeRequests(Remote remote, Project project, List<String> requestedMergeRequestIds, String token) {
        RemoteScan remoteScan = MergeRequestIdsRemoteScan.build(remote, project, requestedMergeRequestIds, token);
        remoteScan.run();
        return remoteScan.uniqueKey();
    }

}
