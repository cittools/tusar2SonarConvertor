package com.thalesgroup.tusar.lib.convertor;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Before;

import java.io.*;

public class AbstractTest {

    @Before
    public void setUp() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalizeWhitespace(true);
        XMLUnit.setIgnoreComments(true);
    }


    private String readXmlAsString(File input)
            throws IOException {
        String xmlString = "";

        if (input == null) {
            throw new IOException("The input stream object is null.");
        }

        FileInputStream fileInputStream = new FileInputStream(input);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            xmlString += line + "\n";
            line = bufferedReader.readLine();
        }
        fileInputStream.close();
        fileInputStream.close();
        bufferedReader.close();

        return xmlString;
    }


    public void convert2SonarV1(String inputXMLPath, String expectedResultPath) throws Exception {
        Convertor convertor = Convertor.getInstance();

        File out = convertor.convert2SonarV1(new File(this.getClass().getResource(inputXMLPath).toURI()));

        Diff myDiff = new Diff(readXmlAsString(new File(this.getClass().getResource(expectedResultPath).toURI())), readXmlAsString(out));
        Assert.assertTrue("XSL transformation did not work" + myDiff, myDiff.similar());
    }

    public void convert2SonarV2(String inputXMLPath, String expectedResultPath) throws Exception {
        Convertor convertor = Convertor.getInstance();

        File out = convertor.convert2SonarV2(new File(this.getClass().getResource(inputXMLPath).toURI()));

        Diff myDiff = new Diff(readXmlAsString(new File(this.getClass().getResource(expectedResultPath).toURI())), readXmlAsString(out));
        Assert.assertTrue("XSL transformation did not work" + myDiff, myDiff.similar());
    }


    public void convertAndValidateSonarV1_SonarV2(String inputXMLPath, String expectedResultPath) throws Exception {
        Convertor convertor = Convertor.getInstance();
        File out = convertor.convert_sonar_v1_to_sonar_v2(readXmlAsString(new File(this.getClass().getResource(inputXMLPath).toURI())));
        Diff myDiff = new Diff(readXmlAsString(new File(this.getClass().getResource(expectedResultPath).toURI())), readXmlAsString(out));
        Assert.assertTrue("XSL transformation did not work" + myDiff, myDiff.similar());
    }


}
