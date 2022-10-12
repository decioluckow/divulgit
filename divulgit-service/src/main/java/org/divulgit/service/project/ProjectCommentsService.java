package org.divulgit.service.project;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;
import org.divulgit.model.util.UserProjectUtil;
import org.divulgit.service.mergeRequest.MergeRequestAggregationService;
import org.divulgit.service.RelatedContentService;
import org.divulgit.util.PeriodUtil;
import org.divulgit.vo.NotDiscussedByAuthorVO;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.divulgit.vo.ProjectIdMaxDiscussion;
import org.divulgit.vo.UserProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectCommentsService {

    private MergeRequestAggregationService mergeRequestAggregationService;
    private RelatedContentService relatedContentService;

    @Autowired
    private ProjectCommentsService(MergeRequestAggregationService mergeRequestAggregationService, RelatedContentService relatedContentService) {
        this.mergeRequestAggregationService = mergeRequestAggregationService;
        this.relatedContentService = relatedContentService;
    }

    public List<UserProjectVO> populateCommentsSum(User user, List<Project> projects) {
        List<String> projectIds = projects.stream().map(p -> p.getId()).collect(Collectors.toList());
        var projectCommentsSumMap =  mergeRequestAggregationService.sumProjectComments(projectIds);
        var projectCommentsMaxDiscussion = mergeRequestAggregationService.maxCommentDiscussedOn(projectIds);
        var projectAuthorCount = relatedContentService.findUndiscussedByAuthor(user);
        List<UserProjectVO> projectComments = new ArrayList<>();
        for (Project project : projects) {
        	UserProject.State userProjectState = UserProjectUtil.getState(user.getUserProjects(), project.getId());
        	ProjectIdCommentsSum projectCommentsSum = projectCommentsSumMap.get(project.getId());
            NotDiscussedByAuthorVO notDiscussedByAuthorVO = projectAuthorCount.get(project.getId());
            ProjectIdMaxDiscussion projectIdMaxDiscussion = projectCommentsMaxDiscussion.get(project.getId());
            UserProjectVO.UserProjectVOBuilder userProjectVOBuilder = UserProjectVO.builder().project(project).state(userProjectState);

            if (projectCommentsSum != null) {
                userProjectVOBuilder.commentsDiscussed(projectCommentsSum.getCommentsDiscussed()).commentsTotal(projectCommentsSum.getCommentsTotal());
            }

            if (projectIdMaxDiscussion != null && projectIdMaxDiscussion.getMaxDiscussedOn() != null) {
                LocalDate maxDiscussedOn = projectIdMaxDiscussion.getMaxDiscussedOn();
                Period period = Period.between(maxDiscussedOn, LocalDate.now());
                userProjectVOBuilder.durationFromLastDiscussion(PeriodUtil.formatDuration(period));
            }

            if (notDiscussedByAuthorVO != null) {
                userProjectVOBuilder.mergeRequestsNotDiscussedByAuthor(notDiscussedByAuthorVO.getMergeRequestCount());
                userProjectVOBuilder.commentsNotDiscussedByAuthor(notDiscussedByAuthorVO.getCommentCount());
            }

            if (Objects.nonNull(project.getLastScan())) {
                Period period = Period.between(project.getLastScan().toLocalDate(), LocalDate.now());
                userProjectVOBuilder.durationFromLastScan(PeriodUtil.formatDuration(period));
            }
            projectComments.add(userProjectVOBuilder.build());
        }
        return projectComments;
    }
}
