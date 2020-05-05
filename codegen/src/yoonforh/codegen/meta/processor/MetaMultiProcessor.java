/*
 * $Id: MetaMultiProcessor.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen.meta.processor;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.*;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.data.DocInfo;
import yoonforh.codegen.util.sql.OracleParserManager;
import yoonforh.codegen.parser.ParseException;
import yoonforh.codegen.processor.TextProcessor;

/**
 * meta framework code generation processor
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-05-15 00:42:57
 * @author   Yoon Kyung Koo
 */

public class MetaMultiProcessor extends TextProcessor {
    private static Logger logger
	= Logger.getLogger(MetaMultiProcessor.class.getName());

    /** param name */
    public static final String PARAM_REPEAT = "repeat";

    /** param value */
    public static final String REPEAT_TABLE = "table";

    /** key to global var which contains sql sheet */
    public static final String SQL_SHEET_PATH_KEY = "sql-sheet-path-key";

    /** key to global var which contains function id */
    public static final String FUNCTION_ID_KEY = "function-id-key";

    /** global var name we will set which should have table name */
    public static final String TABLE_VAR_NAME = "table-var-name";

    /**
     * required constructor
     */
    public MetaMultiProcessor(HashMap vars) {
	super(vars);
    }

    /**
     * generate converted file
     */
    public void generateFile(byte[] bytes, String rootPath, String relPath, boolean isDirectory,
			     String charset, DocInfo docInfo)
	throws java.io.IOException, ApplicationException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("generateFile(bytes - " + bytes
			  + ", rootPath - " + rootPath
			  + ", relPath - " + relPath
			  + ", isDirectory - " + isDirectory
			  + ", charset - " + charset
			  + ", docInfo - " + docInfo);
	}

	HashMap paramMap = docInfo.getProcessorInfo().getParamMap();

	String repeat = (String) paramMap.get(PARAM_REPEAT);
	String functionID = (String) vars.get((String) paramMap.get(FUNCTION_ID_KEY));
	String tableVarName = (String) paramMap.get(TABLE_VAR_NAME);
	HashMap globalMap = (HashMap) vars.clone();
	String relPathProcessed = relPath;

	try {
	    if (REPEAT_TABLE.equals(repeat)) {
		String sqlPathKey = (String) paramMap.get(SQL_SHEET_PATH_KEY);

		if (sqlPathKey == null || sqlPathKey.length() == 0) {
		    logger.severe("required sql path key omitted in template.xml");
		    throw new ApplicationException("required sql path key omitted in template.xml");
		}

		String sqlPath = (String) vars.get(sqlPathKey);
		HashMap tableMap = OracleParserManager.getInstance().parseSQL(sqlPath);
		Iterator iter = tableMap.keySet().iterator();

		while (iter.hasNext()) {
		    String tableName = (String) iter.next();
		    if (functionID != null && functionID.length() > 0) {
			if (!tableName.startsWith(functionID.toLowerCase())) {
			    logger.warning("unsupported table name - " + tableName
					   + ", while function id - " + functionID);
			    continue;
			}
		    }

		    globalMap.put(tableVarName, tableName);

		    TextProcessor processor = new TextProcessor(globalMap);
		    relPathProcessed = processor.apply(relPath);
		    processor.generateFile(bytes, rootPath, relPathProcessed, isDirectory,
					   charset, docInfo);
		}
	    } else {
		TextProcessor processor = new TextProcessor(globalMap);
		relPathProcessed = processor.apply(relPath);
		processor.generateFile(bytes, rootPath, relPathProcessed, isDirectory,
				       charset, docInfo);
	    }
	} catch (java.io.IOException e) {
	    logger.log(Level.SEVERE, "sql sheet io error", e);
	    throw new ApplicationException(e);
	} catch (ParseException e) {
	    logger.log(Level.SEVERE, "sql sheet parse error", e);
	    throw new ApplicationException(e);
	}

    }

}
