/*
 * $Id: TableValueTypeChecker.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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

/**
 *
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-01 11:14:14
 * @author   Yoon Kyung Koo
 */

public interface TableValueTypeChecker {
    /**
     * @return one of HashMapTableModel.STRING_VALUE_TYPE,
     *                HashMapTableModel.DIRECTORY_VALUE_TYPE,
     *                HashMapTableModel.FILE_VALUE_TYPE
     */
    int checkValueType(String key);
}
