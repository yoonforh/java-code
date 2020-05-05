/*
 * $Id: ActionPanelFactory.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

/**
 * panel factory
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 11:08:39
 * @author   Yoon Kyung Koo
 */

public class ActionPanelFactory implements ActionCommand {
    // protect against wrong usage
    private ActionPanelFactory() {}

    /**
     * factory method
     */
    public static AbstractActionPanel createPanel(String cmd, FrameContainer container,
						  String templateName) {
	if (cmd.equals(STEP_ONE_COMMAND)) {
	    return WizardStepOneActionPanel.getPanel(container, templateName);
	} else if (cmd.equals(STEP_TWO_COMMAND)) {
	    return WizardStepTwoActionPanel.getPanel(container, templateName);
	} else if (cmd.equals(STEP_THREE_COMMAND)) {
	    return WizardStepThreeActionPanel.getPanel(container, templateName);
	} else if (cmd.equals(INPUT_SHEET_COMMAND)) {
	    return InputSheetActionPanel.getPanel(container, templateName);
	}

	// if unknown action command
	return null;
    }
}
