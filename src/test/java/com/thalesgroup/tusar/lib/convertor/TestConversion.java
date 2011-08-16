package com.thalesgroup.tusar.lib.convertor;

import org.junit.Test;

public class TestConversion extends AbstractTest {

    @Test
    public void testcaseConvertSonarV12SonarV2() throws Exception {
        convertAndValidateSonarV1_SonarV2("testcaseSonar1/input.xml", "testcaseSonar1/result.xml");
    }
}
