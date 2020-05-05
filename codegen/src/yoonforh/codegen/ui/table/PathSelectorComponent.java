/*
 * $Id: PathSelectorComponent.java,v 1.2 2003/05/31 00:14:28 yoonforh Exp $
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

import java.awt.event.*;
import java.util.logging.*;
import javax.swing.*;

/**
 * table cell editor for file path
 *
 * @version  $Revision: 1.2 $<br>
 *           created at 2003-04-01 09:24:10
 * @author   Yoon Kyung Koo
 */

public class PathSelectorComponent extends JButton implements ActionListener {
    private static Logger logger
	= Logger.getLogger(PathSelectorComponent.class.getName());

    /** store recently chosen value for convenience */
    private static String recentValue = "";

    private String value = "";
    private boolean isDirectory = false;

    public PathSelectorComponent(String value) {
	// default is directory only
	this(value, true);
    }

    public PathSelectorComponent(String value, boolean isDirectory) {
	this.value = value;
	this.isDirectory = isDirectory;
	setText(value);
	addActionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
	JFileChooser chooser = new JFileChooser();

	if (isDirectory) {
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	if (value != null && value.length() > 0) {
	    chooser.setSelectedFile(new java.io.File(value));
	} else {
	    if (recentValue != null && recentValue.length() > 0) {
		chooser.setSelectedFile(new java.io.File(recentValue));
	    }
	}

	int returnVal = chooser.showOpenDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    try {
		value = chooser.getSelectedFile().getCanonicalPath();
		setText(value);
	    } catch (java.io.IOException e) {
		logger.throwing("PathSelectorComponent", "actionPerformed", e);
	    }

	    // store recently chosen value
	    recentValue = value;
	}
    }

    public String getValue() {
	return value;
    }
}
