/*
 * $Id: WizardStepOneActionPanel.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
import yoonforh.codegen.ui.table.HighlightDefaultTableCellRenderer;
import yoonforh.codegen.ui.table.MapKeyExplainer;

/**
 * code generator wizard 1st step
 *
 * STEP 1
 *   fill in paths (root paths)
 *   fill in charsets
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 11:16:25
 * @author   Yoon Kyung Koo
 */

public class WizardStepOneActionPanel extends AbstractActionPanel {
    private static Logger logger
	= Logger.getLogger(WizardStepOneActionPanel.class.getName());
    protected static WizardStepOneActionPanel panel = null;

    protected JTable rootPathTable = null;
    protected JTable charsetTable = null;

    /**
     * protected constructor
     * make ui layout here
     */
    protected WizardStepOneActionPanel(FrameContainer container, String templateName) {
	super(container, templateName);

	init();
	logger.finest("step 1 constructed(), rootPathTable - " + rootPathTable);
    }

    /**
     * constructor will not be each time, but init() method should be called each time
     */
    protected void init() {
	logger.finest("step 1 init(), rootPathTable - " + rootPathTable);

	TemplateInfoDAO dao = TemplateInfoDAO.getInstance();
	TemplateInfo info = dao.getTemplateInfo(templateName);

	// present ui for read root paths and charsets
	JTabbedPane tabPane = new JTabbedPane();

	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createEtchedBorder());

	rootPathTable = new JTable(new HashMapTableModel(info.getRootPathVars().keySet(),
							 HashMapTableModel.ALL_DIRECTORY_TYPE,
							 null,
							 new MapKeyExplainer(info.getRootPathVars())));
	TableColumn column = rootPathTable.getColumn(HashMapTableModel.DEFAULT_VALUE_COLUMN_TITLE);
	column.setCellEditor(new FileSelectionCellEditor());
	column = rootPathTable.getColumn(HashMapTableModel.DEFAULT_KEY_COLUMN_TITLE);
	column.setCellRenderer(new HighlightDefaultTableCellRenderer(info.getDefaultRootPathVar()));

	panel.add(new JScrollPane(rootPathTable));
	tabPane.add("Root Path Variables", panel);

	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createEtchedBorder());

	charsetTable = new JTable(new HashMapTableModel(info.getCharsetVars().keySet(),
							HashMapTableModel.ALL_PLAIN_TYPE,
							null,
							new MapKeyExplainer(info.getCharsetVars())));
	panel.add(new JScrollPane(charsetTable));
	tabPane.add("Character Set Variables", panel);

	setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
 	setLayout(new BorderLayout());
	add(tabPane, BorderLayout.CENTER);
    }

    /**
     * singleton method
     */
    public static AbstractActionPanel getPanel(FrameContainer container, String templateName) {
	if (panel == null) {
	    panel = new WizardStepOneActionPanel(container, templateName);
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
	logger.finest("step 1 handleAction(), rootPathTable - " + rootPathTable);

	// update current edit
	if (rootPathTable.isEditing()) {
	    rootPathTable.editCellAt(-1, -1);
	}
	if (charsetTable.isEditing()) {
	    charsetTable.editCellAt(-1, -1);
	}

	TemplateInfoDAO dao = TemplateInfoDAO.getInstance();
	TemplateInfo info = dao.getTemplateInfo(templateName);

	HashMap rootPathValues = ((HashMapTableModel) rootPathTable.getModel()).getValues();
	HashMap charsetValues = ((HashMapTableModel) charsetTable.getModel()).getValues();

	if (rootPathValues.size() != info.getRootPathVars().size()) { // if something is not specified
	    JOptionPane.showMessageDialog(this,
					  "You should fill in all the root path variables.",
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return false;
	}

	if (charsetValues.size() != info.getCharsetVars().size()) { // if something is not specified
	    JOptionPane.showMessageDialog(this,
					  "You should fill in all the charset variables.",
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return false;
	}

	TemplateValues values = ValuesStore.getInstance().getTemplateValues();
	values.setRootPaths(rootPathValues);
	values.setCharsets(charsetValues);

	logger.finest("step 1 values are : rootPaths - " + values.getRootPaths()
		      + ", charsets - " + values.getCharsets()
		      + ", globals - " + values.getVariables());
	return true;
    }
}
