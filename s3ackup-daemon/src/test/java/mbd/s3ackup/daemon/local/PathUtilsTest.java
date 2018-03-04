package mbd.s3ackup.daemon.local;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PathUtilsTest {

	@Test
	public void testExtractRootFromPath() {
		assertEquals("abucket", PathUtils.extractRootFromPath("abucket"));
		assertEquals("abucket", PathUtils.extractRootFromPath("abucket/first"));
		assertEquals("abucket", PathUtils.extractRootFromPath("abucket/first/second"));

		assertEquals("abucket", PathUtils.extractRootFromPath("abucket/"));
		assertEquals("abucket", PathUtils.extractRootFromPath("abucket/first/"));
		assertEquals("abucket", PathUtils.extractRootFromPath("abucket/first/second/"));

		assertEquals("abucket", PathUtils.extractRootFromPath("/abucket"));
		assertEquals("abucket", PathUtils.extractRootFromPath("/abucket/first"));
		assertEquals("abucket", PathUtils.extractRootFromPath("/abucket/first/second"));

		assertEquals("abucket", PathUtils.extractRootFromPath("/abucket/"));
		assertEquals("abucket", PathUtils.extractRootFromPath("/abucket/first/"));
		assertEquals("abucket", PathUtils.extractRootFromPath("/abucket/first/second/"));
	}

	@Test
	public void testRemoveRootFromPath() {
		assertEquals("", PathUtils.removeRootFromPath("abucket"));
		assertEquals("first", PathUtils.removeRootFromPath("abucket/first"));
		assertEquals("first/second", PathUtils.removeRootFromPath("abucket/first/second"));

		assertEquals("", PathUtils.removeRootFromPath("abucket/"));
		assertEquals("first", PathUtils.removeRootFromPath("abucket/first/"));
		assertEquals("first/second", PathUtils.removeRootFromPath("abucket/first/second/"));

		assertEquals("", PathUtils.removeRootFromPath("/abucket"));
		assertEquals("first", PathUtils.removeRootFromPath("/abucket/first"));
		assertEquals("first/second", PathUtils.removeRootFromPath("/abucket/first/second"));

		assertEquals("", PathUtils.removeRootFromPath("/abucket/"));
		assertEquals("first", PathUtils.removeRootFromPath("/abucket/first/"));
		assertEquals("first/second", PathUtils.removeRootFromPath("/abucket/first/second/"));
	}
}
