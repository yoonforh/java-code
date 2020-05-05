/*
 * $Id: BatchCodeGenerator.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

import java.io.*;
import java.util.HashMap;
import java.util.logging.*;
import yoonforh.codegen.data.TemplateInfoDAO;
import yoonforh.codegen.data.TemplateInfo;
import yoonforh.codegen.data.BatchInputDAO;
import yoonforh.codegen.data.InputSheet;
import yoonforh.codegen.data.ValuesStore;
import yoonforh.codegen.data.TemplateValues;
import yoonforh.codegen.util.CodeGenZipWorker;
import yoonforh.codegen.util.zip.ZipExtractor;

/**
 * batch code generator
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-02 12:13:24
 * @author   Yoon Kyung Koo
 */

public class BatchCodeGenerator {
    private static Logger logger
	= Logger.getLogger(BatchCodeGenerator.class.getName());
    public static final String INPUT_FILE_EXT = ".cgn";
    private TemplateManager templateManager = TemplateManager.getInstance();

    /**
     * there are two batch modes<br>
     * a. one xml file generate mode<br>
     * b. directory in which several xml files resides generate mode
     */
    public void generate(String fileName) throws ApplicationException {
	// first of all, initialize templates
	templateManager.initTemplates();

	File file = new File(fileName);
	if (file.isDirectory()) {
	    File[] files = file.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
			if (name.endsWith(INPUT_FILE_EXT)) {
			    return true;
			}

			return false;
		    }
		});

	    for (int i = 0; i < files.length; i++) {
		try {
		    generateUsing(files[i]);
		} catch (ApplicationException e) {
		    logger.log(Level.SEVERE, "batch job failed while using input file "
			       + files[i].getAbsolutePath(), e);
		}
	    }
	} else {
	    try {
		generateUsing(file);
	    } catch (ApplicationException e) {
		logger.log(Level.SEVERE, "batch job failed while using input file "
			   + file.getAbsolutePath(), e);
	    }
	}
    }

    private void generateUsing(File file) throws ApplicationException {
	logger.finest("start generation using file " + file.getAbsolutePath());

	BatchInputDAO inputDAO = BatchInputDAO.getInstance();
	String templateName = inputDAO.readInputFile(file);

	java.util.Iterator iter = inputDAO.getInputSheetsIterator();
	while (iter.hasNext()) {
	    InputSheet sheet = (InputSheet) iter.next();
	    TemplateInfoDAO tmplDAO = TemplateInfoDAO.getInstance();
	    TemplateInfo info = tmplDAO.getTemplateInfo(templateName);

	    HashMap rootPathValues = sheet.getRootPathValues();
	    HashMap charsetValues = sheet.getCharsetValues();
	    HashMap variableValues = sheet.getVariableValues();

	    if (rootPathValues.size() != info.getRootPathVars().size()) { // if something is not specified
		throw new ApplicationException("You should fill in all the root path variables.");
	    }

	    if (charsetValues.size() != info.getCharsetVars().size()) { // if something is not specified
		throw new ApplicationException("You should fill in all the charset variables.");
	    }

	    if (variableValues.size() != info.getGlobalVars().size()) { // if something is not specified
		throw new ApplicationException("You should fill in all the global variables.");
	    }

	    TemplateValues values = ValuesStore.getInstance().getTemplateValues();
	    values.setRootPaths(rootPathValues);
	    values.setCharsets(charsetValues);
	    values.setVariables(variableValues);

	    java.io.File zipFile = templateManager.getTemplateFile(templateName);

	    try {
		ZipExtractor unzipper = new ZipExtractor(new CodeGenZipWorker(templateName));
		unzipper.extract(zipFile);
	    } catch (java.io.IOException e) {
		logger.throwing("BatchCodeGenerator", "generateUsing(" 
				+ file.getAbsolutePath() + ")", e);

		throw new ApplicationException("cannot extract template files - " + e.getMessage());
	    } catch (ApplicationException e) {
		logger.throwing("BatchCodeGenerator", "generateUsing(" 
				+ file.getAbsolutePath() + ")", e);
		throw e;
	    }

	}
    }

}
