package org.divulgit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.divulgit.model.Project;
import org.divulgit.model.User.UserProject;
import org.divulgit.model.util.UserProjectUtil;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.divulgit.vo.UserProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectCommentsService {

    private MergeRequestAggregationService mergeRequestAggregationService;

    @Autowired
    private ProjectCommentsService(MergeRequestAggregationService mergeRequestAggregationService) {
        this.mergeRequestAggregationService = mergeRequestAggregationService;
    }

    public List<UserProjectVO> populateCommentsSum(List<UserProject> userProjects, List<Project> projects) {
        List<String> projectIds = projects.stream().map(p -> p.getId()).collect(Collectors.toList());
        var projectCommentsSumMap =  mergeRequestAggregationService.sumProjectComments(projectIds);
        List<UserProjectVO> projectComments = new ArrayList<>();
        for (Project project : projects) {
        	UserProject.State userProjectState = UserProjectUtil.getState(userProjects, project.getId());
        	ProjectIdCommentsSum projectCommentsSum = projectCommentsSumMap.get(project.getId());
            if (projectCommentsSum != null) {
                projectComments.add(new UserProjectVO(project, userProjectState, projectCommentsSum.getCommentsTotal(), projectCommentsSum.getCommentsDiscussed()));
            } else {
                projectComments.add(new UserProjectVO(project, userProjectState, 0, 0));
            }
        }
        return projectComments;
    }
}
