/*
 * $Id: UpperCase.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

/**
 *
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-03 18:03:01
 * @author   Yoon Kyung Koo
 */

public class UpperCase implements Handler {
    /**
     * some operations on the given text using the variable map information
     *
     * @param text main text
     * @param paramMap parameters map
     * @param globalMap identified variables map
     */
    public String handle(String text, java.util.HashMap paramMap, java.util.HashMap globalMap) {
	return text.toUpperCase();
    }

}
