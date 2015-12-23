package com.thalesgroup.tusar.lib.convertor;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jenkinsci.lib.dtkit.util.converter.ConversionException;

/**
 * Recursive extraction of every namespace declarations from a XSD, including
 * imported schemae.
 */
public class NamespaceSet {

	private String namespaceUrlPrefix;

	private static Logger logger = LoggerFactory.getLogger(NamespaceSet.class);

	// Namespace: prefix -> URL
	private Map<String, String> namespaces = new HashMap<String, String>();

	public NamespaceSet(String xsdPath, String namespaceUrlPrefix) {
		this.namespaceUrlPrefix = namespaceUrlPrefix;
		collectNamespaces(xsdPath);
	}

	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	private void collectNamespaces(String xsdPath) {
		URL xsdUrl = getClass().getResource(xsdPath);
		try {
			if (xsdUrl != null) {
				collectNamespaces(xsdPath, XmlHelper.readXml(xsdUrl));
			} else {
				throw new ConversionException("Schema not found: " + xsdUrl);
			}
		} catch (Exception e) {
			throw new ConversionException("Cannot read schema: " + xsdUrl);
		}
	}

	private void collectNamespaces(String xsdPath, Document document) {
		Element root = document.getDocumentElement();
		collectNamespaces(xsdPath, document, root);
	}

	private void collectNamespaces(String xsdPath, Document document, Element element) {
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0, iEnd = attributes.getLength(); i < iEnd; ++i) {
			Node node = attributes.item(i);
			if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				Attr attribute = (Attr) node;
				String name = attribute.getName();
				String attributPrefix = "xmlns:";

				String prefix = null;
				String url = null;
				if (name.startsWith(attributPrefix)) {
					prefix = name.substring(attributPrefix.length());
					url = attribute.getValue();
				} else if (name.equalsIgnoreCase("targetNamespace")) {
					prefix = null;
					url = attribute.getValue();
				}

				if (url != null && url.startsWith(namespaceUrlPrefix)) {
					logger.trace("In '{}', found namespace declaration: {} -> '{}'", xsdPath, prefix, url);
					/*
					 * URL are expected to be the same, excepted the 'null'
					 * target namespace. Since we are only interested in the top
					 * root value, we discard subsequent values.
					 */
					if (!namespaces.containsKey(prefix)) {
						namespaces.put(prefix, url);
					}
				}
			}
		}

		if ("import".equalsIgnoreCase(element.getLocalName())) {
			String namespace = element.getAttribute("namespace");
			if (namespace.startsWith(namespaceUrlPrefix)) {
				String location = element.getAttribute("schemaLocation");
				String importedXsdPath = location;
				if (!importedXsdPath.startsWith("/") && xsdPath.startsWith("/")) {
					importedXsdPath = xsdPath.substring(0, xsdPath.lastIndexOf('/') + 1) + importedXsdPath;
				}
				collectNamespaces(importedXsdPath);
			}
		}

		NodeList nodes = element.getChildNodes();
		for (int i = 0, iEnd = nodes.getLength(); i < iEnd; ++i) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				collectNamespaces(xsdPath, document, (Element) node);
			}
		}
	}
}
