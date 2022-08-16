package org.divulgit.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HashTagIdentifierUtilTest {

	@Test
	public void testIdentifingHashTags() {
		String comment = "Este comentário é muito legal #divulgar #legal";
		
		List<String> hashTags = HashTagIdentifierUtil.extractHashTag(comment);
		
		assertEquals(2, hashTags.size());
	}

	@Test
	public void testIdentifingHashTagsInBegin() {
		String comment = "#teste Este comentário é muito legal";

		List<String> hashTags = HashTagIdentifierUtil.extractHashTag(comment);

		assertEquals(1, hashTags.size());
	}
	
	@Test
	public void testContainsTwoHashTags() {
		String comment = "Este comentário é muito legal #divulgar #legal";
		
		boolean contains = HashTagIdentifierUtil.containsHashTag(comment);
		
		assertTrue(contains);
	}
	
	@Test
	public void testContainsOneHashTag() {
		String comment = "Este comentário é muito legal #divulgar";
		
		boolean contains = HashTagIdentifierUtil.containsHashTag(comment);
		
		assertTrue(contains);
	}
}
