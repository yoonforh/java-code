/*
 * $Id: SaveFileAction.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen.actions;

import java.awt.event.*;
import javax.swing.*;
import java.util.logging.*;
import yoonforh.codegen.EditorPanel;
import yoonforh.codegen.util.zip.ZipExtractor;
import yoonforh.codegen.ui.ActionCommand;
import yoonforh.codegen.ui.AbstractActionPanel;
import yoonforh.codegen.ui.ActionPanelFactory;

/**
 * input sheet creation action 
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-03 15:27:17
 * @author   Yoon Kyung Koo
 */

public class SaveFileAction extends AbstractAction implements ActionCommand {
    private static final String BUTTON_LABEL = "Create input sheet!";
    private static Logger logger
	= Logger.getLogger(SaveFileAction.class.getName());

    private EditorPanel editorPanel = null;

    public SaveFileAction(EditorPanel editorPanel) {
	super(BUTTON_LABEL);

	this.editorPanel = editorPanel;
    }

    /**
     * when code generate button was clicked
     */
    public void actionPerformed(ActionEvent e) {
	String cmd = null;
	String selected = editorPanel.getSelectedTemplate();

	logger.finest("selected template = " + selected);

	if (selected == null) { // if nothing selected just return
	    JOptionPane.showMessageDialog(editorPanel,
					  "No selected templates",
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}

	if (!inputDialog(selected)) {
	    return;
	}

    }

    /**
     * show input sheet dialogs
     */
    private boolean inputDialog(String template) {
	boolean result = false;

	AbstractActionPanel panel = ActionPanelFactory.createPanel(INPUT_SHEET_COMMAND,
								   editorPanel, template);
	assert panel != null : "abstract action panel is null";

	editorPanel.refreshUI();
	result = panel.showDialog("Input Sheet for template " + template);

	return result;
    }
}
