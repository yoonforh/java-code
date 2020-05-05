/*
 * $Id: OracleParserManager.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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


package yoonforh.codegen.util.sql;

import java.util.HashMap;
import yoonforh.codegen.parser.OracleCreateTableParser;
import yoonforh.codegen.parser.ParseException;

/**
 * singleton which has a map of <sql path> - <parsed hash map>
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-05-15 00:49:31
 * @author   Yoon Kyung Koo
 */

public class OracleParserManager {
    private static OracleParserManager instance = null;
    private HashMap pathMap = new HashMap();

    private OracleParserManager() {}
    public static OracleParserManager getInstance() {
	if (instance == null) {
	    instance = new OracleParserManager();
	}

	return instance;
    }

    /**
     * if already parsed then return cached data
     */
    public HashMap parseSQL(String sqlPath) throws java.io.IOException, ParseException {
	HashMap tableMap = (HashMap) pathMap.get(sqlPath);
	if (tableMap == null) {
	    tableMap = new HashMap();
	    OracleCreateTableParser parser = new OracleCreateTableParser(
		new java.io.FileInputStream(sqlPath));
	    // the result map is <table name> - <table info> map
	    // <fields map> consists of <field name> - <field type>
	    // each name and types are in lower case
	    parser.SQLSheet(tableMap);

	    pathMap.put(sqlPath, tableMap);
	}

	return tableMap;
    }
}
