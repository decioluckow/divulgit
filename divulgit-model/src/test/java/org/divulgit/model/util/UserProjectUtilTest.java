package org.divulgit.model.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.divulgit.model.User;
import org.divulgit.model.User.UserProject;
import org.junit.jupiter.api.Test;

public class UserProjectUtilTest {
	private static final UserProject USER_PROJECT_A = UserProject.builder().projectId("a").state(UserProject.State.NEW).build();
	private static final UserProject USER_PROJECT_B = UserProject.builder().projectId("b").state(UserProject.State.ACTIVE).build();
	private static final UserProject USER_PROJECT_C = UserProject.builder().projectId("c").state(UserProject.State.IGNORED).build();
	
	private static final User USER = User.builder().userProjects(Arrays.asList(USER_PROJECT_A, USER_PROJECT_B, USER_PROJECT_C)).build();
	
	@Test
	public void testFindByStateNew() {
		
		List<String> projectIds = UserProjectUtil.findByState(USER, UserProject.State.NEW);
		
		assertTrue(projectIds.contains("a"));
		assertTrue(projectIds.size() == 1);
	}
	
	@Test
	public void testFindByStateActive() {
		
		List<String> projectIds = UserProjectUtil.findByState(USER, UserProject.State.ACTIVE);
		
		assertTrue(projectIds.contains("b"));
		assertTrue(projectIds.size() == 1);
	}
	
	@Test
	public void testFindByStateIgnored() {
		
		List<String> projectIds = UserProjectUtil.findByState(USER, UserProject.State.IGNORED);
		
		assertTrue(projectIds.contains("c"));
		assertTrue(projectIds.size() == 1);
	}
	
	
	@Test
	public void testFindByStateNewActive() {
		
		List<String> projectIds = UserProjectUtil.findByState(USER, UserProject.State.NEW, UserProject.State.ACTIVE);
		
		assertTrue(projectIds.contains("a"));
		assertTrue(projectIds.contains("b"));
		assertTrue(projectIds.size() == 2);
	}

}
