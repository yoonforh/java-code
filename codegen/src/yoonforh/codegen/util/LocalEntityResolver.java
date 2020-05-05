/*
 * $Id: LocalEntityResolver.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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

import java.io.InputStream;
import java.util.logging.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * SAX entity resolver for local dtds
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-25 17:32:24
 * @author   Yoon Kyung Koo
 */

public class LocalEntityResolver implements EntityResolver {
    private static Logger logger
	= Logger.getLogger(LocalEntityResolver.class.getName());

    /*
     * template dtd
     */
    public static final String TEMPLATE_PUBLIC_ID
	= "-//Yoon Kyung Koo.//DTD Template Information 1.0//EN";
    public static final String TEMPLATE_SYSTEM_ID
	= "http://java.freehosting.co.kr/dtd/template_info_1_0.dtd";
    private static final String TEMPLATE_DTD_LOCATION
	= "yoonforh/codegen/dtd/template_info.dtd";

    /*
     * template input sheet dtd
     */
    public static final String INPUT_SHEET_PUBLIC_ID
	= "-//Yoon Kyung Koo.//DTD Template Input Sheet 1.0//EN";
    public static final String INPUT_SHEET_SYSTEM_ID
	= "http://java.freehosting.co.kr/dtd/template_input_1_0.dtd";
    private static final String INPUT_SHEET_DTD_LOCATION
	= "yoonforh/codegen/dtd/template_input.dtd";

    /*
     * database table info dtd
     */
    public static final String TABLE_INFO_PUBLIC_ID
	= "-//Yoon Kyung Koo.//DTD Database Table Information 1.0//EN";
    public static final String TABLE_INFO_SYSTEM_ID
	= "http://java.freehosting.co.kr/dtd/db_table_info_1_0.dtd";
    private static final String TABLE_INFO_DTD_LOCATION
	= "yoonforh/codegen/dtd/db_table_info.dtd";

    public InputSource resolveEntity (String publicId, String systemId) {
	logger.entering("LocalEntityResolver", "resolveEntity(publicId - "
			+ publicId + ", systemId - " + systemId + ")");

	if (TEMPLATE_SYSTEM_ID.equals(systemId)
	    || TEMPLATE_PUBLIC_ID.equals(publicId)) {
	    // return a special input source
	    return getLocalInputSource(TEMPLATE_DTD_LOCATION);
	} else if (INPUT_SHEET_SYSTEM_ID.equals(systemId)
	    || INPUT_SHEET_PUBLIC_ID.equals(publicId)) {
	    // return a special input source
	    return getLocalInputSource(INPUT_SHEET_DTD_LOCATION);
	} else if (TABLE_INFO_SYSTEM_ID.equals(systemId)
	    || TABLE_INFO_PUBLIC_ID.equals(publicId)) {
	    // return a special input source
	    return getLocalInputSource(TABLE_INFO_DTD_LOCATION);
	} else {
	    // use the default behaviour
	    return null;
	}
    }

    private InputSource getLocalInputSource(String location) {
	ClassLoader loader = getClass().getClassLoader();

	InputStream in = null;
	if (loader == null) {
	    in = ClassLoader.getSystemResourceAsStream(location);
	} else {
	    in = loader.getResourceAsStream(location);
	}

	return new InputSource(in);
    }
}
