/*
 * $Id: CodeGenAction.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
import yoonforh.codegen.MainPanel;
import yoonforh.codegen.util.zip.ZipExtractor;
import yoonforh.codegen.ui.ActionCommand;
import yoonforh.codegen.ui.AbstractActionPanel;
import yoonforh.codegen.ui.ActionPanelFactory;

/**
 * code generation action 
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-25 16:30:53
 * @author   Yoon Kyung Koo
 */

public class CodeGenAction extends AbstractAction implements ActionCommand {
    private static final String BUTTON_LABEL = "Generate code!";
    private static Logger logger
	= Logger.getLogger(CodeGenAction.class.getName());

    private MainPanel mainPanel = null;

    public CodeGenAction(MainPanel mainPanel) {
	super(BUTTON_LABEL);

	this.mainPanel = mainPanel;
    }

    /**
     * when code generate button was clicked
     */
    public void actionPerformed(ActionEvent e) {
	String cmd = null;
	String selected = mainPanel.getSelectedTemplate();

	logger.finest("selected template = " + selected);

	if (selected == null) { // if nothing selected just return
	    JOptionPane.showMessageDialog(mainPanel,
					  "No selected templates",
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}

	// HERE TO GO
	// HERE get the variables using some wizard interface
	//      and pass the values to CodeGenZipWorker

	for (int i = 1; i <= WIZARD_STEPS; i++) {
	    if (!wizardDialog(i, selected)) {
		return;
	    }
	}

    }

    /**
     * show wizard step dialogs
     *
     * STEP 1
     *   fill in paths (root paths)
     *   fill in charsets
     *
     * STEP 2
     *   fill in variables
     *
     * STEP 3
     *   generate codes
     *
     * ZipExtractor unzipper = new ZipExtractor(new CodeGenZipWorker());
     */
    private boolean wizardDialog(int step, String template) {
	boolean result = false;
	String cmd = String.valueOf(step) + WIZARD_COMMAND_POSTFIX;

	AbstractActionPanel panel = ActionPanelFactory.createPanel(cmd, mainPanel, template);
	assert panel != null : "abstract action panel is null";

	mainPanel.refreshUI();
	result = panel.showDialog("Wizard Step " + step + " for template " + template);

	return result;
    }
}
