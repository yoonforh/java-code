/*
 * $Id: GlobalVariableKeyExplainer.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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


package yoonforh.codegen.ui.table;

import java.util.HashMap;
import yoonforh.codegen.data.GlobalVariable;

/**
 *
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-01 13:09:31
 * @author   Yoon Kyung Koo
 */

public class GlobalVariableKeyExplainer implements TableKeyExplainer {
    private HashMap globalVars = null;

    public GlobalVariableKeyExplainer(HashMap map) {
	globalVars = map;
    }

    public String getDescription(String key) {
	GlobalVariable var = (GlobalVariable) globalVars.get(key);
	if (var == null) {
	    return null;
	}

	return var.getDescription();
    }

}
