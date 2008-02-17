package org.apache.abdera.protocol.server.adapters.hibernate;

import org.junit.internal.runners.TextListener;
import org.junit.runner.JUnitCore;

public class TestSuite {
	public static void main(String[] args) {
	    JUnitCore runner = new JUnitCore();
	    runner.addListener(new TextListener(System.out));
	    runner.run(HibernateCollectionAdapterTest.class);
	  }
}
