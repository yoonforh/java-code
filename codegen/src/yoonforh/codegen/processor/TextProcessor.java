/*
 * $Id: TextProcessor.java,v 1.2 2003/05/31 00:14:28 yoonforh Exp $
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


package yoonforh.codegen.processor;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.regex.*;
import java.util.logging.*;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.handler.Handler;
import yoonforh.codegen.data.Variable;
import yoonforh.codegen.data.DocInfo;
import yoonforh.codegen.data.HandlerInfo;
import yoonforh.codegen.util.AsciiCharSequence;
import yoonforh.codegen.util.DirectoryClassLoader;

/**
 * global variable replacer.
 * global variables are declared in text files
 * using %%<variable name>%% notation
 *
 * @version  $Revision: 1.2 $<br>
 *           created at 2003-04-01 17:33:22
 * @author   Yoon Kyung Koo
 */

public class TextProcessor implements Processor {
    private static Logger logger
	= Logger.getLogger(TextProcessor.class.getName());

    // should use reluctant quanifiers (to use least char matching condition) 
    // against using greedy quantifiers
    // '#' is allowed for future use (nested variable? hmm... sounds unreasonable).
    // there are one matcher group excluding global group
    private static final String PATTERN_EXPR = "%%([\\p{Alpha}#][\\p{Alnum}#-_]*?)(?:\\:([\\p{Alnum}#-_]*?))?%%";

    // case processing instruction constants
    /** no op */
    public static final String CASE_INFO_STARTS_WITH_UPPER = "csu";
    /** decapitialize */
    public static final String CASE_INFO_STARTS_WITH_LOWER = "csl";
    /** to upper case */
    public static final String CASE_INFO_UPPER = "upper";
    /** to lower case */
    public static final String CASE_INFO_LOWER = "lower";

    /** make case sensitive decapitalize with underbar delimited string */
    public static final String CASE_INFO_STARTS_WITH_UPPER_UNDERBAR = "csu_";
    /** make case sensitive decapitalize with underbar delimited string */
    public static final String CASE_INFO_STARTS_WITH_LOWER_UNDERBAR = "csl_";
    /** make upper case removing underbar */
    public static final String CASE_INFO_UPPER_UNDERBAR = "upper_";
    /** make lower case removing underbar */
    public static final String CASE_INFO_LOWER_UNDERBAR = "lower_";

    /** make case sensitive decapitalize with underbar delimited string without db table prefix */
    public static final String CASE_INFO_STARTS_WITH_UPPER_UNDERBAR_DB = "csu_db";
    /** make case sensitive decapitalize with underbar delimited string without db table prefix */
    public static final String CASE_INFO_STARTS_WITH_LOWER_UNDERBAR_DB = "csl_db";
    /** make upper case removing underbar without db table prefix */
    public static final String CASE_INFO_UPPER_UNDERBAR_DB = "upper_db";
    /** make lower case removing underbar without db table prefix */
    public static final String CASE_INFO_LOWER_UNDERBAR_DB = "lower_db";
    /** just trim db table prefix */
    public static final String CASE_INFO_DB = "db";

    private static final int BUFFER_SIZE = 8192;
    private static String handlerDirectory = null;
    private static final String SYSPROP_HANDLER_DIRECTORY = "handler.directory";

    protected HashMap vars = null;

    static { // static initializer. set the handler class directory
	handlerDirectory = System.getProperty(SYSPROP_HANDLER_DIRECTORY);
    }

    /*
     * NOTE that Pattern is immutable and re-entrant but Matcher is not
     */
    protected static Pattern pattern = null;

    // this constructor is only for child classes
    protected TextProcessor() {}

    public TextProcessor(HashMap vars) {
	this.vars = vars;
    }

    protected Pattern getPattern() {
	if (pattern == null) {
	    pattern = Pattern.compile(PATTERN_EXPR);
	}

	return pattern;
    }

    /**
     * find matching parts, apply patterns and invoke handler on that
     *
     * @param contents contents which contains variable declarations
     * @return replaced strings or null if no replacement applied
     */
    public String apply(CharSequence contents) {
	return apply(contents, (HashMap) null);
    }

    /**
     * find matching parts, apply patterns and invoke handler on that
     *
     * @param contents contents which contains variable declarations
     * @param varInfos variable name - Variable object pair map
     * @return replaced strings
     */
    public String apply(CharSequence contents, HashMap varInfos) {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("apply(contents - " + contents
			  + ", varInfos - " + varInfos + ")");
	}

	Matcher matcher = getPattern().matcher(contents);
	StringBuffer buffer = new StringBuffer(contents.length() + 50);

	int count = 0;
	while (matcher.find()) {
	    count++;

	    // no arg group() or group(0) indicates all the matched string
	    String variable = matcher.group(1);
	    // if no case instruction given, this will be null
	    String caseInst = matcher.group(2);
	    // here value can be null when the var type is empty
	    String value = (String) vars.get(variable);
	    if (logger.isLoggable(Level.FINEST)) {
		logger.finest("variable - " + variable + ", value - " + value);
	    }

	    // case processing
	    if (value != null) {
		value = processCase(value, caseInst);
	    }

	    // 1st, run handler on this value
	    if (varInfos != null) {
		Variable varInfo = (Variable) varInfos.get(variable);
		List handlers = null;
		if (varInfo != null) {
		    handlers = varInfo.getHandlerList();
		}
		if (handlers != null) {
		    Iterator iter = handlers.iterator();
		    while (iter.hasNext()) {
			HandlerInfo handler = (HandlerInfo) iter.next();

			String handlerClazz = handler.getType();
			HashMap paramMap = handler.getParamMap();
			if (handlerClazz != null) {
			    value = invokeHandler(handlerClazz, value, paramMap);
			}
		    }
		}
	    }

	    if (value != null) {
		// apply the result
		matcher.appendReplacement(buffer, value);
	    } else { // value is null then just use variable itself
		logger.warning("cannot find value for variable - " + variable);
		matcher.appendReplacement(buffer, variable);
	    }
	}

	if (count > 0) {
	    matcher.appendTail(buffer);
	    return buffer.toString();
	}

	// just return original
	return contents.toString();
    }

    private String invokeHandler(String clazz, String value, HashMap paramMap) {
	String result = null;
	ClassLoader loader = null;

	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("handlerDirectory - " + handlerDirectory);
	}

	if (handlerDirectory != null) {
	    try {
		loader = DirectoryClassLoader.getClassLoader(new File(handlerDirectory));
	    } catch (IOException e) {
		logger.log(Level.WARNING, "cannot get handler class loader, directory - "
			   + handlerDirectory, e);
	    }
	}

	if (loader == null) {
	    loader = getClass().getClassLoader();
	}

	try {
	    Handler handler = (Handler) Class.forName(clazz, true, loader).newInstance();
	    result = handler.handle(value, paramMap, vars);
	} catch (ClassNotFoundException e) {
	    logger.log(Level.SEVERE, "cannot run handler - " + clazz, e);
	} catch (InstantiationException e) {
	    logger.log(Level.SEVERE, "cannot run handler - " + clazz, e);
	} catch (IllegalAccessException e) {
	    logger.log(Level.SEVERE, "cannot run handler - " + clazz, e);
	} catch (ApplicationException e) {
	    logger.log(Level.SEVERE, "error while running handler - " + clazz, e);
	}

	if (result == null) {
	    return value;
	}
	return result;
    }


    /**
     * case processing
     *
     * @param value value which case processing applied
     * @param inst some reserved case instruction
     */
    protected String processCase(String value, String inst) {
	if (inst == null) {
	    return value;
	}

	if (CASE_INFO_STARTS_WITH_UPPER.equals(inst)) {
	    // there are nothing to handle this case
	    // how could I determine the word boundary of
	    // the lower cased word composition
	    // just return original value
	} else if (CASE_INFO_STARTS_WITH_LOWER.equals(inst)) {
	    value = java.beans.Introspector.decapitalize(value);
	} else if (CASE_INFO_UPPER.equals(inst)) {
	    value = value.toUpperCase();
	} else if (CASE_INFO_LOWER.equals(inst)) {
	    value = value.toLowerCase();
	} else if (CASE_INFO_STARTS_WITH_UPPER_UNDERBAR.equals(inst)) {
	    value = removeUnderBar(value);
	} else if (CASE_INFO_STARTS_WITH_LOWER_UNDERBAR.equals(inst)) {
	    value = java.beans.Introspector.decapitalize(removeUnderBar(value));
	} else if (CASE_INFO_UPPER_UNDERBAR.equals(inst)) {
	    value = removeUnderBar(value).toUpperCase();
	} else if (CASE_INFO_LOWER_UNDERBAR.equals(inst)) {
	    value = removeUnderBar(value).toLowerCase();
	} else if (CASE_INFO_STARTS_WITH_UPPER_UNDERBAR_DB.equals(inst)) {
	    value = removeUnderBar(trimDBPrefix(value));
	} else if (CASE_INFO_STARTS_WITH_LOWER_UNDERBAR_DB.equals(inst)) {
	    value = java.beans.Introspector.decapitalize(removeUnderBar(trimDBPrefix(value)));
	} else if (CASE_INFO_UPPER_UNDERBAR_DB.equals(inst)) {
	    value = removeUnderBar(trimDBPrefix(value)).toUpperCase();
	} else if (CASE_INFO_LOWER_UNDERBAR_DB.equals(inst)) {
	    value = removeUnderBar(trimDBPrefix(value)).toLowerCase();
	} else if (CASE_INFO_DB.equals(inst)) {
	    value = trimDBPrefix(value);
	}

	return value;
    }

    /**
     * make case sensitive decapitalized string which starts with upper case
     * of underbar delimited one
     */
    protected String removeUnderBar(String value) {
	String[] words = value.toLowerCase().split("_");
	StringBuffer buffer = new StringBuffer();

	for (int i = 0; i < words.length; i++) {
	    char[] chars = words[i].toCharArray();
	    if (chars.length > 0) {
		chars[0] = Character.toUpperCase(chars[0]);
		buffer.append(chars);
	    }
	}

	return buffer.toString();
    }

    /**
     * remove db table prefix
     */
    protected String trimDBPrefix(String value) {
	String[] words = value.toLowerCase().split("_", 2);
	if (words.length == 2) {
	    return words[1];
	}

	return value;
    }

    /**
     * generate converted file
     */
    public void generateFile(byte[] bytes, String rootPath, String relPath, boolean isDirectory,
			     String charset, DocInfo docInfo)
	throws IOException, ApplicationException {
	/* first apply variables to rel path */
	relPath = apply(relPath);

	OutputStream fout = openFileStream(rootPath, relPath, isDirectory);
	if (fout == null) {
	    return;
	}

	HashMap varInfos = null;
	if (docInfo != null) {
	    varInfos = docInfo.getVars();
	}

	try {
	    String contents = null;
	    if (charset == null || charset.length() == 0) {
		contents = apply(new String(bytes), varInfos);
	    } else if ("US-ASCII".equals(charset.toUpperCase())) {
		// if us-ascii then we will use faster ascii char sequence as regex input
		contents = apply(new AsciiCharSequence(bytes), varInfos);
	    } else {
		contents = apply(new String(bytes, charset), varInfos);
	    }

	    BufferedWriter writer = null;
	    if (charset == null || charset.length() == 0) {
		writer = new BufferedWriter(new OutputStreamWriter(fout), BUFFER_SIZE);
	    } else {
		writer = new BufferedWriter(new OutputStreamWriter(fout, charset), BUFFER_SIZE);
	    }

	    // I use reader to deal with new line characters
	    // which originate from XML code blocks
	    BufferedReader reader = new BufferedReader(new StringReader(contents));
	    String line = null;
	    while ((line = reader.readLine()) != null) {
		writer.write(line, 0, line.length());
		writer.newLine();
	    }

	    writer.flush();
	    writer.close();
	} finally {
	    fout.close();
	}
    }

    protected OutputStream openFileStream(String rootPath, String relPath,
					  boolean isDirectory)
	throws IOException, ApplicationException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("openFileStream(rootPath - " + rootPath
			  + ", relPath - " + relPath
			  + ", isDirectory - " + isDirectory
			  + ")");
	}

	File rootDir = new File(rootPath);
	if (!rootDir.isDirectory()) {
	    throw new ApplicationException("root path is not a directory - "
					   + rootPath);
	}

	File file = new File(rootDir, relPath);
	if (isDirectory) {
	    if (!file.exists()) {
		if (!file.mkdirs()) {
		    logger.warning("Unzip error. cannot create directory named "
				   + relPath);
		}
	    }

	    // directory only needs mkdir() so return
	    return null;
	}

	// create parent directory
	File parent = file.getParentFile();
	if (!parent.exists()) {
	    if (!parent.mkdirs()) {
		logger.warning("Unzip error. cannot create directory - "
			       + parent.getAbsolutePath());
	    }
	}

	// open file ouput stream
	return new FileOutputStream(file);
    }

}
