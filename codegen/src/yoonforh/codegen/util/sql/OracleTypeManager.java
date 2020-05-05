/*
 * $Id: OracleTypeManager.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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
 * oralce sql type manager
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-10 16:08:31
 * @author   Yoon Kyung Koo
 */

public class OracleTypeManager extends SQLTypeManager {

    /*
     * NOTE : all sql types are in lower case
     */
    protected static final Object[][] oracleTypeArray = {
	{ "varchar2", "String", String.class },
	{ "number", "BigDecimal", java.math.BigDecimal.class },
	{ "raw", "bytes", byte[].class },
	{ "long raw", "ByteStream", java.io.InputStream.class },
	{ "rowid", "String", String.class },
	{ "bfile", "ByteStream", java.io.InputStream.class }
    };

    /**
     * populate sql type - java type map
     */
    protected void populateTypeMap() {
	// first populate ANSI sql types
	super.populateTypeMap();

	// next populate oracle-speicific types
	for (int i = 0; i < oracleTypeArray.length; i++) {
	    javaTypeStringMap.put(oracleTypeArray[i][0], oracleTypeArray[i][1]);
	    javaTypeMap.put(oracleTypeArray[i][0], oracleTypeArray[i][2]);
	}
    }

}
