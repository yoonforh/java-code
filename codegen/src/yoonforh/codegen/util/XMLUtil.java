/*
 * $Id: XMLUtil.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
 *
 * Copyright (c) 2003 by Yoon Kyung Koo.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Yoon Kyung Koo("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Yoon Kyung Koo.
 */


package yoonforh.codegen.util;

import java.io.*;
import java.net.*;
import java.util.logging.*; // logger

import org.xml.sax.InputSource;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.DOMImplementation;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

// jaxp 1.0.1 imports
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

// xerces xml writer
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import yoonforh.codegen.ApplicationException;

/**
 * xml parse utils
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-25 11:01:59
 * @author   Yoon Kyung Koo
 */

public class XMLUtil { 
    private static Logger logger
	= Logger.getLogger(XMLUtil.class.getName());

    public static Element loadDocument(URL url) throws ApplicationException {
	try {
	    return XMLUtil.loadDocument(url.openStream());
	} catch (IOException e) {
	    logger.throwing("XMLUtil", "loadDocument", e);
	    throw new ApplicationException(e);
	}
    }

    public static Element loadDocument(File file) throws ApplicationException {
	try {
	    System.out.println("loadDocument(file - " + file + ")");
	    return XMLUtil.loadDocument(new FileInputStream(file));
	} catch (IOException e) {
	    logger.throwing("XMLUtil", "loadDocument", e);
	    throw new ApplicationException(e);
	}
    }

    public static Element loadDocument(InputStream in) throws ApplicationException {
        Document doc = null;
	Exception ex = null;

        try {
            InputSource xmlInp = new InputSource(in);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    // DTD grammar check
	    factory.setValidating(true);
	    // ignore white spaces
	    factory.setIgnoringElementContentWhitespace(true);
	    // ignore comments
	    factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
	    // set entity resolver to lookup dtd file from classloader
	    builder.setEntityResolver(new LocalEntityResolver());

            doc = builder.parse(xmlInp);
            Element root = doc.getDocumentElement();
            root.normalize();
            return root;
        } catch (SAXParseException e) {
            logger.log(Level.SEVERE, "loadDocument() : parsing error" + ", at line " 
		       + e.getLineNumber () + ", uri - " + e.getSystemId ()
		       + ", msg - " + e.getMessage(), e);
	    ex = e;
        } catch (SAXException e) {
            logger.log(Level.SEVERE, "loadDocument() : xml error", e);
	    ex = e;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "loadDocument() : io error", e);
	    ex = e;
	} catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "loadDocument() : parser config error", e);
	    ex = e;
        }

	if (ex != null) {
	    throw new ApplicationException(ex);
	}

	// never reach here
	return null;
    }

    /**
     * create an empty document
     */
    public static Document newDocument() throws ApplicationException {
        Document doc = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	    // DTD grammar check
	    factory.setValidating(true);
	    // ignore white spaces
	    factory.setIgnoringElementContentWhitespace(true);
	    // ignore comments
	    factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
	    // set entity resolver to lookup dtd file from classloader
	    builder.setEntityResolver(new LocalEntityResolver());

	    doc = builder.newDocument();
	} catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "newDocument() : parser config error", e);

	    throw new ApplicationException(e);
        }

	return doc;
    }

    /**
     * create an empty document
     */
    public static Document newDocument(String qualifiedName, String publicId, String systemId)
		throws ApplicationException {
        Document doc = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	    // DTD grammar check
	    factory.setValidating(true);
	    // ignore white spaces
	    factory.setIgnoringElementContentWhitespace(true);
	    // ignore comments
	    factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
	    // set entity resolver to lookup dtd file from classloader
	    builder.setEntityResolver(new LocalEntityResolver());
	    DOMImplementation domImpl = builder.getDOMImplementation();
	    DocumentType docType = domImpl
		.createDocumentType(qualifiedName, publicId, systemId);

	    doc = domImpl.createDocument(null, qualifiedName, docType);
	} catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "newDocument() : parser config error", e);

	    throw new ApplicationException(e);
        }

	return doc;
    }

    /**
     * get the attribute value of given sub tag of the tag in the root element tree
     */
    public static String getSubTagAttribute(Element root, String tagName,
					    String subTagName, String attribute) {
        String returnString = "";
        NodeList list = root.getElementsByTagName(tagName);
        for (int loop = 0; loop < list.getLength(); loop++) {
            Node node = list.item(loop);
            if (node != null) {
                NodeList  children = node.getChildNodes();
                for (int innerLoop =0; innerLoop < children.getLength(); innerLoop++) {
                    Node  child = children.item(innerLoop);
                    if ((child != null) && (child.getNodeName() != null)
			&& child.getNodeName().equals(subTagName) ) {
                        if (child instanceof Element) {
                            return ((Element)child).getAttribute(attribute);
                        }
                    }
                } // end inner loop
            }
        }
        return returnString;
    }

    /**
     * get PCDATA value of given sub tag of the node
     */
    public static String getSubTagValue(Node node, String subTagName) {
        String returnString = "";
        if (node != null) {
            NodeList children = node.getChildNodes();
            for (int innerLoop =0; innerLoop < children.getLength(); innerLoop++) {
                Node child = children.item(innerLoop);
                if ((child != null) && (child.getNodeName() != null)
		    && child.getNodeName().equals(subTagName) ) {
                    Node grandChild = child.getFirstChild();
                    if (grandChild.getNodeValue() != null) {
			return grandChild.getNodeValue();
		    }
                }
            } // end inner loop
        }
        return returnString;
    }

    /**
     * get PCDATA value of given sub tag of the tag in the root element tree
     */
    public static String getSubTagValue(Element root, String tagName, String subTagName) {
        String returnString = "";
        NodeList list = root.getElementsByTagName(tagName);
        for (int loop = 0; loop < list.getLength(); loop++) {
            Node node = list.item(loop);
            if (node != null) {
                NodeList  children = node.getChildNodes();
                for (int innerLoop =0; innerLoop < children.getLength(); innerLoop++) {
                    Node  child = children.item(innerLoop);
                    if ((child != null) && (child.getNodeName() != null)
			&& child.getNodeName().equals(subTagName) ) {
                        Node grandChild = child.getFirstChild();
                        if (grandChild.getNodeValue() != null) {
			    return grandChild.getNodeValue();
			}
                    }
                } // end inner loop
            }
        }
        return returnString;
    }

    /**
     * get PCDATA value of given sub tag of the element
     */
    public static String getTagValue(Element element, String tagName) {
        String returnString = "";
        NodeList list = element.getElementsByTagName(tagName);
        for (int loop = 0; loop < list.getLength(); loop++) {
            Node node = list.item(loop);
            if (node != null) {
                Node child = node.getFirstChild();
                if ((child != null) && child.getNodeValue() != null) {
		    return child.getNodeValue();
		}
            }
        }
        return returnString;
    }

    /**
     * get PCDATA value of given tag
     */
    public static String getTagValue(Node node) {
	Node child = node.getFirstChild();
	if ((child != null) && child.getNodeValue() != null) {
	    return child.getNodeValue();
	}

        return null;
    }

    /**
     * write XML document from DOM
     */
    public static void writeDocument(File file, Document doc) throws IOException {
	FileWriter writer = new FileWriter(file);

// 	if (doc instanceof org.apache.crimson.tree.XmlDocument) {
// 	    // crimson parser
// 	    org.apache.crimson.tree.XmlDocument xdoc = (org.apache.crimson.tree.XmlDocument) doc;
// 	    xdoc.write(writer);
// 	} else {
	    // xerces parser
	OutputFormat format = new OutputFormat(doc);
	format.setIndenting(true);
	XMLSerializer serializer = new XMLSerializer(writer, format);
	serializer.asDOMSerializer();
	serializer.serialize(doc.getDocumentElement());
// 	}
    }

}
