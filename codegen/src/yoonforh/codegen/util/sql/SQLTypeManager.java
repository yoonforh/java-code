/*
 * $Id: SQLTypeManager.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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
import java.util.logging.*;
import yoonforh.codegen.ApplicationException;

/**
 * sql type manager
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-10 15:45:55
 * @author   Yoon Kyung Koo
 */

public class SQLTypeManager {
    private static Logger logger
	= Logger.getLogger(SQLTypeManager.class.getName());
    public final static String ORACLE_TYPE = "oracle";
    public final static String DEFAULT_TYPE = "default";
    private static HashMap instanceMap = new HashMap();

    // java type - sql type map
    protected HashMap javaTypeStringMap = new HashMap();
    protected HashMap javaTypeMap = new HashMap();

    /*
     * NOTE : all sql types are in lower case
     */
    protected static final Object[][] typeArray = {
	{ "tinyint", "byte", Byte.TYPE },
	{ "smallint", "short", Short.TYPE },
	{ "integer", "int", Integer.TYPE },
	{ "bigint", "long", Long.TYPE },
	{ "real", "float", Float.TYPE },
	{ "float", "double", Double.TYPE },
	{ "double", "double", Double.TYPE },
	{ "decimal", "BigDecimal", java.math.BigDecimal.class },
	{ "numeric", "BigDecimal", java.math.BigDecimal.class },
	{ "bit", "boolean", Boolean.TYPE },
	{ "char", "String", String.class },
	{ "varchar", "String", String.class },
	{ "longvarchar", "CharacterStream", java.io.Reader.class },
	{ "binary", "bytes", byte[].class },
	{ "varbinary", "bytes", byte[].class },
	{ "longvarbinary", "ByteStream", java.io.InputStream.class },
	{ "date", "Date", java.sql.Date.class },
	{ "time", "Time", java.sql.Time.class },
	{ "timestamp", "Timestamp", java.sql.Timestamp.class },
	{ "clob", "Clob", java.sql.Clob.class },
	{ "blob", "Blob", java.sql.Blob.class },
	{ "array", "Array", java.sql.Array.class },
	{ "ref", "Ref", java.sql.Ref.class },
	{ "struct", "Object", Object.class },
	{ "java object", "Object", Object.class }
    };


    protected SQLTypeManager() {
	populateTypeMap(); // lazy construction
    }

    public static SQLTypeManager getInstance() throws ApplicationException {
	return SQLTypeManager.getInstance(DEFAULT_TYPE);
    }

    public static SQLTypeManager getInstance(String type) throws ApplicationException {
	SQLTypeManager instance = (SQLTypeManager) instanceMap.get(type);
	if (instance == null) {
	    if (DEFAULT_TYPE.equals(type)) {
		instance = new SQLTypeManager();
	    } else if (ORACLE_TYPE.equals(type)) {
		instance = new OracleTypeManager();
	    } else { // unknown type... error
		logger.severe("unsupported type - " + type);
	    }

	    if (instance != null) {
		instanceMap.put(type, instance);
	    }
	}

	return instance;
    }

    /**
     * get java type corresponding to sql type string
     *
     * @param sqlType sql type string
     */
    public String getJavaTypeString(String sqlType) {
	return (String) javaTypeStringMap.get(sqlType.toLowerCase());
    }

    /**
     * get java type corresponding to sql type string
     *
     * @param sqlType sql type string
     */
    public Class getJavaType(String sqlType) {
	return (Class) javaTypeMap.get(sqlType.toLowerCase());
    }

    /**
     * populate sql type - java type map
     */
    protected void populateTypeMap() {
	for (int i = 0; i < typeArray.length; i++) {
	    javaTypeStringMap.put(typeArray[i][0], typeArray[i][1]);
	    javaTypeMap.put(typeArray[i][0], typeArray[i][2]);
	}
    }
}
