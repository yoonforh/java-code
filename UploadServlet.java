/*
 * UploadServlet.java
 *
 * Copyright (c) 1998-2002 Yoon Kyung Koo(yoonforh at gmail dot com).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL YOON KYUNG KOO OR THE OTHER
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * HISTORY
 *  first release ver 1.0 1998/07/14
 *		first working version
 *  revision 1.0a 1998/11/06
 * 		change Vector with Hashtable to enhance performance
 *  revision 1.0z 1999/03/06
 *		add work around of a bug
 *			in JSDK 2.0's javax.servlet.ServletInputStream.readLine() method
 *  revision 1.01 2001/11/19
 *		fix a bug, instance variables would be shared
 *		by all the servlet calls, so should be avoided
 *			thanks to IsmaelCRamos at aol dot com
 *  revision 1.1 2002/02/23
 *      add multiple file support
 *      and handle characters escaped into integers (support filenames in unicode)
 *  revision 1.1a 2002/02/26
 *      fix some performance issues
 *  revision 1.1b 2002/06/11
 *      added open source license terms 
 *  revision 1.1c 2004/01/10
 *      fix textarea problem
 *      re-indented according to the Java Coding Convention rule (Tab : 8sp, Indent : 4sp)
 *  revision 1.1d 2004/09/21
 *      support multiple values
 *  revision 1.1e 2004/10/03
 *      fix ClassCastException. thanks to Ken.Akune at tecnomen dot com
 */


package yoonforh.upload;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

// import org.apache.log4j.Category; // if you use Log4j

/**
 * a servlet handles upload request.<br>
 * refer to  http://www.rfc.net/rfc1867.html
 *
 * @version  Revision: 1.1
 * @author   Yoon Kyung Koo
 */

public class UploadServlet extends HttpServlet {
    // 	private Category logger = null;
    //	private final static String UPLOAD_PATH = "c:\\upload";
    private final static int BUFFER_SIZE = 8192;
    private final static String PATH_KEY = "path";

    public void init() {
	// 		logger = Category.getInstance("yoonforh.upload");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
        doPost( req, res );
    }

    public void doPost (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	long start = System.currentTimeMillis();

	String fileLocation = null;
	int contentLength = req.getContentLength();

	// RFC 1867 
	String contentType = req.getContentType();

	res.setContentType("text/html;charset=UTF8");
	// 		// res.addHeader("Expires","Mon, 26 Jul 1997 05:00:00 GMT");
	//   		res.addDateHeader("Last-Modified", new java.util.Date().getTime());
	//   		res.addHeader("Cache-Control","no-cache, must-revalidate");
	//   		res.addHeader("Pragma","no-cache");

	PrintWriter out = res.getWriter();

	if (contentType == null) {
	    System.out.println("content type is null");
	    return;
	}

	int ind = contentType.indexOf("boundary=");
	if (ind == -1) {
	    System.out.println("IND is less than 0");
	    // 			logger.info("Upload : IND is less than 0");
	    return;
	}
	String boundary = contentType.substring(ind+9);

	if (boundary == null) {
	    System.out.println("boundary is null");
	    // 			logger.info("Upload : boundary is null");
	    return;
	}

	String boundaryString = "--" + boundary;
	ServletInputStream in = req.getInputStream();
	byte[] buffer = new byte[BUFFER_SIZE];
	HashMap map = new HashMap();
	int result = in.readLine(buffer, 0, BUFFER_SIZE);

	if (false) { // dump out the input stream for debugging
	    while (result > 0) {
		String line = new String(buffer, 0, result);
		System.out.println("LINE: " + line);
		result = in.readLine(buffer, 0, BUFFER_SIZE);
	    }
	    return;
	}

	outer:
	while (true) {
	    if (result <= 0) {
		System.out.println("Error. Stream truncated. 0");
		// 				logger.info("Upload : stream truncated error.");
		break;
	    }

	    String line = new String(buffer, 0, result);
	    // System.out.println("LINE: " + line);
	
	    if (!line.startsWith(boundaryString)) {
		System.out.println("Error. multipart boundary missing.");
		// 				logger.info("Upload : error. multipart boundary missing.");
		break;
	    }

	    // boundary end tag
	    if (line.substring(boundaryString.length()).startsWith("--")) {
		// 				System.out.println("End of multipart");
		break;
	    }

	    result = in.readLine(buffer, 0, BUFFER_SIZE);
	    if (result <= 0) {
		System.out.println("Upload : may be end boundary which has no contents");
		break;
	    }

	    line = new String(buffer, 0, result);
	    // 			System.out.println("content disposition line = " + line);
	    StringTokenizer tokenizer=new StringTokenizer(line, ";\r\n");
	    String token=tokenizer.nextToken();
	    String upperToken = token.toUpperCase();
	    if (!upperToken.startsWith("CONTENT-DISPOSITION")) {
		System.out.println("Format error. Content-Disposition expected.");
		break;
	    }
	    String disposition = upperToken.substring(21);
	    if (!disposition.equals("FORM-DATA")) {
		System.out.println("Sorry, I don't know how to handle ["
				   + disposition + "] disposition.");
		break;
	    }
	    if (tokenizer.hasMoreElements()) {
		token=tokenizer.nextToken();
	    } else {
		System.out.println("Format error. NAME expected.");
		break;
	    }
	    int nameStart=token.indexOf("name=\"");
	    int nameEnd=token.indexOf("\"", nameStart+7);
	    if (nameStart<0 || nameEnd<0) {
		System.out.println("Format error. NAME expected.");
		break;
	    }
	    String name=token.substring(nameStart+6, nameEnd);

	    if (tokenizer.hasMoreElements()) {
		String filename = null;
		int fnStart, fnEnd;
		File file = null;
		String fileContentType = null;
		FileOutputStream fout = null;
		int size = 0;

		fnStart = line.indexOf("filename=\"");
		if (fnStart < 0) { // filename term missing
		    System.out.println("NO FILENAME given.");
		    result = in.readLine(buffer, 0, BUFFER_SIZE);
		    continue;
		}

		fnEnd = line.indexOf("\"", fnStart + 11);
		if (fnEnd < 0) {
		    System.out.println("FILENAME is null.");
		}  else {
		    filename = line.substring(fnStart+10, fnEnd);
		    int lastindex = -1;
		    if ((lastindex = filename.lastIndexOf('/')) < 0) {
			lastindex = filename.lastIndexOf('\\');
		    }
		    if (lastindex >= 0) {
			filename = filename.substring(lastindex+1);
		    }
		    filename = processEscape(filename);
		}

		// 				System.out.println("receiving file named " + filename);

		if (filename != null) {
		    String path = /* UPLOAD_PATH + */ getValue(map, PATH_KEY);

		    file = new File(path);
		    if (path != null && file.exists() && file.isDirectory()) {
			file = new File(path, filename);
		    }
		}

		result = in.readLine(buffer, 0, BUFFER_SIZE);
		if (result <= 0) {
		    System.out.println("Error. Stream truncated. 1");
		    break;
		} 
		fileContentType = new String(buffer, 0, result);
		if (fileContentType.toUpperCase().startsWith("CONTENT-TYPE:")) {
		    fileContentType = fileContentType.substring(13).trim();
		} else {
		    System.out.println("what should I read here ??? - result = " + result
				       + ", and read [" + new String(buffer, 0, result)
				       + "]");
		}

		try {
		    byte[] tmpbuffer1 = buffer;
		    byte[] tmpbuffer2 = new byte[BUFFER_SIZE];
		    byte[] tmpbuffer = tmpbuffer2;
		    int tmpbufferlen = 0;
		    boolean isFirst = true;
		    boolean odd = true;
		inner:
		    while ((result = in.readLine(buffer, 0, BUFFER_SIZE)) > 0) {
			if (isFirst) { // ignore all proceeding \r\n
			    if (result==2 && buffer[0]=='\r' && buffer[1]== '\n') {
				continue;
			    }

			    if (file != null) {
				fout = new FileOutputStream(file);
			    }
			}

			if (bytesStartsWith(buffer, 0, result, boundaryString)) {
			    if (!isFirst) {
				size += tmpbufferlen - 2;
				if (fout != null) {
				    fout.write(tmpbuffer, 0, tmpbufferlen - 2);
				}
			    }
			    continue outer;
			} else {
			    if (!isFirst) {
				size += tmpbufferlen;
				if (fout != null) {
				    fout.write(tmpbuffer, 0, tmpbufferlen);
				}
			    }
			}

			if (odd) {
			    buffer = tmpbuffer2;
			    tmpbuffer = tmpbuffer1;
			} else {
			    buffer = tmpbuffer1;
			    tmpbuffer = tmpbuffer2;
			}
			odd = !odd;

			tmpbufferlen = result;
			isFirst = false;
		    }
		} catch (IOException ie) {
		    System.out.println("IO Error while write to file : " + ie.toString());
		} finally {
		    System.out.println("Upload : size = " + size);
		    if (fout != null) {
			fout.close();
		    }
		    if (size > 0) {
			appendValue(map, name, filename, fileContentType, size);
		    }
		}
		result = in.readLine(buffer, 0, BUFFER_SIZE);
		System.out.println("what should I read here? - result = " + result
				   + ", and read [" + new String(buffer, 0, result)
				   + "]");
	    } else { // no more elements
		result = in.readLine(buffer, 0, BUFFER_SIZE);
		if (result <= 0) {
		    System.out.println("Error. Stream truncated. 2");
		    break;
		}

		StringBuffer valueBuffer = new StringBuffer();
		while (true) {
		    result = in.readLine(buffer, 0, BUFFER_SIZE);
		    if (result <= 0) {
			System.out.println("Error. Stream truncated. 3");
			break outer;
		    }

		    if (bytesStartsWith(buffer, 0, result, boundaryString)) {
			break;
		    }
		    valueBuffer.append(new String(buffer, 0, result));
		}
		valueBuffer.setLength(valueBuffer.length() - 2); // exclude last \r\n
		appendValue(map, name, valueBuffer.toString());
		continue;
	    }

	    result = in.readLine(buffer, 0, BUFFER_SIZE);
	} // end of while

	long end = System.currentTimeMillis();
	System.out.println("Good! it took " + (end - start) + " (ms)");

	printResult(out, map);
	out.close();
    }

    boolean bytesStartsWith(byte[] bytes, int offset, int length, String toCompare) {
	boolean result = true;
	if (toCompare.length() > length) {
	    return false;
	}

	for (int i = toCompare.length() - 1; i >= 0; i--) {
	    if (toCompare.charAt(i) != bytes[offset + i]) {
		result = false;
		break;
	    }
	}

	return result;
    }

    void appendValue(HashMap map, String name, String value, String contentType, int size) {
    	UploadData data = new UploadData(name, value, contentType, size, true);

	ArrayList list = (ArrayList) map.get(name);
	if (list == null) {
	    list = new ArrayList();
	}

	list.add(data);
	map.put(name, list);
    }

    void appendValue(HashMap map, String name, String value) {
    	UploadData data = new UploadData(name, value, null, 0, false);

	ArrayList list = (ArrayList) map.get(name);
	if (list == null) {
	    list = new ArrayList();
	}

	list.add(data);
    	map.put(name, list);
    }

    String[] getValues(HashMap map, String name) {
    	ArrayList list = (ArrayList) map.get(name);
    	if (list == null) {
	    return null;
	}

	String[] values = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    values[i] = ((UploadData) list.get(i)).value;
	}

    	return values;
    }

    String getValue(HashMap map, String name) {
	String[] values = getValues(map, name);
	if (values == null || values.length == 0) {
	    return null;
	}

	return values[0];
    }

    final static int NORMAL = 0;
    final static int AMPERSAND = 1;
    final static int AMPERSHARP = 2;
    /**
     * process html escape characters (&#NNNN;)
     */
    String processEscape(String string) {
	StringBuffer buffer = new StringBuffer(string.length());
	char[] chars = string.toCharArray();
	StringBuffer escaped = new StringBuffer(6);
	int status = NORMAL;

	for (int i = 0; i < string.length(); i++) {
	    switch (status) {
	    case NORMAL :
		if (chars[i] == '&') {
		    status = AMPERSAND;
		} else {
		    buffer.append(chars[i]);
		}
		break;

	    case AMPERSAND :
		if (chars[i] == '#') {
		    status = AMPERSHARP;
		} else {
		    status = NORMAL;
		    buffer.append('&');
		}
		break;

	    case AMPERSHARP :
		if (chars[i] == ';') {
		    try {
			buffer.append((char) Integer.parseInt(escaped.toString()));
		    } catch (NumberFormatException nfe) {
			// I don't handle other Entities
			buffer.append(escaped);
			buffer.append(';');
		    }
		    escaped.setLength(0);
		    status = NORMAL;
		} else {
		    escaped.append(chars[i]);
		}
		break;
	    }
	}

	if (escaped.length() > 0) {
	    buffer.append(escaped);
	}

	return buffer.toString();
    }

    void printResult(PrintWriter out, Map map) throws IOException {
    	Iterator itr = map.values().iterator();

    	out.println("<HTML><HEAD>");
    	out.println("<TITLE>Upload Result</TITLE>");
    	out.println("</HEAD><BODY>"); 
    	out.println("<H1>Upload Result</H1>");
    	out.println("<TABLE>");
    	out.println("<TR><TH>NAME</TH><TH>VALUE</TH><TH>CONTENT TYPE</TH><TH>SIZE</TH><TH>FILE</TH></TR>");

	while (itr.hasNext()) {
	    ArrayList list = (ArrayList) itr.next();
	    for (int i = 0; i < list.size(); i++) {
		UploadData data = (UploadData) list.get(i);
		out.println("<TR>");
		out.println("<TD>" + (data.getName() == null ? "" : data.getName()) + "</TD>");
		out.println("<TD>" + (data.getValue() == null ? "" : data.getValue()) + "</TD>");
		out.println("<TD>" + (data.getContentType() == null ? "" : data.getContentType()) + "</TD>");
		out.println("<TD>" + (data.isFile() ? String.valueOf(data.getSize()) : "") + "</TD>");
		out.println("<TD>" + (data.isFile() ? "file" : "") + "</TD>");
		out.println("</TR>");
	    }
	}

    	out.println("</TABLE>");
    	out.println("</BODY></HTML>");
    }

    public String getServletInfo() {
	return "A servlet that uploads files";
    }

}

class UploadData {
    String name;
    String value;
    String contentType;
    int size;
    boolean isFile;

    UploadData(String name, String value, String contentType, int size, boolean isFile) {
	this.name = name;
	this.value = value;
	this.contentType = contentType;
	this.size = size;
	this.isFile = isFile;
    }

    public String getName() {
	return name;
    }

    public String getValue() {
	return value;
    }

    public String getContentType() {
	return contentType;
    }

    public int getSize() {
	return size;
    }

    public boolean isFile() {
	return isFile;
    }
}
