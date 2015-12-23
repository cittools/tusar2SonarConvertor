package com.thalesgroup.tusar.lib.convertor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ConversionTest {

	private File temporaryDirectory = new File("target/tmp");

	@Before
	public void setUp() {
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setNormalizeWhitespace(true);
		XMLUnit.setIgnoreComments(true);
	}

	@After
	public void tearDown() {
		if (temporaryDirectory.exists() && !deleteFolder(temporaryDirectory)) {
			throw new RuntimeException("Can't delete temporary test directory " + temporaryDirectory);
		}
	}

	@Test
	public void testFindTusarVersion() throws Exception {
		findVersion("tusar-v1.xml", 1);
		findVersion("tusar-v11.xml", 11);
	}

	private void findVersion(String tusarResourceName, Integer expectedVersion) throws ParserConfigurationException,
	        SAXException, IOException {
		Convertor convertor = new Convertor();
		URL tusarUrl = getClass().getResource(tusarResourceName);
		Assert.assertNotNull(tusarUrl);
		Document tusarDocument = XmlHelper.readXml(tusarUrl);
		Assert.assertEquals(expectedVersion, convertor.findTusarVersion(tusarDocument));
	}

	@Test
	public void testUpgradeFromVersion1ToVersion11() throws Exception {
		upgrade(11, "tusar-v1.xml", "tusar-v11.xml");
	}
	
	@Test
	public void testUpgradeFromVersion1ToVersion12() throws Exception {
		upgrade(12, "tusar-v1.xml", "tusar-v12.xml");
	}

	private void upgrade(int toSonarRevision, String inputResourceName, String expectedResultResourceName)
	        throws Exception {
		URL url = getClass().getResource(inputResourceName);
		Document result = Convertor.getInstance().upgrade(url, toSonarRevision);
		checkResult(result, expectedResultResourceName);
		Convertor.getInstance().toModel(result, toSonarRevision);
	}

	private void checkResult(Document result, String expectedResultResourceName) throws ParserConfigurationException,
	        SAXException, IOException {
		Document expectedResult = XmlHelper.readXml(getClass().getResource(expectedResultResourceName));

		Diff diff = new Diff(expectedResult, result);
		Assert.assertTrue("XSL transformation did not work " + diff, diff.similar());

		Assert.assertTrue(isFolderEmpty(temporaryDirectory));
	}

	private boolean deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // Some JVMs return null for empty directories
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					if (!file.delete()) {
						return false;
					}
				}
			}
		}
		return folder.delete();
	}

	private boolean isFolderEmpty(File folder) {
		File[] files = folder.listFiles();
		return files == null || files.length == 0;
	}
}
