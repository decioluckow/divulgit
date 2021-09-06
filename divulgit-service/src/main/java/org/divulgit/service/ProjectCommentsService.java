package org.divulgit.service;

import com.google.common.collect.Collections2;
import org.divulgit.model.Project;
import org.divulgit.model.Remote;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.vo.ProjectCommentsSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectCommentsService {

    private ProjectRepository projectRepository;
    private MergeRequestAggregationService mergeRequestAggregationService;

    @Autowired
    private ProjectCommentsService(ProjectRepository projectRepository, MergeRequestAggregationService mergeRequestAggregationService) {
        this.projectRepository = projectRepository;
        this.mergeRequestAggregationService = mergeRequestAggregationService;
    }

    public List<ProjectCommentsSum> populateCommentsSum(List<Project> projects) {
        List<String> projectIds = projects.stream().map(p -> p.getId()).collect(Collectors.toList());
        var projectCommentsSumMap =  mergeRequestAggregationService.sumProjectComments(projectIds);
        List<ProjectCommentsSum> projectComments = new ArrayList<>();
        for (Project project : projects) {
            var projectCommentsSum = projectCommentsSumMap.get(project.getId());
            projectComments.add(ProjectCommentsSum.builder()
                    .project(project)
                    .commentsTotal(projectCommentsSum.getCommentsTotal())
                    .commentsDiscussed(projectCommentsSum.getCommentsDiscussed()).build());
        }
        return projectComments;
    }
}
