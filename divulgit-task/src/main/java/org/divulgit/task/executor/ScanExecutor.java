package org.divulgit.task.executor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.service.mergeRequest.MergeRequestService;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.mergerequest.MergeRequestIdsRemoteScan;
import org.divulgit.task.mergerequest.MergeRequestRemoteScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScanExecutor {

    private ApplicationContext context;

    private MergeRequestService mergeRequestService;

    @Value("${mergerequest.reload.opened.lastdays:30}")
    private int mergeRequestReloadLastDays;

    public ScanExecutor(ApplicationContext context, MergeRequestService mergeRequestService) {
        this.context = context;
        this.mergeRequestService = mergeRequestService;
    }

    public RemoteScan.UniqueId rescanOpenedMergeRequests(Remote remote, User user, Project project, Authentication authentication) {
        log.info("Starting scanning mergeRequests loaded and opened for remote: {} and project: {}", remote.getId(), project.getId());
        final List<MergeRequest> lastOpenedMergeRequests = mergeRequestService.findLastOpened(project.getId(), mergeRequestReloadLastDays);
        log.debug("Found {} opened merge requests to rescan", lastOpenedMergeRequests.size());
        RemoteScan.UniqueId uniqueId = new RemoteScan.UniqueId(Strings.EMPTY);
        final List<String> lastOpenedIds = lastOpenedMergeRequests.stream().map(MergeRequest::getId).collect(Collectors.toList());
        if (lastOpenedIds.size() > 0) {
            uniqueId = scanProjectForMergeRequests(remote, user, project, lastOpenedIds, authentication);
        }
        return uniqueId;
    }

    public RemoteScan.UniqueId scanRemoteForProjects(Remote remote, User user, Authentication authentication) {
        RemoteScan remoteScan = (RemoteScan) context.getBean("projectRemoteScan", remote, user, authentication);
        remoteScan.register();
        remoteScan.run();
        return remoteScan.uniqueId();
    }

    public RemoteScan.UniqueId scanProjectForMergeRequests(Remote remote, User user, Project project, Optional<Integer> scanFrom, Authentication authentication) {
        RemoteScan remoteScan = MergeRequestRemoteScan.build(remote, user, project, scanFrom, authentication);
        remoteScan.register();
        remoteScan.run();
        return remoteScan.uniqueId();
    }

    public RemoteScan.UniqueId scanProjectForMergeRequests(Remote remote, User user, Project project, List<String> requestedMergeRequestIds, Authentication authentication) {
        RemoteScan remoteScan = MergeRequestIdsRemoteScan.build(remote, user, project, requestedMergeRequestIds, authentication);
        remoteScan.register();
        remoteScan.run();
        return remoteScan.uniqueId();
    }
}
