/*
 * $Id: DocInfo.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
 * template document info data container
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-25 11:22:08
 * @author   Yoon Kyung Koo
 */

public class DocInfo implements java.io.Serializable {
    private String zipPath;
    private ProcessorInfo processorInfo;
    private String relPath;
    private String rootPathVar;
    private String charsetVar;
    /** map of key - Variable pairs */
    private HashMap vars;
    private String description;

    public DocInfo(String zipPath, ProcessorInfo processorInfo, String relPath,
		   String rootPathVar, String charsetVar,
		   HashMap vars, String description) {
	this.zipPath = zipPath;
	this.processorInfo = processorInfo;
	this.relPath = relPath;
	this.rootPathVar = rootPathVar;
	this.charsetVar = charsetVar;
	this.vars = vars;
	this.description = description;
    }

    public String getZipPath() {
	return zipPath;
    }

    public ProcessorInfo getProcessorInfo() {
	return processorInfo;
    }

    public String getRelPath() {
	return relPath;
    }

    public String getRootPathVar() {
	return rootPathVar;
    }

    public String getCharsetVar() {
	return charsetVar;
    }

    public HashMap getVars() {
	return vars;
    }

    public String getDescription() {
	return description;
    }

    /**
     * for debugging purpose only
     */
    public String toString() {
	return "DocInfo(zipPath - " + zipPath
	    + ", processorInfo - " + processorInfo
	    + ", relPath - " + relPath
	    + ", rootPathVar - " + rootPathVar
	    + ", charsetVar - " + charsetVar
	    + ", vars - " + vars
	    + ", description - " + description
	    + ")";
    }
}
