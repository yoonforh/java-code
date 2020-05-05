/*
 * $Id: TemplateValues.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
 * template input data store
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 10:50:21
 * @author   Yoon Kyung Koo
 */

public class TemplateValues implements java.io.Serializable {
    private HashMap rootPaths = new HashMap();
    private HashMap charsets = new HashMap();
    private HashMap globals = new HashMap();
    private HashMap docs = new HashMap();

    public void setRootPaths(HashMap map) {
	rootPaths = map;
    }

    public void setRootPath(String var, String value) {
	rootPaths.put(var, value);
    }

    public HashMap getRootPaths() {
	return rootPaths;
    }

    public String getRootPath(String var) {
	return (String) rootPaths.get(var);
    }

    public void setCharsets(HashMap map) {
	charsets = map;
    }

    public void setCharset(String var, String value) {
	charsets.put(var, value);
    }

    public HashMap getCharsets() {
	return charsets;
    }

    public String getCharset(String var) {
	return (String) charsets.get(var);
    }

    public void setVariables(HashMap map) {
	globals = map;
    }

    public void setVariable(String var, String value) {
	globals.put(var, value);
    }

    public HashMap getVariables() {
	return globals;
    }

    public String getVariable(String var) {
	return (String) globals.get(var);
    }

    public void setDocs(HashMap map) {
	docs = map;
    }

    public void setDoc(String path, DocValues doc) {
	docs.put(path, doc);
    }

    public DocValues getDoc(String path) {
	return (DocValues) docs.get(path);
    }

    public void clear() {
	rootPaths.clear();
	charsets.clear();
	globals.clear();
	docs.clear();
    }
}
