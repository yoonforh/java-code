/*
 * $Id: InputSheet.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

/**
 * template input sheet
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-02 14:43:51
 * @author   Yoon Kyung Koo
 */

public class InputSheet implements java.io.Serializable {
    /** map of key - value pairs */
    private HashMap rootPathValues;
    /** map of key - value pairs */
    private HashMap charsetValues;
    /** map of key - value pairs */
    private HashMap variableValues;

    public InputSheet(HashMap rootPathValues, HashMap charsetValues,
		      HashMap variableValues) {
	this.rootPathValues = rootPathValues;
	this.charsetValues = charsetValues;
	this.variableValues = variableValues;
    }

    public HashMap getRootPathValues() {
	return rootPathValues;
    }

    public HashMap getCharsetValues() {
	return charsetValues;
    }

    public HashMap getVariableValues() {
	return variableValues;
    }
}
