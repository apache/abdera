package com.rc.retroweaver.tests;

public class AppendableTest extends AbstractTest {

	public void testAppendable() {
		try {
			// Should generate a warning - implementing interface from 1.5
			class Ap implements Appendable {
				public Appendable append(char c) {
					return null;
				}

				public Appendable append(CharSequence csq) {
					return null;
				}

				public Appendable append(CharSequence csq, int start, int end) {
					return null;
				}
			}

			new Ap();

			fail("testAppendable should raise a NoClassDefFoundError");
		} catch (NoClassDefFoundError e) {
			success("testAppendable");
		}
	}

}
