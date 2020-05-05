/*
 * $Id: EditorPanel.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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


package yoonforh.codegen;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import yoonforh.codegen.actions.SaveFileAction;
import yoonforh.codegen.ui.FrameContainer;
import yoonforh.codegen.data.TemplateInfoDAO;

/**
 * code generator main panel
 *
 * <br>ui structure
 * +-----------------------------------+
 * |                                   |
 * |  [ jtable                     ]   |
 * |                                   |
 * |                        [save!]    |
 * +-----------------------------------+
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-02 17:32:00
 * @author   Yoon Kyung Koo
 */

public class EditorPanel extends JPanel implements FrameContainer {
    private static Logger logger
	= Logger.getLogger(EditorPanel.class.getName());

    private InputFileEditor frame = null;
    private ButtonGroup templateButtonGroup = new ButtonGroup();
    private TemplateManager templateManager = TemplateManager.getInstance();

    public EditorPanel(InputFileEditor frame) throws ApplicationException {
	this.frame = frame;

	// init template info file and register template jar file
	templateManager.initTemplates();

	// group the radio buttons
	JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), "Select Template"));

	Iterator it = templateManager.getTemplateMap().keySet().iterator();
	while (it.hasNext()) {
	    JPanel radioPanel = new JPanel();
	    String template = (String) it.next();
	    JRadioButton radio = new JRadioButton(template);
	    radio.setActionCommand(template);
	    radio.setPreferredSize(new Dimension(150, 20));
	    radioPanel.add(radio);
	    templateButtonGroup.add(radio);
	    radioPanel.add(new JLabel(TemplateInfoDAO.getInstance()
				      .getTemplateInfo(template).getDescription()));
	    panel.add(radioPanel);
	}

	setLayout(new BorderLayout());
	add(panel, BorderLayout.CENTER);

	// button panel
	panel = new JPanel();
	panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	panel.add(new JButton(new SaveFileAction(this)));
	panel.setBackground(Color.gray);
	panel.setOpaque(true);
	add(panel, BorderLayout.SOUTH);
    }

    public String getSelectedTemplate() {
	ButtonModel selected = templateButtonGroup.getSelection();
	if (selected == null) {
	    return null;
	}

	return selected.getActionCommand();
    }

    /*
     * TemplateContainer interface impl.
     */
    public JFrame getJFrame() {
	return frame;
    }

    public void refreshUI() {
	// for now, nothing to do
    }

}
