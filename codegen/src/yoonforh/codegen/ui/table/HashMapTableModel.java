/*
 * $Id: HashMapTableModel.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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
import java.util.Set;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * JTable model class using HashMap as underlying data store
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 16:29:14
 * @author   Yoon Kyung Koo
 */

public class HashMapTableModel extends AbstractTableModel {
    private static Logger logger
	= Logger.getLogger(HashMapTableModel.class.getName());

    public static final String DEFAULT_KEY_COLUMN_TITLE = "Variables";
    public static final String DEFAULT_VALUE_COLUMN_TITLE = "Values";
    public static final String DEFAULT_DESCRIPTION_COLUMN_TITLE = "Description";

    public static final int VALUE_COLUMN_INDEX = 1;

    public static final int ALL_PLAIN_TYPE = 0;
    public static final int ALL_DIRECTORY_TYPE = 1;
    public static final int ALL_FILE_TYPE = 2;
    public static final int DIVERSE_TYPE = 3;

    public static final int STRING_VALUE_TYPE = 0;
    public static final int DIRECTORY_VALUE_TYPE = 1;
    public static final int FILE_VALUE_TYPE = 2;
    public static final int EMPTY_VALUE_TYPE = 3;
    public static final int CSV_VALUE_TYPE = 4;

    private static final String[] STRING_ZERO_ARRAY = new String[0];

    private HashMap map = new HashMap();
    private String[] keys = null;
    private String keyTitle = DEFAULT_KEY_COLUMN_TITLE;
    private String valueTitle = DEFAULT_VALUE_COLUMN_TITLE;
    private String descriptionTitle = DEFAULT_DESCRIPTION_COLUMN_TITLE;
    private int type = ALL_PLAIN_TYPE;
    private TableValueTypeChecker typeChecker = null;
    private TableKeyExplainer explainer = null;
    private boolean readOnly = false;

    /**
     * this table model accepts keys which are defined in key set only
     */
    public HashMapTableModel(Set keySet) {
	this(DEFAULT_KEY_COLUMN_TITLE, DEFAULT_VALUE_COLUMN_TITLE,
	     DEFAULT_DESCRIPTION_COLUMN_TITLE,
	     keySet, ALL_PLAIN_TYPE, null, null);
    }

    public HashMapTableModel(Set keySet, int type) {
	this(DEFAULT_KEY_COLUMN_TITLE, DEFAULT_VALUE_COLUMN_TITLE,
	     DEFAULT_DESCRIPTION_COLUMN_TITLE,
	     keySet, type, null, null);
    }

    public HashMapTableModel(Set keySet, int type, TableValueTypeChecker typeChecker) {
	this(DEFAULT_KEY_COLUMN_TITLE, DEFAULT_VALUE_COLUMN_TITLE,
	     DEFAULT_DESCRIPTION_COLUMN_TITLE,
	     keySet, type, typeChecker, null);
    }

    public HashMapTableModel(Set keySet, int type,
			     TableValueTypeChecker typeChecker, TableKeyExplainer explainer) {
	this(DEFAULT_KEY_COLUMN_TITLE, DEFAULT_VALUE_COLUMN_TITLE,
	     DEFAULT_DESCRIPTION_COLUMN_TITLE,
	     keySet, type, typeChecker, explainer);
    }

    public HashMapTableModel(String keyTitle, String valueTitle,
			     String descriptionTitle, Set keySet, int type) {
	this(keyTitle, valueTitle, descriptionTitle, keySet, type, null, null);
    }

    public HashMapTableModel(String keyTitle, String valueTitle,
			     String descriptionTitle, Set keySet,
			     int type, TableValueTypeChecker typeChecker,
			     TableKeyExplainer explainer) {
	this.keys = (String[]) keySet.toArray(STRING_ZERO_ARRAY);
	this.keyTitle = keyTitle;
	this.valueTitle = valueTitle;
	this.descriptionTitle = descriptionTitle;
	this.type = type;
	this.typeChecker = typeChecker;
	this.explainer = explainer;
    }

    /*
     * TableModel interface methods
     */

    public int getRowCount() {
	return keys.length;
    }

    /**
     * always have 2 or 3 columns
     * first is key name, second is the value, the third is description
     * the third column is optional
     */
    public int getColumnCount() {
	if (explainer == null) {
	    return 2;
	}

	return 3;
    }

    public String getColumnName(int column) {
	switch (column) {
	case 0 :
	    return keyTitle;

	case 1 :
	    return valueTitle;

	case 2 :
	    return descriptionTitle;
	}

	return null;
    }

    public Class getColumnClass(int column) {
	return String.class;
	// return getValueAt(0, column).getClass();
    }

    public Object getValueAt(int row, int column) {
	switch (column) {
	case 0 :
	    return keys[row];

	case 1 :
	    Object value = map.get(keys[row]);

	    if (value == null) {
		return (Object) "";
	    } else {
		return value;
	    }

	case 2 :
	    if (explainer == null) {
		return null;
	    }

	    return explainer.getDescription(keys[row]);
	}

	// never reach here
	return null;
    }

    public boolean isCellEditable(int row, int column) {
	if (readOnly || column != 1) {
	    return false;
	}

	// 1st column is editable
	return true;
    }

    public void setValueAt(Object value,
			   int row,
			   int column) {
	if (readOnly || column != 1) {
	    throw new UnsupportedOperationException("column "
						    + column + " is not editable");
	}

	logger.finest("setValueAt(value = " + value
		      + ", row = " + row
		      + ", column = " + column + ")");

	map.put(keys[row], value);
	fireTableCellUpdated(row, column);
    }

    /*
     * custom methods
     */
    /**
     * checks the value type
     */
    public int getValueTypeAt(int row, int column) {
	if (column != VALUE_COLUMN_INDEX) {
	    return STRING_VALUE_TYPE;
	}

	switch (type) {
	case ALL_DIRECTORY_TYPE :
	    return DIRECTORY_VALUE_TYPE;

	case ALL_FILE_TYPE :
	    return FILE_VALUE_TYPE;

	case DIVERSE_TYPE :
	    return typeChecker.checkValueType(keys[row]);

	default :
	    break;
	}

	return STRING_VALUE_TYPE;
    }

    /**
     * checks the value type
     * assume that the column is editable.
     * this is the usual case 'cause usually editors are set by columns
     * and the columns can be movable by drag and drop
     */
    public int getValueTypeAt(int row) {
	return getValueTypeAt(row, VALUE_COLUMN_INDEX);
    }

    /**
     * return current values
     */
    public HashMap getValues() {
	return map;
    }

    /**
     * make this table read only
     */
    public void setReadOnly(boolean readOnly) {
	this.readOnly = readOnly;
    }

    /**
     * bulk initialize table data
     */
    public void setDataMap(HashMap map) {
	this.map = map;
        fireTableStructureChanged();
    }

}
