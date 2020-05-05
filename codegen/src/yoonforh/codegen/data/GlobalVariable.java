/*
 * $Id: GlobalVariable.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

/**
 * vars information
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 09:37:00
 * @author   Yoon Kyung Koo
 */

public class GlobalVariable implements java.io.Serializable {
    private String name = null;
    private String desc = null;
    private String type = null;

    public GlobalVariable(String name, String desc, String type) {
	this.name = name;
	this.desc = desc;
	this.type = type;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return desc;
    }

    public String getType() {
	return type;
    }

}
