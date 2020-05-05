/*
 * $Id: CodeGenZipWorker.java,v 1.2 2003/05/31 00:14:28 yoonforh Exp $
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

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.*;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.util.zip.ZipWorker;
import yoonforh.codegen.processor.TextProcessor;
import yoonforh.codegen.data.TemplateInfoDAO;
import yoonforh.codegen.data.TemplateInfo;
import yoonforh.codegen.data.DocInfo;
import yoonforh.codegen.data.ProcessorInfo;
import yoonforh.codegen.data.ValuesStore;
import yoonforh.codegen.data.TemplateValues;

/**
 * code generator zip worker
 *
 * @version  $Revision: 1.2 $<br>
 *           created at 2003-03-30 00:45:29
 * @author   Yoon Kyung Koo
 */

public class CodeGenZipWorker implements ZipWorker {
    private static Logger logger
	= Logger.getLogger(CodeGenZipWorker.class.getName());

    private TemplateInfo info = null;
    private String defaultRootPath = null;
    private HashMap docInfos = null;
    private HashMap rootPaths = null;
    private HashMap charsets = null;
    private HashMap globals = null;
    private TextProcessor processor = null;

    public CodeGenZipWorker(String templateName) throws ApplicationException {
	TemplateInfoDAO dao = TemplateInfoDAO.getInstance();
	info = dao.getTemplateInfo(templateName);
	String defaultRootPathVar = info.getDefaultRootPathVar();
	if (defaultRootPathVar == null) {
	    throw new ApplicationException("default root path variable is not set");
	}
	docInfos = info.getDocInfos();

	TemplateValues values = ValuesStore.getInstance().getTemplateValues();
	rootPaths = (HashMap) values.getRootPaths().clone();
	defaultRootPath = (String) rootPaths.get(defaultRootPathVar);
	if (defaultRootPath == null) {
	    throw new ApplicationException("default root path value is not set");
	}
	charsets = (HashMap) values.getCharsets().clone();
	globals = (HashMap) values.getVariables().clone();

	// add default variables
	// e.g, %%timestamp%%
	addDefaultVariables(globals);

	processor = new TextProcessor(globals);
    }

    /**
     * make an output stream according to the given zip entry
     * if the entry is a directory then the output stream may be null
     *
     * @param entryName zip entry name
     * @param size size of this zip entry
     * @param isDirectory if the entry is a directory
     * @return an output stream to store the entry or null if the entry is null
     *         or to skip the entry 
     * @exception IOException an io error while opening the output stream
     */
    public OutputStream openEntry(String entryName, long size, boolean isDirectory)
		throws IOException, ApplicationException {
	logger.finest("getEntryOutputStream(entryName = " + entryName
		      + ", size = " + size
		      + ", isDirectory = " + isDirectory + ")");
	if (isDirectory) {
	    return null;
	}

	if (size > 0) {
	    return new ByteArrayOutputStream((int) size);
	}

	return new ByteArrayOutputStream();
    }

    /**
     * called when the entry extraction is finished.
     * user can cook the extracted stream here
     *
     * @param entryName zip entry name
     * @param isDirectory if the entry is a directory
     * @param output closed output stream
     * @exception IOException an io error while opening the output stream
     * @exception ApplicationException an application semantic error occurred while processing
     */
    public void closeEntry(String entryName, boolean isDirectory, OutputStream output)
	throws IOException, ApplicationException {
	// apply variables to byte array
	ByteArrayOutputStream bout = (ByteArrayOutputStream) output;
	String zipPath = entryName.replace('\\', '/');
	String rootPath = null;
	String relPath = null;
	String charset = null;
	HashMap varInfos = null;
	TextProcessor currentProcessor = processor;

	DocInfo docInfo = (DocInfo) docInfos.get(zipPath);
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("closeEntry(entryName = " + entryName
			  + ", isDirectory = " + isDirectory + "), docInfo - " + docInfo);
	}

	// in case of directory, plz do nothing right now
	if (isDirectory) {
	    return;
	}

	if (docInfo == null) {
	    // if doc info is null then this is an unregistered zip entry
	    // so, extract normally to the default root path var
	    rootPath = defaultRootPath;
	    relPath = zipPath;
	} else {
	    String var = docInfo.getRootPathVar();
	    if (var != null) {
		rootPath = (String) rootPaths.get(var);
	    }
	    if (rootPath == null || rootPath.length() == 0) {
		rootPath = defaultRootPath;
	    }

	    String processorType = null;
	    ProcessorInfo pInfo = docInfo.getProcessorInfo();
	    if (pInfo != null) {
		processorType = pInfo.getType();
	    }

	    if (processorType != null && processorType.length() > 0) {
		// then change current processor!
		currentProcessor = getProcessorInstance(processorType);
		if (logger.isLoggable(Level.FINEST)) {
		    logger.finest("processor type = " + processorType
				  + ", current processor = " + currentProcessor);
		}
	    }

	    // if relPath is specified then do not use zipPath
	    // relPath may contain variables
	    relPath = docInfo.getRelPath();
	    if (relPath == null || relPath.length() == 0) {
		relPath = zipPath;
	    }

	    var = docInfo.getCharsetVar();
	    if (var != null) {
		charset = (String) charsets.get(var);
	    }
	}

	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("processor = " + processor
			  + ", current processor = " + currentProcessor
			  + ", bout = " + bout);
	}

	currentProcessor.generateFile(
	    (bout != null ? bout.toByteArray() : null), rootPath, relPath,
	    isDirectory, charset, docInfo);
    }


    /**
     * get specified processor instance
     */
    private TextProcessor getProcessorInstance(String type) throws ApplicationException {
	TextProcessor tp = null;
	try {
	    Constructor cons = Class.forName(type).getConstructor(new Class[] { HashMap.class });
	    tp = (TextProcessor) cons.newInstance(new Object[] { globals });
	} catch (ClassNotFoundException e) {
	    logger.log(Level.SEVERE, "cannot get text processor - " + type, e);
	    throw new ApplicationException(e);
	} catch (InstantiationException e) {
	    logger.log(Level.SEVERE, "cannot get text processor - " + type, e);
	    throw new ApplicationException(e);
	} catch (IllegalAccessException e) {
	    logger.log(Level.SEVERE, "cannot get text processor - " + type, e);
	    throw new ApplicationException(e);	
	} catch (NoSuchMethodException e) {
	    logger.log(Level.SEVERE, "cannot get text processor - " + type, e);
	    throw new ApplicationException(e);
	} catch (InvocationTargetException e) {
	    logger.log(Level.SEVERE, "cannot get text processor - " + type, e);
	    throw new ApplicationException(e.getCause());
	}

	return tp;
    }

    private final static java.text.SimpleDateFormat dateFormat
	= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void addDefaultVariables(HashMap globalMap) {
	globalMap.put("timestamp", dateFormat.format(new java.util.Date()));
    }
}
