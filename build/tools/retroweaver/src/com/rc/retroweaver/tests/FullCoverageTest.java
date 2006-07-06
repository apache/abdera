package com.rc.retroweaver.tests;

import com.rc.retroweaver.runtime.Autobox;
import com.rc.retroweaver.runtime.ClassMethods;
import com.rc.retroweaver.runtime.IterableMethods;

/**
 * Ensures that the entire retroweaver code is tested. Note: this is the only
 * test class that should import the retroweaver classes directly.
 */

public class FullCoverageTest extends AbstractTest {

	public void testFullCoverage() {
		Autobox box = new Autobox();
		assertNotNull("Autoboxing", box);

		ClassMethods c = new ClassMethods();
		assertNotNull("ClassMethods", c);

		try {
			IterableMethods.iterator("");
			fail("IterableMethods should raise an UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
		}
	}

}
