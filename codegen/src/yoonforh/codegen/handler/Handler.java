/*
 * $Id: Handler.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen.handler;

import java.util.HashMap;

/**
 * variable handler signature interface.
 * all the variable handlers should implement this interface
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-25 15:10:14
 * @author   Yoon Kyung Koo
 */

public interface Handler {
    /**
     * some operations on the given text using the variable map information
     *
     * @param text main text
     * @param paramMap parameters map
     * @param globalMap identified variables map
     */
    String handle(String text, HashMap paramMap, HashMap globalMap)
	 throws yoonforh.codegen.ApplicationException;
}
