package org.divulgit.task.mergerequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.divulgit.config.ApplicationContextProvider;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.remote.RemoteCallerFacadeFactory;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.remote.model.RemoteMergeRequest;
import org.divulgit.repository.TaskRepository;
import org.divulgit.service.mergeRequest.MergeRequestService;
import org.divulgit.task.AbstractRemoteScan;
import org.divulgit.task.RemoteScan;
import org.divulgit.task.comment.CommentsRemoteScan;
import org.divulgit.task.listener.PersistenceScanListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class MergeRequestRemoteScan extends AbstractRemoteScan {

    @Autowired
    private RemoteCallerFacadeFactory callerFactory;

    @Autowired
    private MergeRequestService mergeRequestService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ScanFromResolver scanFromResolver;

    private final Remote remote;
    private final User user;
    private final Project project;
    private final Optional<Integer> requestedScanFrom;
    private final String token;

    public static RemoteScan build(Remote remote,
    							   User user,
                                   Project project,
                                   Optional<Integer> requestedScanFrom,
                                   String token) {
        return (RemoteScan) ApplicationContextProvider.getApplicationContext()
                .getBean("mergeRequestRemoteScan", remote, user, project, requestedScanFrom, token);
    }

    public MergeRequestRemoteScan(
            Remote remote,
            User user,
            Project project,
            Optional<Integer> requestedScanFrom,
            String token) {
        this.remote = remote;
        this.user = user;
        this.project = project;
        this.requestedScanFrom = requestedScanFrom;
        this.token = token;
    }

    @Override
    public String mountTitle() {
        return "Scanning merge requests ";
    }

    @Override
    public String mountDetail() {
        return mountTitle() + "for project " + project.getName();
    }

    @Override
    public RemoteScan.UniqueId register() {
        super.addScanListener(new PersistenceScanListener(taskRepository, remote, user));
        return super.register();
    }

    @Override
    public void execute() {
        try {
            log.info("Starting scanning mergeRequests ids for remote: {} and project: {}", remote.getId(), project.getId());

            Integer scanFrom = scanFromResolver.resolveScanFrom(requestedScanFrom, project);
            log.info("Considering scanning from merge request {}", scanFrom);

            log.debug("Start retrieving merge requests from remote");
            List<? extends RemoteMergeRequest> remoteMergeRequests = callerFactory.build(remote).retrieveMergeRequests(remote, user, project, scanFrom, token);
            log.debug("Finished retrieving merge requests from remote, {} retrieved", remoteMergeRequests.size());
            
            log.debug("Start saving merge requests");
            List<MergeRequest> mergeRequests = remoteMergeRequests.stream().map(rmr -> rmr.toMergeRequest(project)).collect(Collectors.toList());
            mergeRequestService.saveAll(mergeRequests);
            log.debug("Finished merge requests saving");
            log.debug("Start queueing scan comments");
            mergeRequests.forEach(mr -> scanComments(mr));
            log.debug("Finished queueing scan comments");
        } catch (RemoteException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }

    private void scanComments(MergeRequest mergeRequest) {
        log.debug("Queueing scan comments for merge request {}", mergeRequest.getId());
        RemoteScan commentRemoteScan = CommentsRemoteScan.build(remote, user, project, mergeRequest, token);
        commentRemoteScan.register();
        registerSubTask(commentRemoteScan.uniqueId());
        commentRemoteScan.run();
    }
}
