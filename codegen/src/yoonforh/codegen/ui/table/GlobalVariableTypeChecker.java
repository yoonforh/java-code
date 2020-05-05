/*
 * $Id: GlobalVariableTypeChecker.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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


package yoonforh.codegen.ui.table;

import java.util.HashMap;
import yoonforh.codegen.data.GlobalVariable;

/**
 *
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-01 11:20:58
 * @author   Yoon Kyung Koo
 */

public class GlobalVariableTypeChecker implements TableValueTypeChecker {
    private static final String STRING_TYPE_STRING = "string";
    private static final String FILE_TYPE_STRING = "file";
    private static final String DIRECTORY_TYPE_STRING = "directory";
    private static final String EMPTY_TYPE_STRING = "empty";
    private static final String CSV_TYPE_STRING = "csv";
    private HashMap globalVars = null;

    public GlobalVariableTypeChecker(HashMap map) {
	globalVars = map;
    }

    public int checkValueType(String key) {
	GlobalVariable var = (GlobalVariable) globalVars.get(key);
	if (var == null) {
	    return HashMapTableModel.STRING_VALUE_TYPE;
	}

	String type = var.getType();
	if (type == null || STRING_TYPE_STRING.equals(type)) {
	    return HashMapTableModel.STRING_VALUE_TYPE;
	} else if (FILE_TYPE_STRING.equals(type)) {
	    return HashMapTableModel.FILE_VALUE_TYPE;
	} else if (DIRECTORY_TYPE_STRING.equals(type)) {
	    return HashMapTableModel.DIRECTORY_VALUE_TYPE;
	} else if (EMPTY_TYPE_STRING.equals(type)) {
	    return HashMapTableModel.EMPTY_VALUE_TYPE;
	} else if (CSV_TYPE_STRING.equals(type)) {
	    return HashMapTableModel.CSV_VALUE_TYPE;
	}

	return HashMapTableModel.STRING_VALUE_TYPE;
    }
}
