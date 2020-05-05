/*
 * $Id: BatchInputDAO.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen.data;

import java.io.File;
import java.util.*;
import java.util.logging.*;
import org.w3c.dom.*;

import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.util.XMLUtil;

/**
 * batch input file dao
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-02 14:34:38
 * @author   Yoon Kyung Koo
 */

public class BatchInputDAO {
    private static Logger logger
	= Logger.getLogger(BatchInputDAO.class.getName());

    // xml elements
    public static final String TEMPLATE_INPUT_TAG = "template-input";
    public static final String TEMPLATE_TAG = "template";
    public static final String INPUT_SHEET_TAG = "input-sheet";
    public static final String ROOT_PATH_TAG = "root-path";
    public static final String CHARSET_TAG = "charset";
    public static final String VAR_TAG = "var";

    // xml attribute names
    public static final String ID_ATTR = "id";

    // singleton instance
    private static BatchInputDAO instance = null;

    // instance variables
    /** input sheets list */
    private ArrayList inputSheets = new ArrayList(10);

    private BatchInputDAO() { }

    public static BatchInputDAO getInstance() {
	if (instance == null) {
	    instance = new BatchInputDAO();
	}

	return instance;
    }

    /**
     * get input sheets as an Iterator instance
     */
    public Iterator getInputSheetsIterator() {
	return inputSheets.iterator();
    }

    /**
     * parse the input sheet file into an array
     * and returns the template name
     *
     * @return the template name
     */
    public String readInputFile(File file) throws ApplicationException {
	Element root = XMLUtil.loadDocument(file);

	if (root == null) {
	    logger.warning("cannot find template-input root elements");
	    throw new ApplicationException("cannot find template-input root elements");
	}

	String templateName = XMLUtil.getTagValue(root, TEMPLATE_TAG);
	if (templateName == null) {
	    logger.warning("template-input has no template elements");
	    throw new ApplicationException("template-input has no template elements.");
	}

	NodeList list = root.getElementsByTagName(INPUT_SHEET_TAG);
	for (int i = 0; i < list.getLength(); i++) {
	    Node node = list.item(i);

	    if (node != null && node instanceof Element) {
		InputSheet inputSheet = buildInputSheet((Element) node);
		if (inputSheet != null) {
		    inputSheets.add(inputSheet);
		}
	    }
	}

	return templateName;
    }

    /**
     * build doc info object
     */
    private InputSheet buildInputSheet(Element root) throws ApplicationException {
	HashMap rootPathValues = new HashMap();
	HashMap charsetValues = new HashMap();
	HashMap variableValues = new HashMap();
	NodeList list;
	Node node;
	String id;
	String value;

	list = root.getElementsByTagName(ROOT_PATH_TAG);
	for (int i = 0; i < list.getLength(); i++) {
	    node = list.item(i);
	    if (node != null && node instanceof Element) {
		Element el = (Element) node;

		id = el.getAttribute(ID_ATTR);
		value = XMLUtil.getTagValue(node);

		if (id != null) {
		    rootPathValues.put(id, value);
		}
	    }
	}

	list = root.getElementsByTagName(CHARSET_TAG);
	for (int i = 0; i < list.getLength(); i++) {
	    node = list.item(i);
	    if (node != null && node instanceof Element) {
		Element el = (Element) node;

		id = el.getAttribute(ID_ATTR);
		value = XMLUtil.getTagValue(node);

		if (id != null) {
		    charsetValues.put(id, value);
		}
	    }
	}

	list = root.getElementsByTagName(VAR_TAG);
	for (int i = 0; i < list.getLength(); i++) {
	    node = list.item(i);
	    if (node != null && node instanceof Element) {
		Element el = (Element) node;

		id = el.getAttribute(ID_ATTR);
		value = XMLUtil.getTagValue(node);

		if (id != null) {
		    variableValues.put(id, value);
		}
	    }
	}

	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("new input sheet(root paths - " + rootPathValues
			  + ", charsets - " + charsetValues
			  + ", vars - " + variableValues
			  + ")");
	}
	return new InputSheet(rootPathValues, charsetValues, variableValues);
    }

}
