/*
 * $Id: ActionDialog.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * action dialog used by action panel
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 14:24:09
 * @author   Yoon Kyung Koo
 */

class ActionDialog extends JDialog implements ActionListener {
    // constants
    private final static int INITIAL_WIDTH = 640;
    private final static int INITIAL_HEIGHT = 480;
    private final static Dimension PREFERRED_SIZE
	= new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT);

    // instance members
    private AbstractActionPanel actionPanel = null;
    private int returnValue = AbstractActionPanel.CANCEL_ACTION;

    public ActionDialog(JFrame frame, String title, AbstractActionPanel panel) {
	super(frame, title, true);

	this.actionPanel = panel;
	JPanel buttonPanel = new JPanel();
	JButton okButton = new JButton(panel.getOKButtonLabel());
	okButton.setDefaultCapable(true);
	okButton.setActionCommand(ActionCommand.OK_BUTTON_LABEL);
	okButton.addActionListener(this);
	buttonPanel.add(okButton);
	JButton cancelButton = new JButton(panel.getCancelButtonLabel());
	cancelButton.setActionCommand(ActionCommand.CANCEL_BUTTON_LABEL);
	cancelButton.addActionListener(this);
	buttonPanel.add(cancelButton);

	getContentPane().setLayout(new BorderLayout());
	JScrollPane scrollPane = new 
	    JScrollPane(panel, 
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	getContentPane().add(scrollPane, BorderLayout.CENTER);
	getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	returnValue = AbstractActionPanel.CANCEL_ACTION;
    }

    /**
     * override default behavior to show in the center of parent window
     */
    public void show() {
	setSize(ActionDialog.PREFERRED_SIZE);
	setLocationRelativeTo(getOwner());
	super.show();
    }

    /**
     * action listener method
     */
    public void actionPerformed(ActionEvent evt) {
	String cmd = evt.getActionCommand();

	if (cmd.equals(ActionCommand.OK_BUTTON_LABEL)) {
	    // first treat action request
	    if (!actionPanel.handleAction()) {
		return;
	    }

	    returnValue = AbstractActionPanel.OK_ACTION;
	    dispose();
	} else if (cmd.equals(ActionCommand.CANCEL_BUTTON_LABEL)) {
	    returnValue = AbstractActionPanel.CANCEL_ACTION;
	    dispose();
	}
    }

    /**
     * return OK/Cancel
     */
    public int getResult() {
	return returnValue;
    }

}
