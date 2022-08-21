package org.divulgit.task.mergerequest;
import java.util.ArrayList;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class MergeRequestIdsRemoteScan extends AbstractRemoteScan {

    @Autowired
    private MergeRequestService mergeRequestService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RemoteCallerFacadeFactory callerFactory;

    private final Remote remote;
    private final User user;
    private final Project project;
    private final List<String> requestedMergeRequestIds;
    private final Authentication authentication;

    public static RemoteScan build(Remote remote,
                        User user,
                        Project project,
                        List<String> requestedMergeRequestIds,
                        Authentication authentication) {
        return (RemoteScan) ApplicationContextProvider.getApplicationContext().getBean("mergeRequestIdsRemoteScan", remote, user, project, requestedMergeRequestIds, authentication);
    }

    public MergeRequestIdsRemoteScan(
            Remote remote,
            User user,
            Project project,
            List<String> requestedMergeRequestIds,
            Authentication authentication) {
        this.remote = remote;
        this.user = user;
        this.project = project;
        this.requestedMergeRequestIds = requestedMergeRequestIds;
        this.authentication = authentication;
    }

    @Override
    public String mountTitle() {
        return "Scanning merge requests";
    }

    @Override
    public String mountDetail() {
        return "Scanning " + requestedMergeRequestIds.size() + " merge requests for project " + project.getName();
    }

    @Override
    public RemoteScan.UniqueId register() {
        super.addScanListener(new PersistenceScanListener(taskRepository, remote, user));
        return super.register();
    }

    @Override
    public void execute() {
        try {
            log.info("Starting scanning code review ids for remote: {} and project: {}", remote.getId(), project.getId());
            List<MergeRequest> requestedMergeRequests = mergeRequestService.findAllByIds(requestedMergeRequestIds);
            List<Integer> requestedMergeRequestExernalIds = requestedMergeRequests.stream().map(MergeRequest::getExternalId).collect(Collectors.toList());
            log.trace("Considering external ids: {}", Joiner.on(",").join(requestedMergeRequestExernalIds));

            log.debug("Start retrieving merge requests from remote");
            List<? extends RemoteMergeRequest> remoteMergeRequests = callerFactory.build(remote).retrieveMergeRequests(remote, user, project, requestedMergeRequestExernalIds, authentication);
            log.debug("Finished retrieving merge requests from remote, {} retrieved", remoteMergeRequests.size());

            log.debug("Start merging merge requests");
            for (MergeRequest mergeRequest : requestedMergeRequests) {
                Optional<? extends RemoteMergeRequest> remoteMergeRequest = remoteMergeRequests.stream().filter(rmr -> rmr.getExternalId() == mergeRequest.getExternalId()).findFirst();
                if (remoteMergeRequest.isPresent()) {
                    mergeRequest.setTitle(remoteMergeRequest.get().getTitle());
                    mergeRequest.setDescription(remoteMergeRequest.get().getDescription());
                    mergeRequest.setState(remoteMergeRequest.get().toMergeRequest(project).getState());
                    scanComments(mergeRequest);
                }
            }
            log.debug("Finished merge requests merging");
        } catch (RemoteException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }

    private void scanComments(MergeRequest mergeRequest) {
        log.debug("Queueing scan comments for merge request {}", mergeRequest.getId());
        RemoteScan commentRemoteScan = CommentsRemoteScan.build(remote, user, project, mergeRequest, authentication);
        commentRemoteScan.register();
        registerSubTask(commentRemoteScan.uniqueId());
        commentRemoteScan.run();
    }
}
