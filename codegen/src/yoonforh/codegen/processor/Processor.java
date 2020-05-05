/*
 * $Id: Processor.java,v 1.2 2003/05/31 00:14:28 yoonforh Exp $
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

import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.data.DocInfo;

/**
 * 
 *
 * @version  $Revision: 1.2 $<br>
 *           created at 2003-05-14 22:50:16
 * @author   Yoon Kyung Koo
 */

public interface Processor {
    /**
     * find matching parts, apply patterns and invoke handler on that
     *
     * @param contents contents which contains variable declarations
     * @return replaced strings or null if no replacement applied
     */
    public String apply(CharSequence contents);

    /**
     * find matching parts, apply patterns and invoke handler on that
     *
     * @param contents contents which contains variable declarations
     * @param varInfos variable name - Variable object pair map
     * @return replaced strings
     */
    public String apply(CharSequence contents, java.util.HashMap varInfos);

    /**
     * generate converted file
     */
    public void generateFile(byte[] bytes, String rootPath, String relPath, boolean isDirectory,
			     String charset, DocInfo docInfo)
	throws java.io.IOException, ApplicationException;

}
