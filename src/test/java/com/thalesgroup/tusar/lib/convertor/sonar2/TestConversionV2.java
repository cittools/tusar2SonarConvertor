package com.thalesgroup.tusar.lib.convertor.sonar2;

import com.thalesgroup.tusar.lib.convertor.AbstractTest;
import org.junit.Test;

/**
 * @author Gregory Boissinot
 */
public class TestConversionV2 extends AbstractTest {

    @Test
    public void testcase1() throws Exception {
        convert2SonarV2("testcase1/input.xml", "testcase1/result.xml");
    }

    @Test
    public void testcase2() throws Exception {
        convert2SonarV2("testcase2/input.xml", "testcase2/result.xml");
    }
}