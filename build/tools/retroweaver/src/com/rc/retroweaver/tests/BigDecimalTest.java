package com.rc.retroweaver.tests;

import java.math.BigDecimal;

public class BigDecimalTest extends AbstractTest {

	public void testInt() {
		int i = Integer.MAX_VALUE;
		
		BigDecimal bd = new BigDecimal(i);
		
		assertEquals("testInt", bd.toString(), Integer.toString(i));
	}

	public void testLong() {
		long l = Long.MAX_VALUE;
		
		BigDecimal bd = new BigDecimal(l);
		
		assertEquals("testLong", bd.toString(), Long.toString(l));
	}
	
}
