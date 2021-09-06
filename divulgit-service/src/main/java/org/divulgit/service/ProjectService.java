package org.divulgit.service;

import com.google.common.collect.ImmutableList;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findByRemote(Remote remote) {
        return projectRepository.findByRemoteId(remote.getId());
    }

    public List<String> findIdByRemote(Remote remote) {
        List<Project> projects = projectRepository.findIdByRemoteId(remote.getId());
        return projects.stream().map(p -> p.getId()).collect(Collectors.toList());
    }

    public List<String> findExternalIdByRemote(Remote remote) {
        List<Project> projects = projectRepository.findExternalIdByRemoteId(remote.getId());
        return projects.stream().map(p -> p.getExternalId()).collect(Collectors.toList());
    }

    public List<Project> findAllById(List<String> ids) {
        return ImmutableList.copyOf(projectRepository.findAllById(ids));
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> saveAll(List<Project> projects) {
        return projectRepository.saveAll(projects);
    }
}
