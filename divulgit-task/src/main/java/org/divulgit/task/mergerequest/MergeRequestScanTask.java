package org.divulgit.task.mergerequest;

import org.divulgit.gitlab.mergerequest.GitLabMergeRequest;
import org.divulgit.gitlab.mergerequest.MergeRequestCaller;
import org.divulgit.gitlab.project.GitLabProject;
import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.remote.exception.RemoteException;
import org.divulgit.repository.MergeRequestRepository;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.service.MergeRequestService;
import org.divulgit.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class MergeRequestScanTask extends Task {

    private static final String STATE_OPENED = "opened";
    private static final String STATE_CLOSED = "closed";
    private static final String STATE_MERGED = "merged";

    @Autowired
    private MergeRequestCaller caller;

    @Autowired
    private MergeRequestService mergeRequestService;

    @Autowired
    private UserRepository userRepository;

    private final Remote remote;
    private final Project project;
    private final String token;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MergeRequestScanTask(Remote remote, Project project, List<String> ids, String token) {
        this.remote = remote;
        this.project = project;
        this.token = token;
    }

    @Override
    public UniqueKey uniqueKey() {
        return new UniqueKey("project:" + project.getId());
    }

    @Override
    public void execute() {
        try {
            //TODO como conciliar o scan solicitado do armazenado e do "ultimo"?
            int scanFrom;
            List<String> requestedMergeRequestExternalIds = new ArrayList<>();
            if (requestedMergeRequestExternalIds.isEmpty()) {
                scanFrom = mergeRequestService.findLastExternalId(project);
            } else {
                scanFrom = 0;
            }


            List<GitLabMergeRequest> mergeRequests = caller.retrieveMergeRequests(remote, project, requestedMergeRequestExternalIds, scanFrom, token);

            if (requestedMergeRequestExternalIds.isEmpty()) {

            } else {

            }


            log.info("Starting scanning projects for remote {}", remote.getId());
            List<GitLabProject> projects = caller.retrieveProjects(remote, token);
            log.info("Finished scanning, found {} projects", projects);
            List<String> existingExternalProjectIds = projectService.findExternalIdByRemote(remote);
            List<Project> newProjects = addNewProjects(remote, projects, existingExternalProjectIds);
            log.info("Found {} new projects", newProjects.size());
            List<String> newUserProjectIds = addNewProjectsToUser(newProjects, user);
            log.info("Added {} new projects to user {}", newUserProjectIds.size(), user.getId());
        } catch (RemoteException e) {
            final String message = "Error executing project scanning";
            addErrorStep(message + " - " + e.getMessage());
            log.error(message, e);
        }


    }

    private MergeRequest convertFromRemote(GitLabMergeRequest remoteMergeRequest, Project project) {
        return MergeRequest.builder()
                .projectId(project.getId())
                .externalId(remoteMergeRequest.getExternalId())
                .title(remoteMergeRequest.getTitle())
                .author(remoteMergeRequest.getAuthor())
                .state(convertState(remoteMergeRequest.getState());
    }

    private MergeRequest.State convertState(String state) {
        switch (state) {
            case STATE_OPENED: return MergeRequest.State.OPENED;
            case STATE_MERGED: return MergeRequest.State.MERGED;
            case STATE_CLOSED: return MergeRequest.State.CLOSED;
            default:
                throw new RuntimeException("MergeRequest.State not found for value " + state);
        }
    }
}
