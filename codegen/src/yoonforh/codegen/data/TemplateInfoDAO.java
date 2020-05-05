/*
 * $Id: TemplateInfoDAO.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen.data;

import org.w3c.dom.*;
import java.io.File;
import java.util.*;
import java.util.logging.*;

import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.util.XMLUtil;

/**
 * template information data
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-25 09:30:37
 * @author   Yoon Kyung Koo
 * @see template_info.dtd
 */

public class TemplateInfoDAO {
    private static Logger logger
	= Logger.getLogger(TemplateInfoDAO.class.getName());

    // xml elements
    public static final String TEMPLATE_INFO_TAG = "template-info";
    public static final String NAME_TAG = "name";
    public static final String DESCRIPTION_TAG = "description";
    public static final String VARIABLES_TAG = "variables";
    public static final String DOC_INFO_TAG = "doc-info";
    public static final String ROOT_PATH_VAR_TAG = "root-path-var";
    public static final String CHARSET_VAR_TAG = "charset-var";
    public static final String GLOBAL_VAR_TAG = "global-var";
    public static final String ZIP_PATH_TAG = "zip-path";
    public static final String PROCESSOR_TAG = "processor";
    public static final String REL_PATH_TAG = "rel-path";
    public static final String ROOT_PATH_TAG = "root-path";
    public static final String CHARSET_TAG = "charset";
    public static final String VAR_TAG = "var";
    public static final String HANDLER_TAG = "handler";
    public static final String PARAM_TAG = "param";

    // xml attribute names
    public static final String ID_ATTR = "id";
    public static final String DEFAULT_ATTR = "default";
    public static final String TYPE_ATTR = "type";
    public static final String NAME_ATTR = "name";

    // singleton instance
    private static TemplateInfoDAO instance = null;

    // instance variables
    /** template info map */
    private HashMap templateMap = new HashMap();

    private TemplateInfoDAO() { }

    public static TemplateInfoDAO getInstance() {
	if (instance == null) {
	    instance = new TemplateInfoDAO();
	}

	return instance;
    }

    /**
     * lookup template info object using template name as a key 
     *
     * @param name template name
     */
    public TemplateInfo getTemplateInfo(String name) {
	return (TemplateInfo) templateMap.get(name);
    }

    /**
     * parse the template info file into template map
     * and returns the template name
     *
     * @return the template name
     */
    public String readTemplateInfo(File file) throws ApplicationException {
	Element root = XMLUtil.loadDocument(file);

	if (root == null) {
	    return null;
	}

	TemplateInfo tmpl = buildTemplateInfo(root);
	String name = tmpl.getName();
	templateMap.put(name, tmpl);
	return name;
    }

    /**
     * build template info object
     */
    private TemplateInfo buildTemplateInfo(Element root) throws ApplicationException {
	String name = null;
	String description = null;
	String defaultRootPathVar = null;
	HashMap rootPathVars = new HashMap();
	HashMap charsetVars = new HashMap();
	HashMap globalVars = new HashMap();
	HashMap docInfos = new HashMap();

	name = XMLUtil.getTagValue(root, NAME_TAG);
	if (name == null) {
	    logger.warning("template-info has no name elements");
	    throw new ApplicationException("template-info has no name elements.");
	}

	description = XMLUtil.getTagValue(root, DESCRIPTION_TAG);
	if (description == null) {
	    logger.warning("template-info has no description elements");
	    throw new ApplicationException("template-info has no description elements.");
	}

	NodeList list;
	Node node;
	String value;
	String varDesc;
	boolean first = true;

	list = root.getElementsByTagName(ROOT_PATH_VAR_TAG);
	for (int i = 0; i < list.getLength(); i++) {
	    node = list.item(i);
	    if (node != null && node instanceof Element) {
		Element el = (Element) node;

		value = el.getAttribute(ID_ATTR);
		varDesc = XMLUtil.getTagValue(node);

		if (value != null) {
		    rootPathVars.put(value, varDesc);

		    // if no default root path is specified
		    // then, the first of root path vars
		    // will be assigned to default root path
		    if (first) {
			defaultRootPathVar = value;
			first = false;
		    }

		    String defaultAttr = el.getAttribute(DEFAULT_ATTR);

		    // if multiple default paths are assigned
		    // then the last will prevail
		    if ("true".equals(defaultAttr)) {
			defaultRootPathVar = value;
		    }
		}
	    }
	}
	logger.finest("default root path var is " + defaultRootPathVar);

	list = root.getElementsByTagName(CHARSET_VAR_TAG);
	for (int i = 0; i < list.getLength(); i++) {
	    node = list.item(i);
	    if (node != null && node instanceof Element) {
		Element el = (Element) node;

		value = el.getAttribute(ID_ATTR);
		varDesc = XMLUtil.getTagValue(node);

		if (value != null) {
		    charsetVars.put(value, varDesc);
		}
	    }
	}

	list = root.getElementsByTagName(GLOBAL_VAR_TAG);
	for (int i = 0; i < list.getLength(); i++) {
	    node = list.item(i);
	    if (node != null && node instanceof Element) {
		Element el = (Element) node;
		String type = null;

		value = el.getAttribute(ID_ATTR);
		varDesc = XMLUtil.getTagValue(node);
		type = el.getAttribute(TYPE_ATTR);

		if (value != null) {
		    globalVars.put(value, new GlobalVariable(value, varDesc, type));
		}
	    }
	}

	// read doc-infos
	list = root.getElementsByTagName(DOC_INFO_TAG);
	for (int i = 0; i < list.getLength(); i++) {
	    node = list.item(i);
	    if (node != null) {
		DocInfo docInfo = buildDocInfo(node);

		if (docInfo != null) {
		    docInfos.put(docInfo.getZipPath(), docInfo);
		    if (logger.isLoggable(Level.FINEST)) {
			logger.finest("adding doc info - " + docInfo);
		    }
		}
	    }
	}

	return new TemplateInfo(name, description, defaultRootPathVar, rootPathVars,
				charsetVars, globalVars, docInfos);
    }
    
    /**
     * build doc info object
     */
    private DocInfo buildDocInfo(Node node) throws ApplicationException {
	String zipPath = null;
	ProcessorInfo processorInfo = null;
	String relPath = null;
	String rootPathVar = null;
	String charsetVar = null;
	String description = null;
	HashMap vars = new HashMap();

	zipPath = XMLUtil.getSubTagValue(node, ZIP_PATH_TAG);
	processorInfo = getProcessorInfo(node);
	relPath = XMLUtil.getSubTagValue(node, REL_PATH_TAG);
	rootPathVar = XMLUtil.getSubTagValue(node, ROOT_PATH_TAG);
	charsetVar = XMLUtil.getSubTagValue(node, CHARSET_TAG);
	description = XMLUtil.getSubTagValue(node, DESCRIPTION_TAG);

	NodeList children = node.getChildNodes();
	for (int i =0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if ((child != null) && (child.getNodeName() != null)
		&& child.getNodeName().equals(VAR_TAG) ) {
		String name = null;
		List handlers = buildHandlerList(child);
		if (child instanceof Element) {
		    name = ((Element) child).getAttribute(NAME_ATTR);
		}

		vars.put(name, new Variable(name, handlers));
	    }
	}

	return new DocInfo(zipPath, processorInfo, relPath, rootPathVar,
			   charsetVar, vars, description);
    }

    /**
     * build handler objects list
     */
    private List buildHandlerList(Node node) throws ApplicationException {
	ArrayList list = new ArrayList();

        if (node != null) {
            NodeList children = node.getChildNodes();
            for (int i =0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child != null && HANDLER_TAG.equals(child.getNodeName())) {
		    String type = null;
		    HashMap paramMap = new HashMap();
		    NodeList grandChildren = child.getChildNodes();
		    if (child instanceof Element) {
			type = ((Element) child).getAttribute(TYPE_ATTR);
		    }
		    if (type == null || type.length() == 0) {
			continue;
		    }

		    for (int j = 0; j < grandChildren.getLength(); j++) {
			Node grandChild = grandChildren.item(j);
			String paramName = null;
			String paramValue = null;

			if (grandChild != null && PARAM_TAG.equals(grandChild.getNodeName())) {
			    if (grandChild instanceof Element) {
				paramName = ((Element) grandChild).getAttribute(NAME_ATTR);
				Node valueNode = grandChild.getFirstChild();
				if (valueNode != null) {
				    paramValue = valueNode.getNodeValue();
				}

				paramMap.put(paramName, paramValue);
			    }
			}
		    }

		    list.add(new HandlerInfo(type, (paramMap.isEmpty() ? null : paramMap)));
                }
            } // end loop

        }

	return (list.isEmpty() ? null : list);
    }

    /**
     * get info of processor
     */
    private ProcessorInfo getProcessorInfo(Node node) throws ApplicationException {

        if (node != null) {
            NodeList children = node.getChildNodes();
            for (int i =0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child != null && PROCESSOR_TAG.equals(child.getNodeName())) {
		    String type = null;
		    HashMap paramMap = new HashMap();
		    NodeList grandChildren = child.getChildNodes();
		    if (child instanceof Element) {
			type = ((Element) child).getAttribute(TYPE_ATTR);
		    }
		    if (type == null || type.length() == 0) {
			continue;
		    }

		    for (int j = 0; j < grandChildren.getLength(); j++) {
			Node grandChild = grandChildren.item(j);
			String paramName = null;
			String paramValue = null;

			if (grandChild != null && PARAM_TAG.equals(grandChild.getNodeName())) {
			    if (grandChild instanceof Element) {
				paramName = ((Element) grandChild).getAttribute(NAME_ATTR);
				Node valueNode = grandChild.getFirstChild();
				if (valueNode != null) {
				    paramValue = valueNode.getNodeValue();
				}

				paramMap.put(paramName, paramValue);
			    }
			}
		    }

		    return new ProcessorInfo(type, (paramMap.isEmpty() ? null : paramMap));
                }
            } // end loop

        }

	return null;
    }

}
