/*
 * $Id: ActionCommand.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
 * action command constants
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 11:11:54
 * @author   Yoon Kyung Koo
 */

public interface ActionCommand {
    String OK_BUTTON_LABEL = "OK";
    String CANCEL_BUTTON_LABEL = "Cancel";
    String NEXT_BUTTON_LABEL = "Next";
    String GENERATE_BUTTON_LABEL = "Generate";
    String SAVE_FILE_BUTTON_LABEL = "Save As A File";

    String WIZARD_COMMAND_POSTFIX = "-step";
    String STEP_ONE_COMMAND = "1-step";
    String STEP_TWO_COMMAND = "2-step";
    String STEP_THREE_COMMAND = "3-step";
    int WIZARD_STEPS = 3;

    String INPUT_SHEET_COMMAND = "input-sheet";
}
