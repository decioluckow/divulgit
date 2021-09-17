package org.divulgit.service;

import org.divulgit.model.Project;
import org.divulgit.repository.ProjectRepository;
import org.divulgit.vo.ProjectCommentsSum;
import org.divulgit.vo.ProjectIdCommentsSum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectCommentsServiceTest {

    private static final String PROJECT_ID_1 = "1";
    private static final Project PROJECT_1 = Project.builder().id(PROJECT_ID_1).build();
    private static final ProjectIdCommentsSum PROJECT_1_COMMENTS_SUM = ProjectIdCommentsSum.builder().id(PROJECT_ID_1).commentsTotal(100).commentsDiscussed(10).build();

    private static final String PROJECT_ID_2 = "2";
    private static final Project PROJECT_2 = Project.builder().id(PROJECT_ID_2).build();
    final ProjectIdCommentsSum PROJECT_2_COMMENTS_SUM = ProjectIdCommentsSum.builder().id(PROJECT_ID_2).commentsTotal(200).commentsDiscussed(20).build();


    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MergeRequestAggregationService mergeRequestAggregationService;

    @InjectMocks
    private ProjectCommentsService service;

    @Test
    public void testPopulate() {
        Map<String, ProjectIdCommentsSum> projectIdComments = Map.of(
                PROJECT_ID_1, PROJECT_1_COMMENTS_SUM,
                PROJECT_ID_2, PROJECT_2_COMMENTS_SUM);
        List<Project> projects = Arrays.asList(PROJECT_1, PROJECT_2);
        Mockito.when(mergeRequestAggregationService.sumProjectComments(Mockito.anyList())).thenReturn(projectIdComments);

        List<ProjectCommentsSum> projectsCommentsSum = service.populateCommentsSum(projects);

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

        List<ProjectCommentsSum> projectsCommentsSum = service.populateCommentsSum(projects);

        assertEquals(0, projectsCommentsSum.size());
    }

    private ProjectCommentsSum findById(String id, List<ProjectCommentsSum> projectsCommentsSum) {
        return projectsCommentsSum.stream().filter(pc -> pc.getId().equals(id)).findFirst().get();
    }
}
