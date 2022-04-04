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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class MergeRequestIdsRemoteScan extends AbstractRemoteScan {

    @Autowired
    private MergeRequestCaller caller;

    @Autowired
    private MergeRequestService mergeRequestService;

    @Autowired
    private UserRepository userRepository;

    private final Remote remote;
    private final Project project;
    private final List<String> requestedMergeRequestIds;
    private final String token;

    public static MergeRequestIdsRemoteScan build(Remote remote,
                        Project project,
                        List<String> requestedMergeRequestIds,
                        String token) {
        return (MergeRequestIdsRemoteScan) ApplicationContextProvider.getApplicationContext().getBean("commentsRemoteScan", remote, project, requestedMergeRequestIds, token);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MergeRequestIdsRemoteScan(
            Remote remote,
            Project project,
            List<String> requestedMergeRequestIds,
            String token) {
        this.remote = remote;
        this.project = project;
        this.requestedMergeRequestIds = requestedMergeRequestIds;
        this.token = token;
    }

    @Override
    public RemoteScan.UniqueKey uniqueKey() {
        return new RemoteScan.UniqueKey("project:" + project.getId());
    }

    @Override
    public void execute() {
        try {
            log.info("Starting scanning code review ids for remote: {} and project: {}", remote.getId(), project.getId());
            List<MergeRequest> requestedMergeRequests = mergeRequestService.findAllByIds(requestedMergeRequestIds);
            List<Integer> requestedMergeRequestExernalIds = requestedMergeRequests.stream().map(mr -> mr.getExternalId()).collect(Collectors.toList());
            log.debug("Considering external ids: {}", Joiner.on(",").join(requestedMergeRequestExernalIds));

            log.debug("Start retrieving merge requests from remote");
            List<GitLabMergeRequest> remoteMergeRequests = caller.retrieveMergeRequests(remote, project, requestedMergeRequestExernalIds, token);
            log.debug("Finished retrieving merge requests from remote, {} retrieved", remoteMergeRequests.size());

            log.debug("Start merging merge requests");
            for (MergeRequest mergeRequest : requestedMergeRequests) {
                Optional<GitLabMergeRequest> remoteMergeRequest = remoteMergeRequests.stream().filter(rmr -> rmr.getExternalId() == mergeRequest.getExternalId()).findFirst();
                if (remoteMergeRequest.isPresent()) {
                    mergeRequest.setTitle(remoteMergeRequest.get().getTitle());
                    mergeRequest.setDescription(remoteMergeRequest.get().getDescription());
                    mergeRequest.setState(GitLabMergeRequest.convertState(remoteMergeRequest.get().getState()));
                    //TODO trigger comments scanning
                }
            }
            log.debug("Finished merge requests merging");
        } catch (RemoteException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }
    }
}
