package com.rc.retroweaver.tests;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentTest extends AbstractTest {

	public void testConcurrent() {
		// Should not generate a warning
		ConcurrentHashMap map = new ConcurrentHashMap();

		assertNotNull("testConcurrent", map);
	}

	public void testConcurrentClassLiteral() {
		ConcurrentHashMap map = new ConcurrentHashMap();
		assertEquals("testConcurrentClassLiteral ConcurrentHashMap", ConcurrentHashMap.class, map.getClass());
	}

	public void testException() {
		try {
			throw new CancellationException();
		} catch (CancellationException e) {
			assertNotNull("testException", e);
		}
	}

}
