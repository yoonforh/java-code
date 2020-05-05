/*
 * $Id: ZipUtil.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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


package yoonforh.codegen.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.logging.*;

/**
 * zip/unzip utility class
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-26 11:13:16
 * @author   Yoon Kyung Koo
 */

public class ZipUtil {
    private final static int BUFFER_SIZE = 8192;
    private static Logger logger = null;

    static {
	logger = Logger.getLogger(ZipUtil.class.getName());
    }

    /**
     * extract zip file to given path
     */
    public static void extractTo(String zipFile, String outputPath) throws IOException {
	extractToExcept(zipFile, outputPath, (Set) null);
    }

    /**
     * extract zip file to given path except given entries
     */
    public static void extractToExcept(String zipFile, String outputPath, Set exclusion)
		throws IOException {
	if (zipFile == null || outputPath == null) {
	    logger.warning("src zip file or output path is null.");
	    throw new IOException("src zip file or output path is null");
	}

	File outDir = new File(outputPath);
	if (!outDir.isDirectory()) {
	    logger.warning("output path should be a directory : " + outputPath);
	    throw new IOException("output path is not a directory : " + outputPath);
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
		logger.finest("ENTRY INFORMATION -----------\n"
			    + "name = " + entryName
			    + ", time = " + new java.util.Date(entry.getTime()) 
			    + ", size = " + entry.getSize()
			    + ", compressed size = " + entry.getCompressedSize()
			    + ", crc = " + entry.getCrc()
			    + ", method = " + entry.getMethod()
			    + ", extra = " + entry.getExtra()
			    + ", comment = " + entry.getComment()
			    + ", is directory = " + entry.isDirectory());

		// check exclusion map
		if (exclusion != null && exclusion.contains(entryName)) {
		    continue;
		}

		File file = new File(outputPath, entryName);
		if (entry.isDirectory()) {
		    if (!file.exists()) {
			if (!file.mkdirs()) {
			    logger.warning("Unzip error. cannot create directory named "
					   + entryName);
			}
		    }
		    continue;
		} else {
		    File parent = file.getParentFile();
		    if (!parent.exists()) {
			if (!parent.mkdirs()) {
			    logger.warning("Unzip error. cannot create directory - "
					   + parent.getAbsolutePath());
			}
		    }
		}

		int result = 0, nWrote = 0;
		FileOutputStream fout = null;
		try {
		    fout = new FileOutputStream(file);

		    while ((result = zis.read(buffer, 0, BUFFER_SIZE)) > 0) {
			fout.write(buffer, 0, result);
			nWrote += result;
		    }

		    logger.finest("wrote " + nWrote + " bytes");
		} finally {
		    if (fout != null) {
			fout.close();
		    }
		}
	    }
	} finally {
	    if (zis != null) {
		zis.close();
	    }
	}
    }

    /**
     * extract the files specified in <code>inclusion</code> to given path
     * If <code>inclusion</code> is null then all files are extracted.
     * @param zipFile
     * @param outputPath
     * @param inclusion target file names
     */
    public static void extractTo(String zipFile, String outputPath, Set inclusion)
	throws IOException {
	if (zipFile == null || outputPath == null) {
	    logger.warning("src zip file or output path is null.");
	    throw new IOException("src zip file or output path is null");
	}

	File outDir = new File(outputPath);
	if (!outDir.isDirectory()) {
	    logger.warning("output path should be a directory : " + outputPath);
	    throw new IOException("output path is not a directory : " + outputPath);
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
		logger.finest("ENTRY INFORMATION -----------\n"
			    + "name = " + entryName
			    + ", time = " + new java.util.Date(entry.getTime()) 
			    + ", size = " + entry.getSize()
			    + ", compressed size = " + entry.getCompressedSize()
			    + ", crc = " + entry.getCrc()
			    + ", method = " + entry.getMethod()
			    + ", extra = " + entry.getExtra()
			    + ", comment = " + entry.getComment()
			    + ", is directory = " + entry.isDirectory());

		// check exclusion map
		if (inclusion == null || inclusion.contains(entryName)) {

		    File file = new File(outputPath, entryName);
		    if (entry.isDirectory()) {
			if (!file.exists()) {
			    if (!file.mkdirs()) {
				logger.warning("Unzip error. cannot create directory named "
					       + entryName);
			    }
			}
			continue;
		    } else {
			File parent = file.getParentFile();
			if (!parent.exists()) {
			    if (!parent.mkdirs()) {
				logger.warning("Unzip error. cannot create directory - "
					       + parent.getAbsolutePath());
			    }
			}
		    }

		    int result = 0, nWrote = 0;
		    FileOutputStream fout = null;
		    try {
			fout = new FileOutputStream(file);

			while ((result = zis.read(buffer, 0, BUFFER_SIZE)) > 0) {
			    fout.write(buffer, 0, result);
			    nWrote += result;
			}

			logger.finest("wrote " + nWrote + " bytes");
		    } finally {
			if (fout != null) {
			    fout.close();
			}
		    }
		}
	    }
	} finally {
	    if (zis != null) {
		zis.close();
	    }
	}
    }

    /**
     * get input stream from given zip file
     */
    public static InputStream getZipFileInputStream(ZipFile zipFile, String entryName)
		throws IOException {
	ZipEntry ze = zipFile.getEntry(entryName);
	if (ze == null) { // no such entry
	    throw new FileNotFoundException("no such entry - " + entryName);
	}

	return zipFile.getInputStream(ze);
    }

    /**
     * zip given files
     *
     * @param zipFile zip file path to build
     * @param srcPath file or directory which will be zipped
     * @param compressLevel 0 to 9 (0 means no compression)
     * @param recursive if zip directories recursively
     * @exception IOException
     */
    public static void zipFiles(String zipFile, String srcPath,
				int compressLevel, boolean recursive)
	throws IOException {
	File srcFile = new File(srcPath);

	try {
	    ZipOutputStream zos = new
		ZipOutputStream(new BufferedOutputStream(
				    new FileOutputStream(zipFile), BUFFER_SIZE));
	    /* set compress level */
	    zos.setLevel(compressLevel);

	    includeFiles(zos, srcFile, "", recursive);
	    zos.close();
	} catch (IOException e) {
	    logger.warning("zipFiles(zipFile = " + zipFile + ", srcPath = " + srcPath
			   + ", compress = " + compressLevel + ", recursive = " + recursive
			   + ") io exception - " + e.getMessage());
	    // try to delete destination file
	    new File(zipFile).delete();

	    throw e;
	}
    }

    /**
     * embed given file into the zip stream
     */
    private static void includeFiles(ZipOutputStream zos, File file,
				     String path, boolean recursive)
		throws IOException {
	byte[] buffer = new byte[BUFFER_SIZE];
	String[] files = file.list();

	for (int i = 0; i < files.length; i++) {
	    String entryName = (path.length() > 0 
				? path.replace('\\', '/') + '/' + files[i]
				: files[i]);
	    File newFile = new File(file, files[i]);

	    if (newFile.isFile()) {
		ZipEntry ze = new ZipEntry(entryName);
		ze.setTime(newFile.lastModified());
		ze.setSize(newFile.length());
		zos.putNextEntry(ze);
		writeFileEntry(zos, newFile);
	    } else if (newFile.isDirectory()) { // recursive call
		ZipEntry ze = new ZipEntry(entryName + '/');
		ze.setTime(newFile.lastModified());
		zos.putNextEntry(ze);
		zos.closeEntry();
		if (recursive) {
		    includeFiles(zos, newFile, entryName, recursive);
		}
	    } else { // Huh? neither file nor directory?
		logger.warning("includeFiles() : ignores unknown file type - "
			       + newFile.getAbsolutePath());
	    }
	}
    }

    /**
     * read given file and write to the zip stream
     */
    private static void writeFileEntry(ZipOutputStream zos, File file) throws IOException {
	BufferedInputStream in = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);

	byte[] bytes = new byte[BUFFER_SIZE];
	int nRead = 0;
	while ((nRead = in.read(bytes, 0, bytes.length)) > 0) {
	    zos.write(bytes, 0, nRead);
	}

	in.close();
    }

}
