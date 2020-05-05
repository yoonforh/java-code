/*
 * $Id: InputFileEditor.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.io.*;
import java.util.logging.*; // logger

/**
 * template input file editor
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-02 16:44:51
 * @author   Yoon Kyung Koo
 */

public class InputFileEditor extends JFrame implements ActionListener {
    private static Logger logger
	= Logger.getLogger(InputFileEditor.class.getName());


    protected EditorPanel editorPanel = null;

    // ���� �ٸ� ��Ÿ���� ���� ���� ���̺� ������Ʈ
    protected JLabel statusLabel = null;

    public InputFileEditor(String title) throws ApplicationException {
	super(title);

	getContentPane().setLayout(new BorderLayout());

	editorPanel = new EditorPanel(this);
	editorPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	getContentPane().add(new JScrollPane(editorPanel), BorderLayout.CENTER);

	// �޴��� ����
	JMenuBar mb = new JMenuBar();
	setJMenuBar(mb); // �� �޴��� ������Ʈ�� �������� �޴��ٷ� ����
	JMenu m = new JMenu("File");
	m.setMnemonic('f');

	// Exit menu
	m.add(new JMenuItem("Exit", 'x')).addActionListener(this);
	mb.add(m);

	// ���� ��
	statusLabel = new JLabel("Welcome to template input file editor!!!");
	statusLabel.setBackground(Color.white);
	statusLabel.setOpaque(true); // �������ؾ� ������ ĥ������.

	getContentPane().add(statusLabel, BorderLayout.SOUTH);

	// �������� ������ ����
 	setBackground(Color.lightGray);

	// ������ ���� ��ư Ŭ�� �� �����ϵ��� �̺�Ʈ ó��
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void showUsage() {
	System.out.println("Input File Editor Usage\n");
	System.out.println("  Help(This screen) : -help");
	System.out.println("  Options : -d <template root directory> ");
    }

    public static void main(String[] args) throws ApplicationException {
	/*
	 * xerces SAX �ļ� ���丮�� ������ ����
	 */
	System.setProperty("javax.xml.parsers.SAXParserFactory",
			   "org.apache.xerces.jaxp.SAXParserFactoryImpl");

	/*
	 * xerces DOM �ļ� ���丮�� ������ ����
	 */
	System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
			   "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

	if (args != null && args.length > 0) {
	    // check if template root directory is specified
	    CodeGenerator.checkTemplateOptions(args);

	    if ("-help".equals(args[0])) {
		// show help and exit
		showUsage();
		System.exit(0);
	    }

	}

	InputFileEditor f = new InputFileEditor("Template Input File (*.cgn) Editor");

	f.setSize(500, 300);
	f.validate();
	f.setLocationRelativeTo(null); // display center of the screen
	f.show();
    }

    public void actionPerformed(ActionEvent evt) {
	String cmd = evt.getActionCommand();

	// Exit �޴� ������ Ŭ�� �� �����ϵ��� ó��
	if (cmd.equals("Exit")) {
	    dispose();
	    System.exit(0);
	}
    }

    public void showStatus(String message) {
	statusLabel.setText(message);
    }

}
