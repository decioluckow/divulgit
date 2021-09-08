package org.divulgit.task.mergerequest;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.divulgit.config.ApplicationContextProvider;
import org.divulgit.gitlab.mergerequest.GitLabMergeRequest;
import org.divulgit.gitlab.mergerequest.MergeRequestCaller;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.repository.UserRepository;
import org.divulgit.service.MergeRequestService;
import org.divulgit.task.AbstractRemoteScan;
import org.divulgit.task.RemoteScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class MergeRequestRemoteScan extends AbstractRemoteScan {

    private static final String STATE_OPENED = "opened";
    private static final String STATE_CLOSED = "closed";
    private static final String STATE_MERGED = "merged";

    @Autowired
    private MergeRequestCaller caller;

    @Autowired
    private MergeRequestService mergeRequestService;

    @Autowired
    private ScanFromSolver scanFromSolver;

    private final Remote remote;
    private final Project project;
    private final Optional<Integer> requestedScanFrom;
    private final String token;

    public static RemoteScan build(Remote remote,
                                               Project project,
                                               Optional<Integer> requestedScanFrom,
                                               String token) {
        return (RemoteScan) ApplicationContextProvider.getApplicationContext()
                .getBean("mergeRequestRemoteScan", remote, project, requestedScanFrom, token);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MergeRequestRemoteScan(
            Remote remote,
            Project project,
            Optional<Integer> requestedScanFrom,
            String token) {
        this.remote = remote;
        this.project = project;
        this.requestedScanFrom = requestedScanFrom;
        this.token = token;
    }

    @Override
    public RemoteScan.UniqueKey uniqueKey() {
        return new RemoteScan.UniqueKey("project:" + project.getId());
    }

    @Override
    public void execute() {
        try {
            log.info("Starting scanning mergeRequests ids for remote: {} and project: {}", remote.getId(), project.getId());

            Integer scanFrom = scanFromSolver.solveScanFrom(requestedScanFrom, project);
            log.info("Considering scanning from merge request {}", scanFrom);

            log.debug("Start retrieving merge requests from remote");
            List<GitLabMergeRequest> remoteMergeRequests = caller.retrieveMergeRequests(remote, project, scanFrom, token);
            log.debug("Finished retrieving merge requests from remote, {} retrieved", remoteMergeRequests.size());

            log.debug("Start saving merge requests");
            List<MergeRequest> mergeRequests = remoteMergeRequests.stream().map(rmr -> rmr.toMergeRequest(project)).collect(Collectors.toList());
            mergeRequestService.saveAll(mergeRequests);
            log.debug("Finished merge requests saving");
        } catch (RemoteException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }
}
