package com.thalesgroup.tusar.lib.convertor.sonar4;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.thalesgroup.tusar.lib.convertor.AbstractTest;
import com.thalesgroup.tusar.lib.convertor.Convertor;

public class TestConversionV4 extends AbstractTest {
	
	@Test
	public void testcase1() throws Exception{
		convert2SonarV4("testcase1/tusar-v3.xml", "testcase1/result.xml");
	}
	
	@Test
	public void testcase2() throws Exception{
		convert2SonarV4("testcase2/tusar-v10.xml", "testcase2/result.xml");
	}

}
