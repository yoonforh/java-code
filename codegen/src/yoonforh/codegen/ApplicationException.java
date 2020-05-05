/*
 * $Id: ApplicationException.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen;

/**
 * generic application exception
 * JDK 1.4 style
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-24 17:32:06
 * @author   Yoon Kyung Koo
 * @since JDK 1.4
 */

public class ApplicationException extends Exception {
    public ApplicationException() {}

    public ApplicationException(String message) {
	super(message);
    }

    public ApplicationException(String message, Throwable cause) {
	super(message, cause);
    }

    public ApplicationException(Throwable cause) {
	super(cause);
    }
}
