/*
 * $Id: ZipWorker.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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


package yoonforh.codegen.util.zip;

import java.io.IOException;
import java.io.OutputStream;
import yoonforh.codegen.ApplicationException;

/**
 * kind of callback
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-27 14:57:59
 * @author   Yoon Kyung Koo
 */

public interface ZipWorker {
    /**
     * make an output stream according to the given zip entry
     * if the entry is a directory then the output stream may be null
     *
     * @param entryName zip entry name
     * @param entrySize size of this entry
     * @param isDirectory if the entry is a directory
     * @return an output stream to store the entry or null if the entry is null
     *         or to skip the entry 
     * @exception IOException an io error while opening the output stream
     * @exception ApplicationException an application semantic error occurred while processing
     */
    OutputStream openEntry(String entryName, long entrySize, boolean isDirectory)
	throws IOException, ApplicationException;

    /**
     * called when the entry extraction is finished. 
     * user can cook the extracted stream here
     *
     * @param entryName zip entry name
     * @param isDirectory if the entry is a directory
     * @param output closed output stream
     * @exception IOException an io error while opening the output stream
     * @exception ApplicationException an application semantic error occurred while processing
     */
    void closeEntry(String entryName, boolean isDirectory, OutputStream output)
	throws IOException, ApplicationException;

}
