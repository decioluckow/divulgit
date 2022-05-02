package org.divulgit.controller.helper;

import java.util.Optional;

import org.divulgit.model.MergeRequest;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.model.User;
import org.divulgit.repository.MergeRequestRepository;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.repository.RemoteRepository;
import org.divulgit.repository.UserRepository;
import org.divulgit.security.UserAuthentication;
import org.divulgit.security.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class EntityLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RemoteRepository remoteRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MergeRequestRepository mergeRequestRepository;

    public User loadUser(Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        Optional<User> user = userRepository.findById(userDetails.getUser().getId());
        if (!user.isPresent()) {
            throw new IllegalStateException("User not found");
        }
        return user.get();
    }

    public UserDetails getUserDetails(Authentication authentication) {
        return ((UserAuthentication) authentication).getUserDetails();
    }

    public Remote loadRemote(String remoteId) {
        Optional<Remote> remote = remoteRepository.findById(remoteId);
        if (!remote.isPresent()) {
            throw new IllegalStateException("Remote not found");
        }
        return remote.get();
    }

    public Project loadProject(User user, String projectId) {
        final Optional<Project> project = projectRepository.findById(projectId);
        if (!project.isPresent())
            throw new IllegalStateException("Project " + projectId + " not found");
        if (user.getUserProjects().stream().noneMatch(up -> up.getProjectId().equals(project.get().getId())))
            throw new IllegalStateException("User don´t have access to the project");
        return project.get();
    }

    public void verifyProject(User user, String projectId) {
        loadProject(user, projectId);
    }

    public MergeRequest loadMergeRequest(Project project, String mergeRequestId) {
        Optional<MergeRequest> mergeRequest = mergeRequestRepository.findById(mergeRequestId);
        if (!mergeRequest.isPresent())
            throw new IllegalStateException("Merge Request not found");
        if (!mergeRequest.get().getProjectId().equals(project.getId()))
            throw new IllegalStateException("Merge request is not owned by the project");
        return mergeRequest.get();
    }

    public MergeRequest loadMergeRequest(String mergeRequestId) {
        Optional<MergeRequest> mergeRequest = mergeRequestRepository.findById(mergeRequestId);
        if (!mergeRequest.isPresent()) {
            throw new IllegalStateException("Merge Request not found");
        }
        return mergeRequest.get();
    }
}
