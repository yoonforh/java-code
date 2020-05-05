/*
 * $Id: Variable.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

import java.util.List;

/**
 * vars information
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 09:37:00
 * @author   Yoon Kyung Koo
 */

public class Variable implements java.io.Serializable {
    private String name = null;
    private List handlers = null;

    public Variable(String name, List handlers) {
	this.name = name;
	this.handlers = handlers;
    }

    public String getName() {
	return name;
    }

    /**
     * @return list of HandlerInfo objects
     */
    public List getHandlerList() {
	return handlers;
    }

    /**
     * for debugging purpose only
     */
    public String toString() {
	return "Variable(name - " + name
	    + ", handlers - " + handlers
	    + ")";
    }

}
