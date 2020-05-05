/*
 * $Id: InputSheetDocumentBuilder.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.util.XMLUtil;
import yoonforh.codegen.util.LocalEntityResolver;

/**
 *
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-03 16:01:51
 * @author   Yoon Kyung Koo
 */

public class InputSheetDocumentBuilder {
    private static Logger logger
	= Logger.getLogger(InputSheetDocumentBuilder.class.getName());

    private static InputSheetDocumentBuilder instance = null;
    private InputSheetDocumentBuilder() {}
    public static InputSheetDocumentBuilder getInstance() {
	if (instance == null) {
	    instance = new InputSheetDocumentBuilder();
	}

	return instance;
    }

    public Document build(String templateName, TemplateValues values) throws ApplicationException {
	HashMap rootPaths = values.getRootPaths();
	HashMap charsets = values.getCharsets();
	HashMap globals = values.getVariables();
	Document doc = null;

	logger.finest("values are : rootPaths - " + rootPaths
		      + ", charsets - " + charsets
		      + ", globals - " + globals);

	doc = XMLUtil.newDocument(BatchInputDAO.TEMPLATE_INPUT_TAG,
				  LocalEntityResolver.INPUT_SHEET_PUBLIC_ID,
				  LocalEntityResolver.INPUT_SHEET_SYSTEM_ID);
	Element root = doc.getDocumentElement(); 
	if (root == null) {
	    root = doc.createElement(BatchInputDAO.TEMPLATE_INPUT_TAG);
	    doc.appendChild(root);
	}

	// <template> tag
	Element elem = doc.createElement(BatchInputDAO.TEMPLATE_TAG);
	elem.appendChild(doc.createTextNode(templateName));
	root.appendChild(elem);

	// <input-sheet> tag
	Element sheet = doc.createElement(BatchInputDAO.INPUT_SHEET_TAG);
	root.appendChild(sheet);

	// <root-path> tags
	Iterator it = rootPaths.keySet().iterator();
	while (it.hasNext()) {
	    String key = (String) it.next();
	    String value = (String) rootPaths.get(key);
	    elem = doc.createElement(BatchInputDAO.ROOT_PATH_TAG);
	    elem.setAttribute(BatchInputDAO.ID_ATTR, key);
	    elem.appendChild(doc.createTextNode(value));
	    sheet.appendChild(elem);
	}

	// <charset> tags
	it = charsets.keySet().iterator();
	while (it.hasNext()) {
	    String key = (String) it.next();
	    String value = (String) charsets.get(key);
	    elem = doc.createElement(BatchInputDAO.CHARSET_TAG);
	    elem.setAttribute(BatchInputDAO.ID_ATTR, key);
	    elem.appendChild(doc.createTextNode(value));
	    sheet.appendChild(elem);
	}

	// <var> tags
	it = globals.keySet().iterator();
	while (it.hasNext()) {
	    String key = (String) it.next();
	    String value = (String) globals.get(key);
	    elem = doc.createElement(BatchInputDAO.VAR_TAG);
	    elem.setAttribute(BatchInputDAO.ID_ATTR, key);
	    elem.appendChild(doc.createTextNode(value));
	    sheet.appendChild(elem);
	}

	return doc;
    }

}
