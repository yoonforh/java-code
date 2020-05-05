/*
 * $Id: HandlerInfo.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

import java.util.HashMap;

/**
 * handler information
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-07 15:55:03
 * @author   Yoon Kyung Koo
 */

public class HandlerInfo implements java.io.Serializable {
    private String type = null;
    private HashMap params = null;

    public HandlerInfo(String type, HashMap params) {
	this.type = type;
	this.params = params;
    }

    public String getType() {
	return type;
    }

    public HashMap getParamMap() {
	return params;
    }

    /**
     * for debugging purpose only
     */
    public String toString() {
	return "HandlerInfo(type - " + type
	    + ", params - " + params
	    + ")";
    }

}
