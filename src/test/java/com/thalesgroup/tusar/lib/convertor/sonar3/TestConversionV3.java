package com.thalesgroup.tusar.lib.convertor.sonar3;

import org.junit.Test;

import com.thalesgroup.tusar.lib.convertor.AbstractTest;

public class TestConversionV3 extends AbstractTest {
	
	@Test
	public void testcase1() throws Exception{
		convert2SonarV3("testcase1/tusar-v9.xml", "testcase1/result.xml");
	}
	
	@Test
	public void testcase2() throws Exception{
		convert2SonarV3("testcase2/tusar-v3.xml", "testcase2/result.xml");
	}

}
