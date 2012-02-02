package com.thalesgroup.tusar.lib.convertor;

import com.thalesgroup.dtkit.util.converter.ConversionService;
import com.thalesgroup.dtkit.util.converter.ConversionServiceFactory;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Convertor {

    private static Convertor instance;

    private ConversionService conversionService;

    private Convertor() {
        this.conversionService = ConversionServiceFactory.getInstance();
    }

    public static synchronized Convertor getInstance() {
        if (instance == null) {
            instance = new Convertor();
        }
        return instance;
    }

    private boolean isTusarForVersion(File file, int version) {
        try {
            if (version != 1) {
                JAXBContext jc = JAXBContext.newInstance("com.thalesgroup.tusar.v" + version);
                jc.createUnmarshaller().unmarshal(file);
            } else {
                JAXBContext jc = JAXBContext.newInstance("com.thalesgroup.dtkit.tusar.model");
                jc.createUnmarshaller().unmarshal(file);
            }
            return true;
        } catch (JAXBException je) {
            return false;
        }
    }

    private String removeNamespace(File inputFile) {
        return conversionService.convertAndReturn(new StreamSource(this.getClass().getResourceAsStream("remove-namespace.xsl")), inputFile, null);
    }

    public File convert(File inputFile) throws IOException {

        if (isTusarForVersion(inputFile, 6)
                || isTusarForVersion(inputFile, 5)
                || isTusarForVersion(inputFile, 4)
                || isTusarForVersion(inputFile, 3)
                || isTusarForVersion(inputFile, 2)
                || isTusarForVersion(inputFile, 1)
                ) {
            String sonarV1 = convert2SonarV1AndGetContent(inputFile);
            String sonarV2 = convertAndGetOutput(sonarV1, "sonar_v1_to_sonar_v2.xsl");
            return convert_sonar_v2_to_sonar_v3(sonarV2);
        } else {
            //return convert2SonarV2(inputFile);
        	String sonarV2 = convert2SonarV2AndGetContent(inputFile);
            return convert_sonar_v2_to_sonar_v3(sonarV2);
        }
    }


    public File convert2SonarV2(File inputFile) throws IOException {
        // Remove Namespace if needed
        String out = conversionService.convertAndReturn(new StreamSource(this.getClass().getResourceAsStream("remove-namespace.xsl")), inputFile, null);
        return convertAndGetOutputFile(out, "tusar-v7-v8-2Sonarv2.xsl");
    }

    public File convert2SonarV1(File inputFile) throws IOException {
        File file = File.createTempFile("convertFile", "sonar");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(convert2SonarV1AndGetContent(inputFile).getBytes());
        return file;
    }

    private String convert2SonarV1AndGetContent(File inputFile) throws IOException {

        // Remove Namespace if needed
        String out = conversionService.convertAndReturn(new StreamSource(this.getClass().getResourceAsStream("remove-namespace.xsl")), inputFile, null);

        //Tusar version v4,v5,v6
        if (isTusarForVersion(inputFile, 4)
                || isTusarForVersion(inputFile, 5)
                || isTusarForVersion(inputFile, 6)) {
            return convertAndGetOutput(out, "tusar-v4-v5-v6-2Sonarv1.xsl");
        }

        //Tusar v1,v2,v3
        else if (isTusarForVersion(inputFile, 1)
                || isTusarForVersion(inputFile, 2)
                || isTusarForVersion(inputFile, 3)) {
            return convertAndGetOutput(out, "tusar-v1-v2-v3-2Sonarv1.xsl");
        }

        throw new IllegalArgumentException("There is no convert to Soanr v1 for the input file " + inputFile.getAbsolutePath());
    }
    
    private String convert2SonarV2AndGetContent(File inputFile) throws IOException {
    	// Remove Namespace if needed
        String out = conversionService.convertAndReturn(new StreamSource(this.getClass().getResourceAsStream("remove-namespace.xsl")), inputFile, null);
        return convertAndGetOutput(out, "tusar-v7-v8-2Sonarv2.xsl");
    }

    public File convert_sonar_v1_to_sonar_v2(String input) throws IOException {
        String xsl = "sonar_v1_to_sonar_v2.xsl";
        return convertAndGetOutputFile(input, xsl);
    }
    
    public File convert_sonar_v2_to_sonar_v3(String input) throws IOException {
        String xsl = "sonar_v2_to_sonar_v3.xsl";
        return convertAndGetOutputFile(input, xsl);
    }

    private File convertAndGetOutputFile(String input, String xsl) throws IOException {
        File out = File.createTempFile("convertFile", "sonar");
        ByteArrayInputStream inputBytes = new ByteArrayInputStream(input.getBytes());
        conversionService.convert(new StreamSource(this.getClass().getResourceAsStream(xsl)), new InputSource(inputBytes), out, null);
        return out;
    }

    private String convertAndGetOutput(String input, String xsl) throws IOException {
        ByteArrayInputStream inputBytes = new ByteArrayInputStream(input.getBytes());
        return conversionService.convertAndReturn(new StreamSource(this.getClass().getResourceAsStream(xsl)), new InputSource(inputBytes), null);

    }

}
