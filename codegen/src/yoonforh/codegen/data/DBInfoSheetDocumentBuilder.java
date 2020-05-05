/*
 * $Id: DBInfoSheetDocumentBuilder.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
import java.util.ArrayList;
import java.util.logging.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.util.XMLUtil;
import yoonforh.codegen.util.CSVUtil;
import yoonforh.codegen.util.LocalEntityResolver;
import yoonforh.codegen.parser.OracleCreateTableParser.TableInfo;
import yoonforh.codegen.parser.OracleCreateTableParser.FieldType;

/**
 * 
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-19 00:29:14
 * @author   Yoon Kyung Koo
 */

public class DBInfoSheetDocumentBuilder {
    private static Logger logger
	= Logger.getLogger(DBInfoSheetDocumentBuilder.class.getName());

    public static final String TABLE_INFO_SHEET_TAG = "table-info-sheet";
    public static final String TITLE_TAG = "title";
    public static final String AUTHOR_TAG = "author";
    public static final String DATE_TAG = "date";
    public static final String DESCRIPTION_TAG = "description";
    public static final String TABLE_TAG = "table";
    public static final String FIELD_TAG = "field";

    public static final String NAME_ATTR = "name";
    public static final String KEYS_ATTR = "keys";
    public static final String ALIAS_ATTR = "alias";
    public static final String TYPE_ATTR = "type";
    public static final String SIZE_ATTR = "size";

    public static final String AUTHOR_NAME = "codegen 1.0";

    private static DBInfoSheetDocumentBuilder instance = null;
    private DBInfoSheetDocumentBuilder() {}

    public static DBInfoSheetDocumentBuilder getInstance() {
	if (instance == null) {
	    instance = new DBInfoSheetDocumentBuilder();
	}

	return instance;
    }

    /**
     * @param title xml title
     * @param description additional description
     * @param tableMap <table name> - <TableInfo> map
     */
    public Document build(String title, String description, HashMap tableMap)
		throws ApplicationException {
	Document doc = null;

	doc = XMLUtil.newDocument(TABLE_INFO_SHEET_TAG,
				  LocalEntityResolver.TABLE_INFO_PUBLIC_ID,
				  LocalEntityResolver.TABLE_INFO_SYSTEM_ID);
	Element root = doc.getDocumentElement(); 
	if (root == null) {
	    root = doc.createElement(TABLE_INFO_SHEET_TAG);
	    doc.appendChild(root);
	}

	// <title> tag
	Element elem = doc.createElement(TITLE_TAG);
	elem.appendChild(doc.createTextNode(title));
	root.appendChild(elem);

	// <author> tag
	elem = doc.createElement(AUTHOR_TAG);
	elem.appendChild(doc.createTextNode(AUTHOR_NAME));
	root.appendChild(elem);

	// <date> tag
	elem = doc.createElement(DATE_TAG);
	elem.appendChild(doc.createTextNode((new java.util.Date()).toString()));
	root.appendChild(elem);

	// <description> tag
	elem = doc.createElement(DESCRIPTION_TAG);
	elem.appendChild(doc.createTextNode(description));
	root.appendChild(elem);

	Iterator iter = tableMap.keySet().iterator();
	while (iter.hasNext()) {
	    String tableName = (String) iter.next();
	    TableInfo tableInfo = (TableInfo) tableMap.get(tableName);
	    ArrayList pkList = tableInfo.getPKList();
	    HashMap fieldMap = tableInfo.getFieldMap();

	    if (fieldMap == null) {
		logger.severe("cannot get the table info - " + tableName);
		throw new ApplicationException("no table info - " + tableName);
	    }

	    Element table = doc.createElement(TABLE_TAG);
	    table.setAttribute(NAME_ATTR, tableName);

	    if (pkList != null && pkList.size() > 0) {
		table.setAttribute(KEYS_ATTR, CSVUtil.getCSVFromList(pkList));
	    }

	    root.appendChild(table);

	    Iterator iter2 = fieldMap.keySet().iterator();
	    while (iter2.hasNext()) {
		String fieldName = (String) iter2.next();
		FieldType type = (FieldType) fieldMap.get(fieldName);

		Element field = doc.createElement(FIELD_TAG);
		field.setAttribute(NAME_ATTR, fieldName);
		String value = type.getName();
		if (value != null && value.length() > 0) {
		    field.setAttribute(TYPE_ATTR, value);
		}
		value = type.getSize();
		if (value != null && value.length() > 0) {
		    field.setAttribute(SIZE_ATTR, value);
		}

		table.appendChild(field);
	    }

	}

	return doc;
    }

}
