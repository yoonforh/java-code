/*
 * $Id: CodeBlockProcessor.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

import java.util.regex.*;
import java.util.HashMap;
import java.util.logging.*;

/**
 * code block variables text processor.
 * code block variables are declared in the text block
 * using ${<variable name>:<case handling instruction>} notation.
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-11 14:08:02
 * @author   Yoon Kyung Koo
 */

public class CodeBlockProcessor extends TextProcessor {
    private static Logger logger
	= Logger.getLogger(CodeBlockProcessor.class.getName());

    // should use reluctant quanifiers (to use least char matching condition) 
    // against using greedy quantifiers
    // '#' is allowed for future use (nested variable? hmm... sounds unreasonable).
    // there are two matcher group excluding global group.
    // optional matcher group will be null if there are no case instructions
    private static final String PATTERN_EXPR
	= "\\$\\{([\\p{Alpha}#][\\p{Alnum}#-_]*?)(?:\\:([\\p{Alnum}#-_]*?))?\\}";

    /*
     * NOTE that Pattern is immutable and re-entrant but Matcher is not
     */
    protected static Pattern pattern = null;

    /**
     * override this method to use your own pattern.
     * note that the static constant PATTERN_EXPR could be hided
     */
    protected Pattern getPattern() {
	if (pattern == null) {
	    pattern = Pattern.compile(PATTERN_EXPR);
	}

	return pattern;
    }

    // make a singleton
    protected static CodeBlockProcessor instance = null;
    protected CodeBlockProcessor() {}

    public static CodeBlockProcessor getInstance() {
	if (instance == null) {
	    instance = new CodeBlockProcessor();
	}

	return instance;
    }

    /**
     * find matching parts, apply patterns and invoke handler on that
     *
     * @param buffer string buffer
     * @param contents contents which contains variable declarations
     * @return replaced strings or null if no replacement applied
     */
    public void apply(StringBuffer buffer, CharSequence contents, HashMap vars) {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("apply(contents - " + contents
			  + ", vars - " + vars + ")");
	}

	Matcher matcher = getPattern().matcher(contents);

	int count = 0;
	while (matcher.find()) {
	    count++;

	    // no arg group() or group(0) indicates all the matched string
	    String variable = matcher.group(1);
	    // if no case instruction given, this will be null
	    String caseInst = matcher.group(2);
	    String value = (String) vars.get(variable);
	    if (logger.isLoggable(Level.FINEST)) {
		logger.finest("variable - " + variable + ", value - " + value);
	    }

	    if (value != null) {
		value = processCase(value, caseInst);

		// apply the result
		matcher.appendReplacement(buffer, value);
	    } else {
		logger.warning("cannot find value for variable - " + variable);
		matcher.appendReplacement(buffer, variable);
	    }
	}

	if (count > 0) {
	    matcher.appendTail(buffer);
	} else {
	    // just return original
	    buffer.append(contents);
	}
    }
}
