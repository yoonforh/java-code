/*
 * $Id: TemplateInfo.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

import java.util.*;

/**
 * template info data container
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-25 10:44:26
 * @author   Yoon Kyung Koo
 */

public class TemplateInfo implements java.io.Serializable {
    private String name;
    private String description;
    private String defaultRootPathVar;
    /** map of key - description pairs */
    private HashMap rootPathVars;
    /** map of key - description pairs */
    private HashMap charsetVars;
    /** map of key - GlobalVariable pairs */
    private HashMap globalVars;
    /** map of zipPath - DocInfo pairs */
    private HashMap docInfos;

    public TemplateInfo(String name, String description, String defaultRootPathVar,
			HashMap rootPathVars, HashMap charsetVars,
			HashMap globalVars, HashMap docInfos) {
	this.name = name;
	this.description = description;
	this.defaultRootPathVar = defaultRootPathVar;
	this.rootPathVars = rootPathVars;
	this.charsetVars = charsetVars;
	this.globalVars = globalVars;
	this.docInfos = docInfos;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public String getDefaultRootPathVar() {
	return defaultRootPathVar;
    }

    public HashMap getRootPathVars() {
	return rootPathVars;
    }

    public HashMap getCharsetVars() {
	return charsetVars;
    }

    public HashMap getGlobalVars() {
	return globalVars;
    }

    public HashMap getDocInfos() {
	return docInfos;
    }
}
