/*
 * $Id: DBInfoXMLGenerator.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.*;
import org.w3c.dom.Document;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.data.DBInfoSheetDocumentBuilder;
import yoonforh.codegen.util.sql.OracleParserManager;
import yoonforh.codegen.parser.ParseException;
import yoonforh.codegen.util.XMLUtil;

/**
 * database info xml file generator - a code gen handler
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-19 00:15:16
 * @author   Yoon Kyung Koo
 */

public class DBInfoXMLGenerator implements Handler {
    private static Logger logger
	= Logger.getLogger(DBInfoXMLGenerator.class.getName());

    /*
     * sql sheet file path input key
     */
    public static final String SQL_SHEET_PATH_KEY = "sql-sheet-path-key";

    /*
     * xml sheet file path output key
     */
    public static final String XML_SHEET_PATH_KEY = "xml-sheet-path-key";

    public static final String XML_SHEET_TITLE_KEY = "xml-sheet-title-key";

    /**
     * this handler has no effect on the text.
     * it generates an xml file using input sql sheet.
     * paramMap are specified in template.xml and globalMap are specified by user inputs.
     *
     *  input param 1. sql-sheet-path-key
     *  input param 2. xml-sheet-path-key
     *
     * @param text main text
     * @param paramMap parameters map
     * @param globalMap identified variables map
     */
    public String handle(String text, HashMap paramMap, HashMap globalMap)
		throws ApplicationException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("handle(text - " + text + ", paramMap - " + paramMap
			  + ", globalMap - " + globalMap);
	}

	String result = null;

	String sqlPathKey = (String) paramMap.get(SQL_SHEET_PATH_KEY);

	if (sqlPathKey == null || sqlPathKey.length() == 0) {
	    logger.severe("required sql path key omitted");
	    throw new ApplicationException("required sql path key omitted");
	}

	String xmlPathKey = (String) paramMap.get(XML_SHEET_PATH_KEY);

	if (xmlPathKey == null || xmlPathKey.length() == 0) {
	    logger.severe("required xml path key omitted");
	    throw new ApplicationException("required xml path key omitted");
	}

	String xmlTitleKey = (String) paramMap.get(XML_SHEET_TITLE_KEY);

	if (xmlTitleKey == null || xmlTitleKey.length() == 0) {
	    logger.severe("required xml title key omitted");
	    throw new ApplicationException("required xml title key omitted");
	}

	String sqlPath = (String) globalMap.get(sqlPathKey);
	String xmlPath = (String) globalMap.get(xmlPathKey);
	String xmlTitle = (String) globalMap.get(xmlTitleKey);
	generate(sqlPath, xmlPath, xmlTitle);

	return text;
    }

    public void generate(String sqlPath, String xmlPath, String xmlTitle)
		throws ApplicationException {
	try {
	    HashMap tableMap = OracleParserManager.getInstance().parseSQL(sqlPath);

	    DBInfoSheetDocumentBuilder builder = DBInfoSheetDocumentBuilder.getInstance();
	    Document doc = builder.build(xmlTitle, "converted from " + sqlPath, tableMap);
	    XMLUtil.writeDocument(new java.io.File(xmlPath), doc);
	} catch (java.io.IOException e) {
	    logger.log(Level.SEVERE, "sql sheet io error", e);
	    throw new ApplicationException(e);
	} catch (ParseException e) {
	    logger.log(Level.SEVERE, "sql sheet parse error", e);
	    throw new ApplicationException(e);
	}
    }

    /**
     * direct conversion
     */
    public static void main(String[] args) throws ApplicationException {
	if (args.length < 3) {
	    System.err.println("Usage : java yoonforh.codegen.handler.DBInfoXMLGenerator "
			       + "      <sql path> <output xml path> <xml title>");
	    System.exit(0);
	}

	new DBInfoXMLGenerator().generate(args[0], args[1], args[2]);
    }
}
