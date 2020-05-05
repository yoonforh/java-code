/*
 * $Id: DBCodeHandler.java,v 1.3 2003/05/31 07:19:25 yoonforh Exp $
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


package yoonforh.codegen.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.*;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.util.CSVUtil;
import yoonforh.codegen.util.sql.SQLTypeManager;
import yoonforh.codegen.util.sql.OracleParserManager;
import yoonforh.codegen.processor.CodeBlockProcessor;
import yoonforh.codegen.parser.OracleCreateTableParser.TableInfo;
import yoonforh.codegen.parser.OracleCreateTableParser.FieldType;
import yoonforh.codegen.parser.ParseException;

/**
 * database table/field - type handler for EJB/JDBC codes
 *
 * @version  $Revision: 1.3 $<br>
 *           created at 2003-04-07 17:55:30
 * @author   Yoon Kyung Koo
 */

public class DBCodeHandler implements Handler {
    private static Logger logger
	= Logger.getLogger(DBCodeHandler.class.getName());


    /**
     * input type
     */
    public static final String INPUT_TYPE = "input-type";

    public static final String INPUT_TYPE_CSV = "csv";
    public static final String INPUT_TYPE_XML_SHEET = "xml-sheet";
    public static final String INPUT_TYPE_SQL_SHEET = "sql-sheet";

    /**
     * code type
     */
    public static final String CODE_TYPE = "code-type";

    public static final String CODE_TYPE_ENTITY_TO_BEAN = "entity2bean";
//     public static final String CODE_TYPE_RS_TO_BEAN = "rs2bean";
//     public static final String CODE_TYPE_BEAN_TO_ENTITY = "bean2entity";
    public static final String CODE_TYPE_CODE_BLOCK = "code-block";

    /**
     * iteration type
     */
    public static final String ITERATION_TYPE = "iteration-type";

    public static final String ITERATION_TYPE_NONE = "none";
    public static final String ITERATION_TYPE_TABLE = "table";
    public static final String ITERATION_TYPE_FIELD = "field";
    public static final String ITERATION_TYPE_PK_FIELD = "pk-field";
    /** iteration suffix will not be appended to last item */
    public static final String ITERATION_SUFFIX = "iteration-suffix";
    /** iteration prefix will not be prepended to first item */
    public static final String ITERATION_PREFIX = "iteration-prefix";
    /** iteration index variable will be put to variable map by default
	when one of the iteration types is used */
    public static final String KW_ITERATION_INDEX = "iteration-index";

    /*
     * csv input type
     */
    /** key to csv type global var which contains db table list */
    public static final String TABLE_NAMES_KEY = "table-names-key";
    /** key to csv type global var which contains db field list */
    public static final String FIELD_NAMES_KEY = "field-names-key";
    /** key to csv type global var which contains db type list */
    public static final String FIELD_TYPES_KEY = "field-types-key";
    /** key to csv type global var which contains db type size list */
    public static final String FIELD_SIZES_KEY = "field-sizes-key";
    /** key to csv type global var which contains db primary key fields */
    public static final String KEY_FIELDS_KEY = "key-fields-key";

    /** key to global var which contains table id */
    public static final String TABLE_ID_KEY = "table-id-key";

    /*
     * xml sheet input type (currently, not implemented)
     */
    public static final String XML_SHEET_PATH_KEY = "xml-sheet-path-key";

    /*
     * sql sheet input type
     */
    public static final String SQL_SHEET_PATH_KEY = "sql-sheet-path-key";

    /*
     * variables in codes
     */
    /** EJB entity bean local variable name */
    public static final String KW_ENTITY_NAME = "entity-name";
    /** JavaBeans local variable name */
    public static final String KW_BEAN_NAME = "bean-name";
    /** ResultSet local variable name */
    public static final String KW_RS_NAME = "rs-name";
    /** indentation - still not implemented */
    public static final String KW_INDENT = "indent";

    /*
     * when code type is code block
     */
    /** code block if necessary */
    public static final String CODE_BLOCK = "code-block";
    /** alternative java type code block prefix */
    public static final String CODE_BLOCK_ALT_PREFIX = "code-block-";
    /** when alternative java type is primitive */
    public static final String CODE_BLOCK_PRIMITIVE_TYPE_SUFFIX = "primitive";

    /*
     * code block processing instructions
     */
    // code block keywords.
    // you can use three code variables above also in code block
    public static final String KW_TABLE_NAME = "table-name";
    public static final String KW_FIELD_NAME = "field-name";
    public static final String KW_PK_FIELD_NAME = "pk-field-name";
    public static final String KW_FIELD_SQL_TYPE = "field-sql-type";
    public static final String KW_FIELD_JAVA_TYPE = "field-java-type";
    // fully qualified distinguished java class name
    public static final String KW_FIELD_JAVA_FQDN = "field-java-fqdn";

    // supported code block case processing instructions are
    // csu (no effect), csl (decapitalize), upper, lower, ...
    // See TextProcessor.processCase() method for detail

    /**
     * some operations on the given text using the variable map information.
     * paramMap are specified in template.xml and globalMap are specified by user inputs.
     * Expected supported types are as belows :
     *
     * INPUT_TYPE_CSV
     *  input param 1. field-names-key (key to csv type global var)
     *  input param 2. field-types-key (key to csv type global var)
     *  input param 3. code-type (specify supported code type)
     *  input param 4. code block including variables (specify code block)
     *
     * INPUT_TYPE_XML_SHEET
     *  input param 1. xml-sheet-path-key (key to xml file path global var which defines field-names and field-types)
     *  input param 2. code-type (specify supported code type)
     *  input param 3. code block including variables (specify code block)
     *  input param 4. table-id-key (specify the table)
     *
     * INPUT_TYPE_SQL_SHEET
     *  input param 1. sql-sheet-path-key (key to sql file path global var which declares tables)
     *  input param 2. code-type (specify supported code type)
     *  input param 3. code block including variables (specify code block)
     *  input param 4. table-id-key (specify the table)
     *
     * @param text main text
     * @param paramMap parameters map
     * @param globalMap identified variables map
     */
    public String handle(String text, HashMap paramMap, HashMap globalMap)
		throws ApplicationException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("handle(text - " + text + ", paramMap - " + paramMap
			  + ", globalMap - " + globalMap);
	}

	String result = null;

	// 1. first get variable keys from paramMap
	// 2. second get values from globalMap including some input file path
	// 3. handle the text
	String inputType = (String) paramMap.get(INPUT_TYPE);
	if (inputType == null || inputType.length() == 0) {
	    logger.severe("required input type omitted");
	    return text;
	}

	if (INPUT_TYPE_CSV.equals(inputType)) {
	    return handleCSVType(text, paramMap, globalMap);
	} else if (INPUT_TYPE_SQL_SHEET.equals(inputType)) {
	    return handleSQLSheetType(text, paramMap, globalMap);
	}

	// currently we support csv and sql sheet only
	logger.severe("unsupported input type - " + inputType);
	return text;
    }

    /**
     * handle CSV inputs
     */
    protected String handleCSVType(String text, HashMap paramMap,
				   HashMap globalMap)
	throws ApplicationException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("handleCSVType(text - " + text
			  + ", paramMap - " + paramMap
			  + ", globalMap - " + globalMap);
	}

	String iterationType = (String) paramMap.get(ITERATION_TYPE);
	if (iterationType == null || iterationType.length() == 0) {
	    logger.severe("required iteration type omitted");
	    return text;
	}

	if (ITERATION_TYPE_TABLE.equals(iterationType)) {
	    String tableNamesKey = (String) paramMap.get(TABLE_NAMES_KEY);

	    if (tableNamesKey == null || tableNamesKey.length() == 0) {
		logger.severe("required table names key omitted");
		return text;
	    }

	    // get table name array
	    String[] tableNames = CSVUtil.split((String) globalMap.get(tableNamesKey));

	    return processTableCodeType(text,
				   tableNames,
				   paramMap, globalMap);
	} else if (ITERATION_TYPE_FIELD.equals(iterationType)
		   || ITERATION_TYPE_PK_FIELD.equals(iterationType)) {
	    String fieldNamesKey = (String) paramMap.get(FIELD_NAMES_KEY);
	    String fieldTypesKey = (String) paramMap.get(FIELD_TYPES_KEY);
	    String fieldSizesKey = (String) paramMap.get(FIELD_SIZES_KEY);
	    String keyFieldsKey = (String) paramMap.get(KEY_FIELDS_KEY);

	    if (fieldNamesKey == null || fieldNamesKey.length() == 0) {
		logger.severe("required field names key omitted");
		return text;
	    }

	    if (fieldTypesKey == null || fieldTypesKey.length() == 0) {
		logger.severe("required field types key omitted");
		return text;
	    }

	    // get field name array
	    String[] fieldNames = CSVUtil.split((String) globalMap.get(fieldNamesKey));
	    // get field sql type array
	    String[] fieldTypes = CSVUtil.split((String) globalMap.get(fieldTypesKey));
	    // get field sql type size array
	    String[] fieldSizes = null;
	    // get primary key field name array
	    String[] pkFields = null;

	    if (fieldSizesKey == null || fieldSizesKey.length() == 0) {
		if (logger.isLoggable(Level.FINEST)) {
		    logger.finest("field sizes key not given");
		}
	    } else {
		fieldSizes = CSVUtil.split((String) globalMap.get(fieldSizesKey));
	    }

	    if (keyFieldsKey == null || keyFieldsKey.length() == 0) {
		if (ITERATION_TYPE_PK_FIELD.equals(iterationType)) {
		    logger.severe("primary key fields key not given");
		    return text;
		} else {
		    if (logger.isLoggable(Level.FINEST)) {
			logger.finest("primary key fields key not given");
		    }
		}
	    } else {
		pkFields = CSVUtil.split((String) globalMap.get(keyFieldsKey));
	    }

	    HashMap fieldMap = new HashMap();
	    for (int i = 0; i < fieldNames.length; i++) {
		FieldType type = null;
		if (fieldSizes != null && fieldSizes.length > 0) {
		    type = new FieldType(fieldTypes[i], fieldSizes[i]);
		} else {
		    type = new FieldType(fieldTypes[i]);
		}

		fieldMap.put(fieldNames[i], type);
	    }

	    return processFieldCodeType(text, iterationType,
					fieldMap, pkFields,
					paramMap, globalMap);
	}

	// huh?
	return text;
    }

    private final static String[] STRING_ARRAY_TYPE = new String[0];

    /**
     * handle sql sheet input
     */
    protected String handleSQLSheetType(String text, HashMap paramMap,
					HashMap globalMap)
	throws ApplicationException {
	if (logger.isLoggable(Level.FINEST)) {
	    logger.finest("handleSQLSheetType(text - " + text
			  + ", paramMap - " + paramMap
			  + ", globalMap - " + globalMap);
	}

	String sqlPathKey = (String) paramMap.get(SQL_SHEET_PATH_KEY);

	if (sqlPathKey == null || sqlPathKey.length() == 0) {
	    logger.severe("required sql path key omitted");
	    return text;
	}

	String sqlPath = (String) globalMap.get(sqlPathKey);
	if (sqlPath == null || sqlPath.length() == 0) {
	    logger.severe("cannot get required sql path from global key - " + sqlPathKey);
	    return text;
	}

	String iterationType = (String) paramMap.get(ITERATION_TYPE);
	if (iterationType == null || iterationType.length() == 0) {
	    logger.severe("required iteration type omitted");
	    return text;
	}

	HashMap tableMap = null;
	try {
	    tableMap = OracleParserManager.getInstance().parseSQL(sqlPath);
	} catch (java.io.IOException e) {
	    logger.log(Level.SEVERE, "sql sheet io error", e);
	    throw new ApplicationException(e);
	} catch (ParseException e) {
	    logger.log(Level.SEVERE, "sql sheet parse error", e);
	    throw new ApplicationException(e);
	}

	if (ITERATION_TYPE_TABLE.equals(iterationType)) {
	    // get table name array
	    String[] tableNames = (String[]) tableMap.keySet().toArray(STRING_ARRAY_TYPE);

	    return processTableCodeType(text,
					tableNames,
					paramMap, globalMap);
	} else if (ITERATION_TYPE_FIELD.equals(iterationType)
		   || ITERATION_TYPE_PK_FIELD.equals(iterationType)) {
	    // get primary key field name array
	    String[] pkFields = null;

	    String tableIDKey = (String) paramMap.get(TABLE_ID_KEY);

	    if (tableIDKey == null || tableIDKey.length() == 0) {
		logger.severe("required table id key omitted");
		return text;
	    }

	    String tableID = (String) globalMap.get(tableIDKey);

	    // get fields map
	    TableInfo tableInfo = (TableInfo) tableMap.get(tableID.toLowerCase());
	    if (tableInfo == null) {
		logger.severe("cannot get the table info - " + tableID);
		throw new ApplicationException("no table info - " + tableID);
	    }

	    ArrayList keyList = tableInfo.getPKList();
	    if (keyList.size() > 0) {
		pkFields = (String[]) keyList.toArray(STRING_ARRAY_TYPE);
	    }

	    HashMap fieldMap = tableInfo.getFieldMap();

	    return processFieldCodeType(text, iterationType, fieldMap, pkFields,
					paramMap, globalMap);
	}

	// hmm...
	return text;
    }

    /**
     * handle specified code type of db table iteration<br>
     * currently, we support code-block only in table iteration mode
     *
     * @param paramMap handler info parameter map
     * @param globalMap global variables map
     */
    protected String processTableCodeType(String text,
				     String[] tableNames,
				     HashMap paramMap,
				     HashMap globalMap)
		throws ApplicationException {
	String result = null;
	String codeType = (String) paramMap.get(CODE_TYPE);

	if (codeType == null || codeType.length() == 0) {
	    logger.severe("required code type value omitted");
	    return text;
	}

	if (CODE_TYPE_CODE_BLOCK.equals(codeType)) {
	    String codeBlock = (String) paramMap.get(CODE_BLOCK);

	    if (codeBlock == null || codeBlock.length() == 0) {
		logger.severe("required code block value omitted");
		return text;
	    }

	    result = iterateTableCodeBlock(codeBlock, tableNames, paramMap, globalMap);
	}

	if (result == null) {
	    return text;
	}

	return result;
    }

    /**
     * handle specified code type<br>
     * currently, we support code-block and entity2bean only
     *
     * @param paramMap handler info parameter map
     * @param globalMap global variables map
     */
    protected String processFieldCodeType(String text,
					  String iterationType,
					  HashMap fieldMap,
					  String[] pkFields,
					  HashMap paramMap,
					  HashMap globalMap)
		throws ApplicationException {
	String result = null;
	String codeType = (String) paramMap.get(CODE_TYPE);

	if (codeType == null || codeType.length() == 0) {
	    logger.severe("required code type value omitted");
	    return text;
	}

	if (CODE_TYPE_ENTITY_TO_BEAN.equals(codeType)) {
	    String entityName = (String) paramMap.get(KW_ENTITY_NAME);
	    String beanName = (String) paramMap.get(KW_BEAN_NAME);
	    String rsName = (String) paramMap.get(KW_RS_NAME);
	    String indent = (String) paramMap.get(KW_INDENT);

	    result = iterateEntityToBean(fieldMap, pkFields,
					 entityName, beanName, paramMap, globalMap);
	} else if (CODE_TYPE_CODE_BLOCK.equals(codeType)) {
	    String codeBlock = (String) paramMap.get(CODE_BLOCK);

	    if (codeBlock == null || codeBlock.length() == 0) {
		logger.severe("required code block value omitted");
		return text;
	    }

	    result = iterateFieldCodeBlock(codeBlock, iterationType,
					   fieldMap, pkFields,
					   paramMap, globalMap);
	}

	if (result == null) {
	    return text;
	}

	return result;
    }

    private static final String ENTITY_TO_BEAN_DEFAULT_PATTERN
	= "${bean-name}.set${field-name:csu}(${entity-name}.get${field-name:csu}());\n";
    private static final String ENTITY_TO_BEAN_OF_DATE_PATTERN
	= "${bean-name}.set${field-name:csu}(kr.mil.add.util.DateUtil.getDateOnlyString(${entity-name}.get${field-name:csu}()));\n";

    public String iterateEntityToBean(HashMap fieldMap, String[] pkFields,
				      String entityName, String beanName,
				      HashMap paramMap, HashMap globalMap)
		throws ApplicationException {
	SQLTypeManager sqlManager = SQLTypeManager.getInstance(SQLTypeManager.ORACLE_TYPE);
	CodeBlockProcessor processor = CodeBlockProcessor.getInstance();
	StringBuffer buffer = new StringBuffer();
	HashMap map = (HashMap) globalMap.clone();
	map.put(KW_BEAN_NAME, beanName);
	map.put(KW_ENTITY_NAME, entityName);

	Iterator it = fieldMap.keySet().iterator();
	while (it.hasNext()) {
	    String fieldName = (String) it.next();
	    FieldType type = (FieldType) fieldMap.get(fieldName);
	    String typeName = type.getName();

	    map.put(KW_FIELD_NAME, fieldName);
	    map.put(KW_FIELD_JAVA_TYPE, sqlManager.getJavaTypeString(typeName));

	    Class clazz = sqlManager.getJavaType(typeName);
	    if (clazz == java.sql.Date.class) {
		processor.apply(buffer, ENTITY_TO_BEAN_OF_DATE_PATTERN, map);
	    } else {
		processor.apply(buffer, ENTITY_TO_BEAN_DEFAULT_PATTERN, map);
	    }
	}

	return buffer.toString();
    }

    /**
     * field iterated code block.<br>
     * this iteration supports alternative code blocks
     * that matches the corresponding java type of the fields.<br>
     * the default code block key is <strong>code-block</strong>,
     * while alternative <strong>code-block-primitive</strong>,
     * <strong>code-block-int</strong>, <strong>code-block-java.math.BigDecimal</strong>
     * keys can be declared at the same time.<br>
     * if multiple code blocks for the java type of given field are declared
     * more accurate java type alt code block will be used.<br>
     * for example, the java type is int and
     * code-block, code-block-primitive, code-block-int keys are declared,
     * then the code-block-int keyed value will be used.
     */
    public String iterateFieldCodeBlock(String codeBlock, String iterationType,
					HashMap fieldMap, String[] pkFields,
					HashMap paramMap, HashMap globalMap)
		throws ApplicationException {
	SQLTypeManager sqlManager = SQLTypeManager.getInstance(SQLTypeManager.ORACLE_TYPE);
	CodeBlockProcessor processor = CodeBlockProcessor.getInstance();
	StringBuffer buffer = new StringBuffer();
	HashMap map = (HashMap) globalMap.clone();
	String prefix = (String) paramMap.get(ITERATION_PREFIX);
	String suffix = (String) paramMap.get(ITERATION_SUFFIX);
	if (prefix == null) {
	    prefix = "";
	}
	if (suffix == null) {
	    suffix = "";
	}

	if (ITERATION_TYPE_FIELD.equals(iterationType)) {
	    Iterator it = fieldMap.keySet().iterator();

	    for (int i = 0; it.hasNext(); i++) {
		if (i != 0) {
		    buffer.append(prefix);
		}
		String fieldName = (String) it.next();
		FieldType type = (FieldType) fieldMap.get(fieldName);
		String typeName = type.getName();
		Class javaType = sqlManager.getJavaType(typeName);
		String currentCodeBlock = codeBlock;

		map.put(KW_ITERATION_INDEX, String.valueOf(i));
		map.put(KW_FIELD_NAME, fieldName);
		map.put(KW_FIELD_JAVA_TYPE, sqlManager.getJavaTypeString(typeName));
		map.put(KW_FIELD_SQL_TYPE, typeName);
		map.put(KW_FIELD_JAVA_FQDN, javaType.getName());

		/* first, check if alternative primitive type code block exists */
		if (javaType.isPrimitive()
		    && paramMap.containsKey(CODE_BLOCK_ALT_PREFIX
					    + CODE_BLOCK_PRIMITIVE_TYPE_SUFFIX)) {
		    currentCodeBlock = (String) paramMap.get(CODE_BLOCK_ALT_PREFIX
					     + CODE_BLOCK_PRIMITIVE_TYPE_SUFFIX);
		}

		/* second, check if alternative type code block exists */
		if (paramMap.containsKey(CODE_BLOCK_ALT_PREFIX
					 + javaType.getName())) {
		    currentCodeBlock = (String) paramMap.get(CODE_BLOCK_ALT_PREFIX
						      + javaType.getName());
		}

		processor.apply(buffer, currentCodeBlock, map);
		buffer.append(suffix);
	    }
	    buffer.setLength(buffer.length() - suffix.length());
	} else if (ITERATION_TYPE_PK_FIELD.equals(iterationType)) {
	    if (pkFields == null || pkFields.length == 0) {
		logger.severe("pk fields are not properly given where code block - ["
			      + codeBlock + "] and field map - " + fieldMap);
	    } else {
		for (int i = 0; i < pkFields.length; i++) {
		    if (i != 0) {
			buffer.append(prefix);
		    }
		    String fieldName = pkFields[i];
		    FieldType type = (FieldType) fieldMap.get(fieldName);
		    String typeName = type.getName();
		    Class javaType = sqlManager.getJavaType(typeName);
		    String currentCodeBlock = codeBlock;

		    map.put(KW_ITERATION_INDEX, String.valueOf(i));
		    map.put(KW_FIELD_NAME, fieldName);
		    map.put(KW_FIELD_JAVA_TYPE, sqlManager.getJavaTypeString(typeName));
		    map.put(KW_FIELD_SQL_TYPE, typeName);
		    map.put(KW_FIELD_JAVA_FQDN, javaType.getName());

		    /* first, check if alternative primitive type code block exists */
		    if (javaType.isPrimitive()
			&& paramMap.containsKey(CODE_BLOCK_ALT_PREFIX
						+ CODE_BLOCK_PRIMITIVE_TYPE_SUFFIX)) {
			currentCodeBlock = (String) paramMap.get(
			    CODE_BLOCK_ALT_PREFIX + CODE_BLOCK_PRIMITIVE_TYPE_SUFFIX);
		    }

		    /* second, check if alternative type code block exists */
		    if (paramMap.containsKey(CODE_BLOCK_ALT_PREFIX
					     + javaType.getName())) {
			currentCodeBlock = (String) paramMap.get(CODE_BLOCK_ALT_PREFIX
								 + javaType.getName());
		    }

		    processor.apply(buffer, currentCodeBlock, map);
		    buffer.append(suffix);
		}
		buffer.setLength(buffer.length() - suffix.length());
	    }
	}

	return buffer.toString();
    }

    /**
     * table iterated code block
     */
    public String iterateTableCodeBlock(String codeBlock,
					String[] tableNames,
					HashMap paramMap,
					HashMap globalMap) 
		throws ApplicationException {
	SQLTypeManager sqlManager = SQLTypeManager.getInstance(SQLTypeManager.ORACLE_TYPE);
	CodeBlockProcessor processor = CodeBlockProcessor.getInstance();
	StringBuffer buffer = new StringBuffer();
	HashMap map = (HashMap) globalMap.clone();
	String prefix = (String) paramMap.get(ITERATION_PREFIX);
	String suffix = (String) paramMap.get(ITERATION_SUFFIX);
	if (prefix == null) {
	    prefix = "";
	}
	if (suffix == null) {
	    suffix = "";
	}

	for (int i = 0; i < tableNames.length; i++) {
	    if (i != 0) {
		buffer.append(prefix);
	    }
	    map.put(KW_ITERATION_INDEX, String.valueOf(i));
	    map.put(KW_TABLE_NAME, tableNames[i]);

	    processor.apply(buffer, codeBlock, map);
	    buffer.append(suffix);
	}
	buffer.setLength(buffer.length() - suffix.length());

	return buffer.toString();
    }

}
