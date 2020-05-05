/*
 * $Id$
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
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.jar.*;
import java.util.logging.*; // 로그 사용

/**
 * 특정 디렉토리에 있는 Jar 혹은 Zip 파일들로부터 정의된 클래스를 적재하는 클래스 로더
 * Jar나 Zip 파일이 없을 경우 해당 디렉토리를 클래스 경로로 인식한다
 *     
 * @version  $Revision$<br>
 *           created at 2003-10-30 20:13:39
 * @author   Yoon Kyung Koo
 */

public class DirectoryClassLoader extends URLClassLoader {
    private static Logger logger = Logger.getLogger(DirectoryClassLoader.class.getName());

    private static HashMap loaderMap = new HashMap();

    /**
     * @param urls jar, zip 파일들의 URL 표현
     * @param parent 부모 클래스 로더
     */
    private DirectoryClassLoader(URL[] urls, ClassLoader parent) {
	super(urls, parent);
    }

    /**
     * @param directory 적재할 jar 혹은 zip 파일들이 위치한 디렉토리
     */
    public static DirectoryClassLoader getClassLoader(File directory) throws IOException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("getClassLoader(directory - " + directory.getAbsolutePath() + ")");
	}

	String path = directory.getCanonicalPath();
	DirectoryClassLoader loader = (DirectoryClassLoader) loaderMap.get(path);
	if (loader == null) {
	    if (!directory.isDirectory()) {
		throw new IOException("Given path is not a directory - " + path);
	    }

	    File[] files = directory.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
			if (name == null || name.length() == 0) {
			    return false;
			}

			String lowered = name.toLowerCase();
			if (lowered.endsWith(".zip")
			    || lowered.endsWith(".jar")) {
			    return true;
			}

			return false;
		    }
		});

	    URL[] urls = null;
	    if (files.length == 0) {
		urls = new URL[1];
		urls[0] = directory.toURL();

		// directory url should ends with '/'
		// if not the URLClassLoader will take it as a zip archive
		if (logger.isLoggable(Level.FINEST)) {
		    logger.finest("directory url - " + urls[0]);
		}
	    } else {
		urls = new URL[files.length];
		for (int i = 0; i < files.length; i++) {
		    urls[i] = files[i].toURL();
		}

		if (logger.isLoggable(Level.FINEST)) {
		    logger.finest("zip urls - " + getArrayContents(urls));
		}
	    }

	    // create class loader instance
	    loader = new DirectoryClassLoader(urls, DirectoryClassLoader.class.getClassLoader());
	    // cache it
	    loaderMap.put(path, loader);
	}

	return loader;
    }

    /**
     * get array contents as string.
     * used for debugging purpose
     */
    private static String getArrayContents(Object[] array) {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[");
	for (int i = 0; i < array.length; i++) {
	    buffer.append(array[i].toString()).append(",");
	}
	buffer.setLength(buffer.length() - 1);
	buffer.append("]");

	return buffer.toString();
    }

}
