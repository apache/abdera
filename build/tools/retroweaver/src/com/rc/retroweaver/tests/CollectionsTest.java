package com.rc.retroweaver.tests;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CollectionsTest extends AbstractTest {

	public void testEmptyList() {
		List<Object> l = Collections.emptyList();
		// replaced with Collections.EMPTY_LIST

		assertEquals("testEmptyList", l.size(), 0);
	}

	public void testEmptySet() {
		Set<Object> s = Collections.emptySet();
		// replaced with Collections.EMPTY_SET

		assertEquals("testEmptySet", s.size(), 0);
	}

}
