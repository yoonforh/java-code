/*
 * $Id: WizardStepTwoActionPanel.java,v 1.2 2003/05/31 00:14:28 yoonforh Exp $
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
import java.util.logging.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.TableColumn;
import yoonforh.codegen.data.TemplateInfoDAO;
import yoonforh.codegen.data.TemplateInfo;
import yoonforh.codegen.data.ValuesStore;
import yoonforh.codegen.data.TemplateValues;
import yoonforh.codegen.ui.table.HashMapTableModel;
import yoonforh.codegen.ui.table.FileSelectionCellEditor;
import yoonforh.codegen.ui.table.GlobalVariableTypeChecker;
import yoonforh.codegen.ui.table.GlobalVariableKeyExplainer;

/**
 * wizard step 2
 *   fill in variables
 *
 * @version  $Revision: 1.2 $<br>
 *           created at 2003-03-31 11:16:25
 * @author   Yoon Kyung Koo
 */

public class WizardStepTwoActionPanel extends AbstractActionPanel {
    private static Logger logger
	= Logger.getLogger(WizardStepTwoActionPanel.class.getName());
    protected static WizardStepTwoActionPanel panel = null;

    protected JTable globalVarTable = null;

    /**
     * protected constructor
     * make ui layout here
     */
    protected WizardStepTwoActionPanel(FrameContainer container, String templateName) {
	super(container, templateName);

	init();
    }

    /**
     * constructor will not be each time, but init() method should be called each time
     */
    protected void init() {
	TemplateInfoDAO dao = TemplateInfoDAO.getInstance();
	TemplateInfo info = dao.getTemplateInfo(templateName);

	// present ui for read global variables
	JTabbedPane tabPane = new JTabbedPane();

	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createEtchedBorder());

	globalVarTable = new JTable(
	    new HashMapTableModel(info.getGlobalVars().keySet(),
				  HashMapTableModel.DIVERSE_TYPE,
				  new GlobalVariableTypeChecker(info.getGlobalVars()),
				  new GlobalVariableKeyExplainer(info.getGlobalVars())));
	TableColumn column = globalVarTable.getColumn(HashMapTableModel.DEFAULT_VALUE_COLUMN_TITLE);
	column.setCellEditor(new FileSelectionCellEditor());
	panel.add(new JScrollPane(globalVarTable));
	tabPane.add("Global Variables", panel);

	setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
 	setLayout(new BorderLayout());
	add(tabPane, BorderLayout.CENTER);
    }

    /**
     * singleton method
     */
    public static AbstractActionPanel getPanel(FrameContainer container, String templateName) {
	if (panel == null) {
	    panel = new WizardStepTwoActionPanel(container, templateName);
	}

	return panel;
    }

    protected String getOKButtonLabel() {
	return NEXT_BUTTON_LABEL;
    }

    /**
     * action handler
     */
    protected boolean handleAction() {
	logger.finest("step 2 handleAction()");
	    
	// update current edit
	if (globalVarTable.isEditing()) {
	    globalVarTable.editCellAt(-1, -1);
	}

	TemplateInfoDAO dao = TemplateInfoDAO.getInstance();
	TemplateInfo info = dao.getTemplateInfo(templateName);

	HashMap globalValues = ((HashMapTableModel) globalVarTable.getModel()).getValues();
	if (globalValues.size() != info.getGlobalVars().size()) { // if something is not specified
	    logger.finest("global values are " + globalValues
			  + " and info vars are " + info.getGlobalVars());
	    JOptionPane.showMessageDialog(this,
					  "You should fill in all the global variables.",
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return false;
	}

	TemplateValues values = ValuesStore.getInstance().getTemplateValues();
	values.setVariables(globalValues);

	logger.finest("step 2 values are : rootPaths - " + values.getRootPaths()
		      + ", charsets - " + values.getCharsets()
		      + ", globals - " + values.getVariables());

	return true;
    }
}
