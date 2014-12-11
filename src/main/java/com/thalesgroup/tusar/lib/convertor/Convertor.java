package com.thalesgroup.tusar.lib.convertor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import org.jenkinsci.lib.dtkit.util.converter.ConversionException;
import org.jenkinsci.lib.dtkit.util.converter.ConversionServiceFactory;

public class Convertor {

	public static final int FIRST_SUPPORTED_VERSION = 1;

	public static final int LAST_SUPPORTED_VERSION = 12;

	private static final String TUSAR_VERSION_ATTRIBUTE = "version";

	private static final String NAMESPACE_URL_PREFIX = "http://www.thalesgroup.com/tusar";

	/**
	 * The TUSAR namespace value format, allowing us to know its version.
	 */
	public static final Pattern TUSAR_NAMESPACE_FORMAT = Pattern.compile(Pattern.quote(NAMESPACE_URL_PREFIX)
	        + "/v(\\d+)");

	private static Logger logger = LoggerFactory.getLogger(Convertor.class);

	/**
	 * A XSLT conversion to a given version.
	 */
	private class UpgradeStep {

		private final int toVersion;

		private final Transformer transformer;

		public UpgradeStep(int toVersion, Transformer transformer) {
			this.toVersion = toVersion;
			this.transformer = transformer;
		}

		public Document apply(Document input) throws ConversionException {
			logger.debug("Applying XSLT '{}'", transformer);
			Document output = transformer.apply(removeNamespace.apply(input));
			output.getDocumentElement().setAttribute(TUSAR_VERSION_ATTRIBUTE, Integer.toString(toVersion));
			return output;
		}
	}

	private static Convertor instance;

	public static synchronized Convertor getInstance() {
		if (instance == null) {
			instance = new Convertor();
		}
		return instance;
	}

	/*
	 * For test purpose. Since XSLT parsing and compilation are cached, using
	 * the singleton instance is not the same thing as constructing a new
	 * Convertor for each conversion (see Transformer.Context inner class.)
	 */
	Convertor() {
	}

	private static class SchemaInfo {

		public final Schema schema;
		public final NamespaceSet namespaceSet;

		public SchemaInfo(Schema schema, NamespaceSet namespaceSet) {
			this.schema = schema;
			this.namespaceSet = namespaceSet;
		}
	}

	private final Map<Integer, SchemaInfo> schemaInfos = new HashMap<Integer, SchemaInfo>();
	{
		for (int version = FIRST_SUPPORTED_VERSION; version <= LAST_SUPPORTED_VERSION; ++version) {
			String xsdPath = "/com/thalesgroup/dtkit/tusar/model/xsd/tusar-" + version + ".xsd";
			try {
				URL xsdUrl = getClass().getResource(xsdPath);
				if (xsdUrl != null) {
					Schema schema = XmlHelper.readSchema(xsdUrl);
					NamespaceSet namespaceSet = new NamespaceSet(xsdPath, NAMESPACE_URL_PREFIX);
					schemaInfos.put(version, new SchemaInfo(schema, namespaceSet));
				} else {
					throw new ConversionException("Schema not found: " + xsdPath);
				}
			} catch (SAXException e) {
				throw new ConversionException("Cannot read schema: " + xsdPath);
			}
		}
	}

	private final Transformer identity = new Transformer("id.xsl");

	private final Transformer removeNamespace = new Transformer("remove-namespace.xsl");

	/**
	 * For each known version, we search for a conversion to a higher version
	 * with he minimu gap (that is, from N to M with M-N minimal). In practise,
	 * only one such conversion shall exist. If none are found, we choose a N to
	 * N+1 identity conversion.
	 */
	private final Map<Integer, UpgradeStep> steps = new HashMap<Integer, UpgradeStep>();
	{
		for (int version = FIRST_SUPPORTED_VERSION; version < LAST_SUPPORTED_VERSION; ++version) {
			Transformer transform = null;
			for (int toVersion = version + 1; toVersion <= LAST_SUPPORTED_VERSION; ++toVersion) {
				String xsl = String.format("v%d_to_v%d.xsl", version, toVersion);
				URL xslUrl = getClass().getResource(xsl);
				if (xslUrl != null) {
					transform = new Transformer(xslUrl, xsl);
					steps.put(version, new UpgradeStep(toVersion, transform));
					break;
				}
			}
			if (transform == null) {
				steps.put(version, new UpgradeStep(version + 1, identity));
			}
		}
	}

	public com.thalesgroup.tusar.v12.Tusar upgradeToLastVersionModel(URL tusarUrl) throws ConversionException {
		int lastVersion = 12; // Informally equal to LAST_SUPPORTED_VERSION.
		Document tusarDocument = upgrade(tusarUrl, lastVersion);
		return (com.thalesgroup.tusar.v12.Tusar) toModel(tusarDocument, lastVersion);
	}

	public Document upgradeToLastVersion(URL tusarUrl) throws ConversionException {
		return upgrade(tusarUrl, LAST_SUPPORTED_VERSION);
	}

	public Document upgrade(URL tusarUrl, int toVersion) throws ConversionException {
		Document document;
		try {
			logger.debug("Parsing TUSAR file '{}'", tusarUrl);
			document = XmlHelper.readXml(tusarUrl);
		} catch (Exception e) {
			throw new ConversionException("Can't parse TUSAR input file " + tusarUrl, e);
		}
		Integer version = findTusarVersion(document);
		if (version != null) {
			return upgrade(document, version, toVersion);
		} else {
			throw new ConversionException("Failed to identify version for TUSAR file " + tusarUrl);
		}
	}

	private Document upgrade(Document document, int version, int toVersion) throws ConversionException {
		while (true) {
			document = removeNamespace.apply(document);
			/*
			 * Namespaces are mandatory for a proper validation, but also
			 * annoying when writing the XSL and, anyway, almost never provided
			 * by the user. That's why we remove them before transforming the
			 * document and add them back here. We known that namespaces to use
			 * by parsing the relevant schemae for each version (have a look at
			 * the NamespaceSet class). Setting the right namespace for each
			 * element is easy since the prefixes used for namespace match the
			 * element name (if an element has no matching namespace, it uses
			 * its parent's). This is an informal rule which happens to be
			 * followed by all TUSAR schemae starting with version 2. Just
			 * continu following it and everything will be fine.
			 */
			if (version > 1) {
				XmlHelper.autoQualify(document, schemaInfos.get(version).namespaceSet.getNamespaces());
			}
			validate(document, version);
			if (version < toVersion) {
				UpgradeStep step = steps.get(version);
				if (step != null) {
					document = step.apply(document);
					version = step.toVersion;
					if (logger.isTraceEnabled()) {
						logger.trace("Output document:\n{}", XmlHelper.dumpXml(document));
					}
				} else {
					throw new ConversionException("No TUSAR conversion found for TUSAR version " + version);
				}
			} else {
				return document;
			}
		}
	}

	private void validate(Document tusarDocument, int version) {
		try {
			XmlHelper.validate(schemaInfos.get(version).schema, tusarDocument);
			logger.debug("The document is a valid TUSAR v{} document", version);
		} catch (SAXException e) {
			logger.error("Output document:\n{}", XmlHelper.dumpXml(tusarDocument));
			throw new ConversionException("The produced output cannot be validated against TUSAR schema v" + version, e);
		} catch (IOException e) {
			throw new ConversionException("When validating transformed output with TUSAR schema v" + version, e);
		}
	}

	Integer findTusarVersion(Document tusarDocument) {
		String expectedRootName = "tusar";
		Element rootElement = tusarDocument.getDocumentElement();
		String rootName = rootElement.getLocalName();
		if (expectedRootName.equalsIgnoreCase(rootName)) {

			Integer namespaceVersion = getNamespaceVersion(rootElement);
			Integer explicitVersion = getExplicitVersion(rootElement);

			Integer version = null;
			/*
			 * Namespace is regarded as the most reliable way to know the
			 * version, in particular since the version attribute became
			 * optional between version 1 and 2.
			 */
			if (namespaceVersion != null) {
				if (explicitVersion == null || explicitVersion.equals(namespaceVersion)) {
					version = namespaceVersion;
				} else {
					throw new ConversionException("TUSAR explicit version is " + expectedRootName
					        + ", but namespace version used is " + namespaceVersion);
				}
			}
			/*
			 * If no namespace is defined, we go for the version number.
			 */
			else if (explicitVersion != null) {
				version = explicitVersion;
			}

			/*
			 * Failing that, we are left with a brutal duck typing comparison.
			 */
			if (version == null) {
				logger.warn("Unidentified TUSAR version (no TUSAR namespace provided neither explicit version)");
				version = guessTusarVersion(tusarDocument);
			}

			return version;
		} else {
			throw new ConversionException("Expected root name of a TUSAR report is " + expectedRootName + ", but "
			        + rootElement + " found.");
		}
	}

	private Integer getExplicitVersion(Element tusar) {
		String versionAttributeValue = tusar.getAttribute(TUSAR_VERSION_ATTRIBUTE);
		if (versionAttributeValue != null && !versionAttributeValue.isEmpty()) {
			try {
				int version = Integer.parseInt(versionAttributeValue);
				logger.debug("Explicitly identified TUSAR version is {}", version);
				return version;
			} catch (NumberFormatException e) {
				logger.warn("Version attribute found but with a invalid value: "+ versionAttributeValue);
			}
		}
		return null;
	}

	private Integer getNamespaceVersion(Element tusar) {
		String namespaceUri = tusar.getNamespaceURI();
		if (namespaceUri != null) {
			Matcher matcher = TUSAR_NAMESPACE_FORMAT.matcher(namespaceUri);
			if (matcher.matches()) {
				String value = matcher.group(1);
				try {
					int version = Integer.parseInt(value);
					logger.debug("Namespace identified TUSAR version is {}", version);
					return version;
				} catch (NumberFormatException e) {
					throw new ConversionException("Can't happen since " + value + " shall be a number thanks to regex "
					        + TUSAR_NAMESPACE_FORMAT);
				}
			}
		}
		return null;
	}

	/**
	 * Return the first TUSAR version whose corresponding JAXB mapping can load
	 * the provided TUSAR document.
	 */
	private Integer guessTusarVersion(Document tusarDocument) {
		List<Integer> sortedTusarVersions = new ArrayList<Integer>(steps.keySet());
		Collections.sort(sortedTusarVersions);
		Collections.reverse(sortedTusarVersions);
		for (int version : sortedTusarVersions) {
			try {
				tusarDocument = removeNamespace.apply(tusarDocument);
				if (version > 1) {
					XmlHelper.autoQualify(tusarDocument, schemaInfos.get(version).namespaceSet.getNamespaces());
				}
				toModel(tusarDocument, version);
				logger.debug("Guessed TUSAR version is {}", version);
				return version;
			} catch (ConversionException e) {
				continue;
			}
		}
		return null;
	}

	Object toModel(Document tusarDocument, int version) throws ConversionException {
		try {
			JAXBContext jc = getTusarJaxbContext(version);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			Thread.currentThread().setContextClassLoader(ConversionServiceFactory.class.getClassLoader());
			return unmarshaller.unmarshal(tusarDocument);
		} catch (JAXBException e) {
			throw new ConversionException("Can not parse TUSAR report: ", e);
		}
	}

	private JAXBContext getTusarJaxbContext(int version) throws JAXBException {
		String contextPath;
		if (version != 1) {
			/*
			 * Every version of TUSAR schema but the first use namespace and
			 * have their JAXB classes generated in the corresponding package.
			 * For the first version, the package is explictly provided in the
			 * POM and, unfortunately, doesn't quite match the same naming
			 * scheme as with the others.
			 */
			contextPath = "com.thalesgroup.tusar.v" + version;
		} else {
			contextPath = "com.thalesgroup.dtkit.tusar.model";
		}
		// Any class from the library would do the job.
		ClassLoader classLoader = com.thalesgroup.dtkit.tusar.model.ObjectFactory.class.getClassLoader();
		return JAXBContext.newInstance(contextPath, classLoader);
	}
}
