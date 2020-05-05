/*
 * $Id: CSVUtil.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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


package yoonforh.codegen.util;

/**
 * CSV(Comma Separated Values) util
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-10 17:08:04
 * @author   Yoon Kyung Koo
 */

public class CSVUtil {
    /**
     * split comma separated values into array.
     * ignores the trailing spaces after comma
     */
    public static String[] split(String csv) {
	return csv.split(", *");
    }

    /**
     * get split comma separated values from list.
     */
    public static String getCSVFromList(java.util.List list) {
	if (list == null || list.size() == 0) {
	    return null;
	}

	StringBuffer buffer = new StringBuffer();
	java.util.Iterator it = list.iterator();

	while (it.hasNext()) {
	    String value = (String) it.next();
	    buffer.append(value).append(',');
	}
	buffer.setLength(buffer.length() - 1);

	return buffer.toString();
    }
}
