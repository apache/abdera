package com.rc.retroweaver.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ensures that Retroweaver can handle inner classes ok.
 */

public class InnerClassTest extends AbstractTest {

	public void testAnonymous() {
		String name = new InnerClass().getAnonymousClassName();
		assertEquals("testAnonymous", name,
				"com.rc.retroweaver.tests.InnerClass$1");
	}

	public void testNamedInnerClass() {
		String name = new InnerClass().namedInnerClassName;
		assertEquals("testNamedInnerClass", name,
				"com.rc.retroweaver.tests.InnerClass$NamedInnerClass");
		name = new InnerClass().namedInnerClass.getClass().getName();
		assertEquals("testNamedInnerClass2", name,
				"com.rc.retroweaver.tests.InnerClass$NamedInnerClass");
	}

	public void testStaticInnerClass() {
		String name = new InnerClass().staticInnerClass.getClass().getName();
		assertEquals("testStaticInnerClass", name,
				"com.rc.retroweaver.tests.InnerClass$StaticInnerClass");
		name = InnerClass.staticInnerClassName;
		assertEquals("testStaticInnerClass2", name,
				"com.rc.retroweaver.tests.InnerClass$StaticInnerClass");
	}

}

class InnerClass {
	String anonymousClassName;

	String namedInnerClassName;

	NamedInnerClass namedInnerClass = new NamedInnerClass();

	static String staticInnerClassName;

	StaticInnerClass staticInnerClass = new StaticInnerClass();

	public String getAnonymousClassName() {
		ActionListener a = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				anonymousClassName = getClass().getName();
			}
		};
		a.actionPerformed(null);

		return anonymousClassName;
	}

	public class NamedInnerClass {
		public NamedInnerClass() {
			namedInnerClassName = getClass().getName();
		}
	}

	static class StaticInnerClass {
		public StaticInnerClass() {
			staticInnerClassName = getClass().getName();
		}
	}

}
