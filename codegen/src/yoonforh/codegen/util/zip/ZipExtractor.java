/*
 * $Id: ZipExtractor.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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

import java.io.*;
import java.util.zip.*;
import java.util.logging.*;
import yoonforh.codegen.ApplicationException;

/**
 * zip file extractor which uses kinda template method pattern
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-26 11:36:27
 * @author   Yoon Kyung Koo
 */

public class ZipExtractor {
    private final static int BUFFER_SIZE = 8192;
    private static Logger logger = Logger.getLogger(ZipExtractor.class.getName());

    // worker interface for template method
    private ZipWorker worker = null;

    public ZipExtractor(ZipWorker worker) {
	this.worker = worker;
    }

    public void extract(File zipFile) throws IOException, ApplicationException {
	if (zipFile == null) {
	    logger.warning("src zip file is null.");
	    throw new NullPointerException("src zip file is null");
	}

	ZipInputStream zis = null;

	try {
	    zis = new ZipInputStream(new BufferedInputStream(
					 new FileInputStream(zipFile), BUFFER_SIZE));
	    byte[] buffer = new byte[BUFFER_SIZE];

	    while (true) {
		ZipEntry entry = zis.getNextEntry();
		if (entry == null) {
		    break;
		}

		String entryName = entry.getName();
		long entrySize = entry.getSize();
		boolean isDirectory = entry.isDirectory();
		if (logger.isLoggable(Level.FINEST)) {
		    logger.finest("ENTRY INFORMATION -----------\n"
				  + "name = " + entryName
				  + ", time = " + new java.util.Date(entry.getTime()) 
				  + ", size = " + entrySize
				  + ", compressed size = " + entry.getCompressedSize()
				  + ", crc = " + entry.getCrc()
				  + ", method = " + entry.getMethod()
				  + ", extra = " + entry.getExtra()
				  + ", comment = " + entry.getComment()
				  + ", is directory = " + isDirectory);
		}

		OutputStream output = worker.openEntry(entryName, entrySize, isDirectory);
		// check if the stream is null
		if (output == null) {
		    worker.closeEntry(entryName, isDirectory, output);
		    continue;
		}

		int result = 0, nWrote = 0;

		try {
		    // extract zip file
		    while ((result = zis.read(buffer, 0, BUFFER_SIZE)) > 0) {
			output.write(buffer, 0, result);
			nWrote += result;
		    }

		    logger.finest("wrote " + nWrote + " bytes");
		} finally {
		    output.close();
		}

		// cook extracted zip streams
		worker.closeEntry(entryName, isDirectory, output);
	    }
	} finally {
	    if (zis != null) {
		zis.close();
	    }
	}
    }
}
