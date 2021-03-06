package org.divulgit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.divulgit.model.Project;
import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;
import org.divulgit.model.User.UserProject.State;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.service.mergeRequest.MergeRequestAggregationService;
import org.divulgit.service.project.ProjectCommentsService;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.divulgit.vo.UserProjectVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectCommentsServiceTest {

    private static final String PROJECT_ID_1 = "1";
    private static final Project PROJECT_1 = Project.builder().id(PROJECT_ID_1).build();
    private static final ProjectIdCommentsSum PROJECT_1_COMMENTS_SUM = ProjectIdCommentsSum.builder().id(PROJECT_ID_1).commentsTotal(100).commentsDiscussed(10).build();

    private static final String PROJECT_ID_2 = "2";
    private static final Project PROJECT_2 = Project.builder().id(PROJECT_ID_2).build();
    final ProjectIdCommentsSum PROJECT_2_COMMENTS_SUM = ProjectIdCommentsSum.builder().id(PROJECT_ID_2).commentsTotal(200).commentsDiscussed(20).build();

    private static final UserProject USER_PROJECT_1 = UserProject.builder().projectId(PROJECT_ID_1).state(State.ACTIVE).build();
    private static final UserProject USER_PROJECT_2 = UserProject.builder().projectId(PROJECT_ID_2).state(State.ACTIVE).build();
    
    private static final List<UserProject> USER_PROJECTS = Arrays.asList(USER_PROJECT_1, USER_PROJECT_2);

    private static final User USER = User.builder().userProjects(USER_PROJECTS).build();

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MergeRequestAggregationService mergeRequestAggregationService;

    @Mock
    private RelatedContentService relatedContentService;

    @InjectMocks
    private ProjectCommentsService service;

    @Test
    public void testPopulate() {
    	
        Map<String, ProjectIdCommentsSum> projectIdComments = Map.of(
                PROJECT_ID_1, PROJECT_1_COMMENTS_SUM,
                PROJECT_ID_2, PROJECT_2_COMMENTS_SUM);
        List<Project> projects = Arrays.asList(PROJECT_1, PROJECT_2);
        Mockito.when(mergeRequestAggregationService.sumProjectComments(Mockito.anyList())).thenReturn(projectIdComments);

        List<UserProjectVO> projectsCommentsSum = service.populateCommentsSum(USER, projects);

        var project1 = findById(PROJECT_ID_1, projectsCommentsSum);
        assertEquals("1", project1.getId());
        assertEquals(100, project1.getCommentsTotal());
        assertEquals(10, project1.getCommentsDiscussed());

        var project2 = findById(PROJECT_ID_2, projectsCommentsSum);
        assertEquals("2", project2.getId());
        assertEquals(200, project2.getCommentsTotal());
        assertEquals(20, project2.getCommentsDiscussed());

        assertEquals(2, projectsCommentsSum.size());
    }

    @Test
    public void testEmpty() {
        Map<String, ProjectIdCommentsSum> projectIdComments = Map.of();
        List<Project> projects = new ArrayList<>();
        Mockito.when(mergeRequestAggregationService.sumProjectComments(Mockito.anyList())).thenReturn(projectIdComments);

        List<UserProjectVO> projectsCommentsSum = service.populateCommentsSum(USER, projects);

        assertEquals(0, projectsCommentsSum.size());
    }

    private UserProjectVO findById(String id, List<UserProjectVO> projectsCommentsSum) {
        return projectsCommentsSum.stream().filter(pc -> pc.getId().equals(id)).findFirst().get();
    }
}
