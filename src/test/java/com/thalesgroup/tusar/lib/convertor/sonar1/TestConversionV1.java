package com.thalesgroup.tusar.lib.convertor.sonar1;

import com.thalesgroup.tusar.lib.convertor.AbstractTest;
import org.junit.Test;

/**
 * @author Gregory Boissinot
 */
public class TestConversionV1 extends AbstractTest {

    @Test
    public void testcase1() throws Exception {
        convert2SonarV1("testcase1/input.xml", "testcase1/result.xml");
    }

    @Test
    public void testcase2() throws Exception {
        convert2SonarV1("testcase2/input.xml", "testcase2/result.xml");
    }

    @Test
    public void testcase3() throws Exception {
        convert2SonarV1("testcase3/input.xml", "testcase3/result.xml");
    }

    @Test
    public void testcase4() throws Exception {
        convert2SonarV1("testcase4/input.xml", "testcase4/result.xml");
    }

    @Test
    public void testcase5() throws Exception {
        convert2SonarV1("testcase5/input.xml", "testcase5/result.xml");
    }

    @Test
    public void testcase6() throws Exception {
        convert2SonarV1("testcase6/input.xml", "testcase6/result.xml");
    }

    @Test
    public void testcase7() throws Exception {
        convert2SonarV1("testcase7/input.xml", "testcase7/result.xml");
    }

    @Test
    public void testcase8() throws Exception {
        convert2SonarV1("testcase8/input.xml", "testcase8/result.xml");
    }

    @Test
    public void testcase9() throws Exception {
        convert2SonarV1("testcase9/input.xml", "testcase9/result.xml");
    }

}
