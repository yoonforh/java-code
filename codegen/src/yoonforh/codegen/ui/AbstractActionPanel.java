/*
 * $Id: AbstractActionPanel.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
 * abstract panel for wizard dialog
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-30 01:05:37
 * @author   Yoon Kyung Koo
 */

public abstract class AbstractActionPanel extends JPanel implements ActionCommand {
    public static final int OK_ACTION = 0x01;
    public static final int CANCEL_ACTION = 0x00;

    // instance members
    protected FrameContainer container = null;
    protected String templateName = null;

    protected AbstractActionPanel(FrameContainer container, String name) {
	this.container = container;
	this.templateName = name;
    }

    /**
     * get template name
     */
    public String getTemplateName() {
	return templateName;
    }

    /**
     * OK label. child classes can override this method to change the text
     */
    protected String getOKButtonLabel() {
	return OK_BUTTON_LABEL;
    }

    /**
     * Cancel label. child classes can override this method to change the text
     */
    protected String getCancelButtonLabel() {
	return CANCEL_BUTTON_LABEL;
    }

    /**
     * show dialog
     *
     * @return boolean if the dialog has OK result
     */
    public boolean showDialog(String title) {
	ActionDialog dlg = new ActionDialog(container.getJFrame(), title, this);
	dlg.show();

	if (dlg.getResult() == OK_ACTION) {
	    container.refreshUI();
	    return true;
	}

	return false;
    }

    /**
     * handler action event
     *
     * @return true if the successfully treated the action, false otherwise
     */
    protected abstract boolean handleAction();

}
