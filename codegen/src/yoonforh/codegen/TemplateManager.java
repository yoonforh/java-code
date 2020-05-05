/*
 * $Id: TemplateManager.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.*;
import yoonforh.codegen.data.TemplateInfoDAO;

/**
 *
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-02 15:07:57
 * @author   Yoon Kyung Koo
 */

public class TemplateManager {
    private static Logger logger
	= Logger.getLogger(TemplateManager.class.getName());
    private static final String DEFAULT_TEMPLATE_ROOT_DIR = "templates";
    private static final String TEMPLATE_XML = "template.xml";

    private HashMap templates = new HashMap();
    private String templateRootDir = DEFAULT_TEMPLATE_ROOT_DIR;

    private static TemplateManager instance = null;
    private TemplateManager() {}

    public static TemplateManager getInstance() {
	if (instance == null) {
	    instance = new TemplateManager();
	}
	return instance;
    }

    public void setTemplateRootDir(String path) {
	this.templateRootDir = path;
    }

    /**
     * read template directory
     */
    public void initTemplates() throws ApplicationException {
	templates.clear();

	File file = new File(templateRootDir);
	if (!file.isDirectory()) {
	    throw new ApplicationException("cannot find template root directory");
	}

	// this 
	File[] dirs = file.listFiles();
	if (dirs == null) {
	    throw new ApplicationException("no template registered");
	}

	for (int i = 0; i < dirs.length; i++) {
	    if (!dirs[i].isDirectory()) {
		try {
		    logger.warning("non-template directory - " + dirs[i].getCanonicalPath()); 
		} catch (IOException e) {}
		continue;
	    }

	    try {
		registerTemplate(dirs[i]);
	    } catch (ApplicationException e) {
		logger.log(Level.WARNING,
			   "directory does not contain proper templates - " + dirs[i],
			   e);
	    }
	}

    }

    /**
     * register a template directory
     * only accept template.xml and ${template name}.zip
     */
    private void registerTemplate(File dir) throws ApplicationException {
	logger.entering("MainPanel", "registerTemplate(dir - " + dir + ")");
	String name = dir.getName();
	String templateFileName = name + ".zip";
	File infoFile = null;
	File templateFile = null;

	String[] files = dir.list();

	for (int i = 0; i < files.length; i++) {
	    if (TEMPLATE_XML.equals(files[i])) {
		infoFile = new File(dir, files[i]);
	    } else if (files[i].equals(templateFileName)) { // then template zip file
		templateFile = new File(dir, files[i]);
	    }
	}

	if (infoFile == null || templateFile == null) {
	    throw new ApplicationException("required template files are not set");
	}

	readTemplate(infoFile, templateFile);
    }

    private void readTemplate(File infoFile, File templateFile) throws ApplicationException {
	// parse xml info file
	// and add files to some map

	String name = TemplateInfoDAO.getInstance().readTemplateInfo(infoFile);
	templates.put(name, templateFile);
    }

    public java.util.Map getTemplateMap() {
	return templates;
    }

    public File getTemplateFile(String name) {
	return (File) templates.get(name);
    }

}
