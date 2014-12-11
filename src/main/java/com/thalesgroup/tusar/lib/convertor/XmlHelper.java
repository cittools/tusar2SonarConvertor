package com.thalesgroup.tusar.lib.convertor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A bunch of XML methods.
 */
class XmlHelper {
	
	private static final String GENERIC_BRANCH_COVERAGE = "generic-branch-coverage";
	private static final List<String> genericBranchCoverageTypes = new ArrayList<String>(6);
	
	static {
		genericBranchCoverageTypes.add("branch-coverage");
		genericBranchCoverageTypes.add("condition-coverage");
		genericBranchCoverageTypes.add("decision-coverage");
		genericBranchCoverageTypes.add("condition-decision-coverage");
		genericBranchCoverageTypes.add("modified-condition-decision-coverage");
		genericBranchCoverageTypes.add("multi-condition-coverage");
	}

	public static DocumentBuilderFactory newNamespaceAwareFactory() {
		/*
		 * TODO return DocumentBuilderFactory.newInstance(
		 * "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl",
		 * XmlHelper.class.getClassLoader());
		 */
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		/*
		 * Somewhat required for proper JAXB usage... Really? Why I've wrote
		 * that? Default value is false and it seems to works like a charm
		 * whatever the value.
		 */
		factory.setNamespaceAware(true);
		return factory;
	}

	/**
	 * Parse a XML file and return the resulting DOM tree.
	 */
	public static Document readXml(URL xmlUrl) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = newNamespaceAwareFactory();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream input = xmlUrl.openStream();
		try {
			return builder.parse(input);
		} finally {
			input.close();
		}
	}

	public static Schema readSchema(URL xsdUrl) throws SAXException {
		String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(language);
		return factory.newSchema(xsdUrl);
	}

	public static void validate(Schema schema, Document document) throws SAXException, IOException {
		schema.newValidator().validate(new DOMSource(document));
	}

	/**
	 * Serialize an XML document into a String (for debug purpose only!). the
	 * returned String could contains an error message instead if a problem as
	 * occurred during the serialization. This method is intended for debug /
	 * trace purpose because you really don't need to serialize XML at all in
	 * your code unless your planing to output it to a file. In this case, use
	 * {@link XmlHelper#writeXmlToFile(Document, File)} instead.
	 */
	public static String dumpXml(Document document) {
		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (TransformerException e) {
			return "XML dump failed: " + e.getMessage();
		}
	}

	/**
	 * Write a XML document down to a file.
	 */
	public static void writeXmlToFile(Document document, File file) throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(document), new StreamResult(file));
	}

	/**
	 * Qualification of every element in a DOM document. Previous namespaces are
	 * ignored.
	 * 
	 * @param document
	 *            The document to modify.
	 * @param namespaces
	 *            A map of prefix/URL where prefix are expected to match the
	 *            local name of the elements they qualify (if an element has no
	 *            corresponding prefix, it uses the namespace of its parent).
	 */
	public static void autoQualify(Document document, Map<String, String> namespaces) {
		Element root = document.getDocumentElement();
		for (Map.Entry<String, String> entry : namespaces.entrySet()) {
			String prefix = entry.getKey();
			String url = entry.getValue();
			StringBuilder attributName = new StringBuilder("xmlns");
			if (prefix != null) {
				attributName.append(':').append(prefix);
			}
			root.setAttribute(attributName.toString(), url);
		}
		autoQualify(document, root, namespaces, null);
	}

	private static void autoQualify(Document document, Element element, Map<String, String> namespaces,
	        String parentPrefix) {
		String localName = element.getLocalName();
		element = renameElement(document, element, localName, namespaces.get(parentPrefix));
		String prefix = parentPrefix;
		if (namespaces.containsKey(localName.toLowerCase())) {
			prefix = localName;
		}
		else if (genericBranchCoverageTypes.contains(localName.toLowerCase())){
			prefix = GENERIC_BRANCH_COVERAGE;
		}
		NodeList nodes = element.getChildNodes();
		for (int i = 0, iEnd = nodes.getLength(); i < iEnd; ++i) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				autoQualify(document, (Element) node, namespaces, prefix);
			}
		}
	}

	/**
	 * Rename an element in a DOM document. It happens to involves a node
	 * replication.
	 * 
	 * @param document
	 *            The document containing the element (some way to verify
	 *            that?).
	 */
	public static Element renameElement(Document document, Element element, String newName, String namespace) {
		if (namespace == null) {
			throw new IllegalArgumentException("No namespace provided for element " + element);
		}
		Element newElement = document.createElementNS(namespace, newName);
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0, iEnd = attributes.getLength(); i < iEnd; i++) {
			Attr attr2 = (Attr) document.importNode(attributes.item(i), true);
			newElement.getAttributes().setNamedItem(attr2);
		}
		while (element.hasChildNodes()) {
			newElement.appendChild(element.getFirstChild());
		}
		element.getParentNode().replaceChild(newElement, element);
		return newElement;
	}

	private XmlHelper() {
	}
}
