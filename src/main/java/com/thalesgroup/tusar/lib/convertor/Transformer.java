package com.thalesgroup.tusar.lib.convertor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.MissingResourceException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DOMDestination;
import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.w3c.dom.Document;

import org.jenkinsci.lib.dtkit.util.converter.ConversionException;

/**
 * Wrap an XSL transformation by hidding the usual boilerplate code.
 * <p/>
 * Note that {@link com.thalesgroup.dtkit.util.converter.ConversionService}
 * isn't used anymore because it relies too much on file manipulation instead of
 * URLs and memory buffers. However, we also use Saxon implementation instead of
 * the default one to benefit from 2.0 features.
 */
class Transformer {

	/**
	 * A XsltTransformer goes with its Processor and can't be used with another.
	 */
	private static class Context {

		public final Processor processor;

		public final XsltTransformer xsltTransformer;

		public Context(Processor processor, XsltTransformer xsltTransformer) {
			this.processor = processor;
			this.xsltTransformer = xsltTransformer;
		}
	}

	private final URL xslUrl;

	private final String name;

	private WeakReference<Context> contextRef = new WeakReference<Context>(null);

	public Transformer(String xslResourceName) throws MissingResourceException {
		xslUrl = getClass().getResource(xslResourceName);
		if (xslUrl == null) {
			throw new MissingResourceException("Missing XSLT", xslResourceName, xslResourceName);
		}
		this.name = xslResourceName;
	}

	public Transformer(URL xslUrl) {
		this(xslUrl, xslUrl.getFile());
	}

	public Transformer(URL xslUrl, String name) {
		this.xslUrl = xslUrl;
		this.name = name;
	}

	public Document apply(Document input) throws ConversionException {
		try {
			DocumentBuilderFactory factory = XmlHelper.newNamespaceAwareFactory();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document output = builder.newDocument();
			Destination destination = new DOMDestination(output);

			Context context = getCachedContext();

			XsltTransformer xsltTransformer = getCachedContext().xsltTransformer;
			xsltTransformer.setInitialContextNode(domToXdm(context.processor, input));
			xsltTransformer.setDestination(destination);
			xsltTransformer.transform();

			return output;
		} catch (IOException e) {
			throw new ConversionException("When applying XSLT " + xslUrl, e);
		} catch (SaxonApiException e) {
			throw new ConversionException("When applying XSLT " + xslUrl, e);
		} catch (ParserConfigurationException e) {
			throw new ConversionException("When applying XSLT " + xslUrl, e);
		}
	}

	private Context getCachedContext() throws IOException, SaxonApiException {
		Context context = contextRef.get();
		if (context == null) {
			Processor processor = new Processor(false);
			XsltCompiler compiler = processor.newXsltCompiler();
			XsltExecutable xsltExecutable;
			InputStream xslStream = xslUrl.openStream();
			try {
				xsltExecutable = compiler.compile(new StreamSource(xslStream));
			} finally {
				xslStream.close();
			}
			context = new Context(processor, xsltExecutable.load());
			contextRef = new WeakReference<Context>(context);
		}
		return context;
	}

	private XdmNode domToXdm(Processor processor, Document document) throws SaxonApiException {
		net.sf.saxon.s9api.DocumentBuilder saxonDocumentBuilder = processor.newDocumentBuilder();
		saxonDocumentBuilder.setDTDValidation(false);
		return saxonDocumentBuilder.build(new DOMSource(document.getDocumentElement()));
	}

	@Override
	public String toString() {
		return name;
	}
}
