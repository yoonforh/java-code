/*
 * $Id: ValuesStore.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

/**
 * input values repository
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 15:38:12
 * @author   Yoon Kyung Koo
 */

public class ValuesStore {
    // singleton instance
    private static ValuesStore instance = null;

    // instance variables
    private TemplateValues values = new TemplateValues();

    private ValuesStore() { }

    public static ValuesStore getInstance() {
	if (instance == null) {
	    instance = new ValuesStore();
	}

	return instance;
    }

    /**
     * clear current repository
     */
    public void clear() {
	values.clear();
    }

    public void setDocValues(String docPath, DocValues doc) {
	values.setDoc(docPath, doc);
    }

    public TemplateValues getTemplateValues() {
	return values;
    }

    public DocValues getDocValues(String docPath) {
	return values.getDoc(docPath);
    }

}
