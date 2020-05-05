/*
 * $Id: InputSheetActionPanel.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.border.BevelBorder;
import org.w3c.dom.Document;
import yoonforh.codegen.ApplicationException;
import yoonforh.codegen.BatchCodeGenerator;
import yoonforh.codegen.TemplateManager;
import yoonforh.codegen.data.TemplateInfoDAO;
import yoonforh.codegen.data.TemplateInfo;
import yoonforh.codegen.data.ValuesStore;
import yoonforh.codegen.data.TemplateValues;
import yoonforh.codegen.data.InputSheetDocumentBuilder;
import yoonforh.codegen.util.XMLUtil;
import yoonforh.codegen.ui.table.HashMapTableModel;
import yoonforh.codegen.ui.table.HighlightDefaultTableCellRenderer;
import yoonforh.codegen.ui.table.FileSelectionCellEditor;
import yoonforh.codegen.ui.table.MapKeyExplainer;
import yoonforh.codegen.ui.table.GlobalVariableTypeChecker;
import yoonforh.codegen.ui.table.GlobalVariableKeyExplainer;

/**
 * wizard step 3
 *   generate codes
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-03 15:31:02
 * @author   Yoon Kyung Koo
 */

public class InputSheetActionPanel extends AbstractActionPanel {
    private static Logger logger
	= Logger.getLogger(InputSheetActionPanel.class.getName());
    protected static InputSheetActionPanel panel = null;

    protected JTable rootPathTable = null;
    protected JTable charsetTable = null;
    protected JTable globalVarTable = null;

    /**
     * protected constructor
     * make ui layout here
     */
    protected InputSheetActionPanel(FrameContainer container, String templateName) {
	super(container, templateName);
    }

    /**
     * constructor will not be each time, but init() method should be called each time
     */
    protected void init() {
	TemplateInfoDAO dao = TemplateInfoDAO.getInstance();
	TemplateInfo info = dao.getTemplateInfo(templateName);

	// present ui for read root paths and charsets
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BorderLayout());

	JPanel panel = new JPanel();
	panel.add(new JLabel("Edit Input Values"));
	mainPanel.add(panel, BorderLayout.NORTH);

	JTabbedPane tabPane = new JTabbedPane();
	mainPanel.add(tabPane, BorderLayout.CENTER);

	panel = new JPanel();
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

	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createEtchedBorder());

	globalVarTable = new JTable(
	    new HashMapTableModel(info.getGlobalVars().keySet(),
				  HashMapTableModel.DIVERSE_TYPE,
				  new GlobalVariableTypeChecker(info.getGlobalVars()),
				  new GlobalVariableKeyExplainer(info.getGlobalVars())));
	column = globalVarTable.getColumn(HashMapTableModel.DEFAULT_VALUE_COLUMN_TITLE);
	column.setCellEditor(new FileSelectionCellEditor());
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
	    panel = new InputSheetActionPanel(container, templateName);
	}

	// we should call init() method every time getPanel() is called
	// but, we can't use inheritance mechanism in this static context
	panel.init();
	return panel;
    }

    protected String getOKButtonLabel() {
	return SAVE_FILE_BUTTON_LABEL;
    }

    /**
     * action handler
     */
    protected boolean handleAction() {
	logger.finest("input sheet handleAction()");

	// update current edit
	if (rootPathTable.isEditing()) {
	    rootPathTable.editCellAt(-1, -1);
	}
	if (charsetTable.isEditing()) {
	    charsetTable.editCellAt(-1, -1);
	}
	if (globalVarTable.isEditing()) {
	    globalVarTable.editCellAt(-1, -1);
	}

	TemplateInfoDAO dao = TemplateInfoDAO.getInstance();
	TemplateInfo info = dao.getTemplateInfo(templateName);

	HashMap rootPathValues = ((HashMapTableModel) rootPathTable.getModel()).getValues();
	HashMap charsetValues = ((HashMapTableModel) charsetTable.getModel()).getValues();
	HashMap globalValues = ((HashMapTableModel) globalVarTable.getModel()).getValues();

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

	if (globalValues.size() != info.getGlobalVars().size()) { // if something is not specified
	    JOptionPane.showMessageDialog(this,
					  "You should fill in all the global variables.",
					  "Error", JOptionPane.ERROR_MESSAGE);
	    return false;
	}

	TemplateValues values = ValuesStore.getInstance().getTemplateValues();
	values.setRootPaths(rootPathValues);
	values.setCharsets(charsetValues);
	values.setVariables(globalValues);

	logger.finest("input sheet values are : rootPaths - " + values.getRootPaths()
		      + ", charsets - " + values.getCharsets()
		      + ", globals - " + values.getVariables());

	return saveSheet(templateName, values);
    }

    protected boolean saveSheet(String templateName, TemplateValues values) {
	JFileChooser chooser = new JFileChooser();
	chooser.setFileFilter(new FileFilter() {
		public boolean accept(java.io.File f) {
		    if (f.isDirectory()) {
			return true;
		    }

		    return f.getName().endsWith(BatchCodeGenerator.INPUT_FILE_EXT);
		}

		public String getDescription() {
		    return "Code Generator Input File Sheets";
		}
	    });

	int returnVal = chooser.showOpenDialog(this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    try {
		String path = chooser.getSelectedFile().getCanonicalPath();
		if (path == null || path.length() == 0) {
		    return false;
		}

		if (!path.endsWith(BatchCodeGenerator.INPUT_FILE_EXT)) {
		    path += BatchCodeGenerator.INPUT_FILE_EXT;
		}

		Document doc = InputSheetDocumentBuilder.getInstance().build(templateName, values);
		XMLUtil.writeDocument(new java.io.File(path), doc);
		JOptionPane.showMessageDialog(this,
					      "Generated new input sheet - " + path,
					      "Information", JOptionPane.INFORMATION_MESSAGE);
		return true;
	    } catch (java.io.IOException e) {
		logger.throwing("InputSheetActionPanel", "saveSheet", e);
	    } catch (ApplicationException e) {
		logger.throwing("InputSheetActionPanel", "saveSheet", e);
	    }
	}

	return false;
    }

}
