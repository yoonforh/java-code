/*
 * $Id: WizardStepThreeActionPanel.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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
import java.util.HashMap;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.border.BevelBorder;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.TemplateManager;
import yoonforh.codegen.data.TemplateInfoDAO;
import yoonforh.codegen.data.TemplateInfo;
import yoonforh.codegen.data.ValuesStore;
import yoonforh.codegen.data.TemplateValues;
import yoonforh.codegen.ui.table.HashMapTableModel;
import yoonforh.codegen.ui.table.HighlightDefaultTableCellRenderer;
import yoonforh.codegen.ui.table.MapKeyExplainer;
import yoonforh.codegen.ui.table.GlobalVariableTypeChecker;
import yoonforh.codegen.ui.table.GlobalVariableKeyExplainer;
import yoonforh.codegen.util.CodeGenZipWorker;
import yoonforh.codegen.util.zip.ZipExtractor;

/**
 * wizard step 3
 *   generate codes
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 11:16:25
 * @author   Yoon Kyung Koo
 */

public class WizardStepThreeActionPanel extends AbstractActionPanel {
    private static Logger logger
	= Logger.getLogger(WizardStepThreeActionPanel.class.getName());
    protected static WizardStepThreeActionPanel panel = null;

    protected JTable rootPathTable = null;
    protected JTable charsetTable = null;
    protected JTable globalVarTable = null;

    /**
     * protected constructor
     * make ui layout here
     */
    protected WizardStepThreeActionPanel(FrameContainer container, String templateName) {
	super(container, templateName);

	init();
    }

    /**
     * constructor will not be each time, but init() method should be called each time
     */
    protected void init() {
	TemplateInfoDAO dao = TemplateInfoDAO.getInstance();
	TemplateInfo info = dao.getTemplateInfo(templateName);
	TemplateValues values = ValuesStore.getInstance().getTemplateValues();
	HashMap rootPaths = values.getRootPaths();
	HashMap charsets = values.getCharsets();
	HashMap globals = values.getVariables();

	logger.finest("values are : rootPaths - " + rootPaths
		      + ", charsets - " + charsets
		      + ", globals - " + globals);

	// present ui for read root paths and charsets
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BorderLayout());

	JPanel panel = new JPanel();
	panel.add(new JLabel("Values for code generation"));
	mainPanel.add(panel, BorderLayout.NORTH);

	JTabbedPane tabPane = new JTabbedPane();
	mainPanel.add(tabPane, BorderLayout.CENTER);

	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createEtchedBorder());

	HashMapTableModel tableModel = new
	    HashMapTableModel(rootPaths.keySet(),
			      HashMapTableModel.ALL_DIRECTORY_TYPE,
			      null,
			      new MapKeyExplainer(info.getRootPathVars()));
	tableModel.setDataMap(rootPaths);
	tableModel.setReadOnly(true);
	rootPathTable = new JTable(tableModel);
	TableColumn column = rootPathTable.getColumn(HashMapTableModel.DEFAULT_KEY_COLUMN_TITLE);
	column.setCellRenderer(new HighlightDefaultTableCellRenderer(info.getDefaultRootPathVar()));

	panel.add(new JScrollPane(rootPathTable));
	tabPane.add("Root Path Variables", panel);

	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createEtchedBorder());

	tableModel = new
	    HashMapTableModel(charsets.keySet(),
			      HashMapTableModel.ALL_PLAIN_TYPE,
			      null,
			      new MapKeyExplainer(info.getCharsetVars()));
	tableModel.setDataMap(charsets);
	tableModel.setReadOnly(true);

	charsetTable = new JTable(tableModel);
	panel.add(new JScrollPane(charsetTable));
	tabPane.add("Character Set Variables", panel);

	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createEtchedBorder());

	tableModel = new
	    HashMapTableModel(globals.keySet(),
			      HashMapTableModel.DIVERSE_TYPE,
			      new GlobalVariableTypeChecker(info.getGlobalVars()),
			      new GlobalVariableKeyExplainer(info.getGlobalVars()));
	tableModel.setDataMap(globals);
	tableModel.setReadOnly(true);

	globalVarTable = new JTable(tableModel);
	panel.add(new JScrollPane(globalVarTable));
	tabPane.add("Global Variables", panel);

	setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
 	setLayout(new BorderLayout());
	add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * singleton method
     */
    public static AbstractActionPanel getPanel(FrameContainer container, String templateName) {
	if (panel == null) {
	    panel = new WizardStepThreeActionPanel(container, templateName);
	}

	return panel;
    }

    protected String getOKButtonLabel() {
	return GENERATE_BUTTON_LABEL;
    }

    /**
     * action handler
     */
    protected boolean handleAction() {
	logger.finest("step 3 handleAction()");

	java.io.File zipFile = TemplateManager.getInstance().getTemplateFile(templateName);

	try {
	    ZipExtractor unzipper = new ZipExtractor(new CodeGenZipWorker(templateName));
	    unzipper.extract(zipFile);
	} catch (java.io.IOException e) {
	    logger.throwing("WizardStepThreeActionPanel", "handleAction", e);

	    JOptionPane.showMessageDialog(this,
					  "cannot extract template files - " + e.getMessage(),
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return false;
	} catch (ApplicationException e) {
	    logger.throwing("WizardStepThreeActionPanel", "handleAction", e);

	    JOptionPane.showMessageDialog(this,
					  "cannot extract template files - " + e.getMessage(),
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return false;
	}

	return true;
    }
}
