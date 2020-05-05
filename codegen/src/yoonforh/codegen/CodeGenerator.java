/*
 * $Id: CodeGenerator.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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
 * template code generator main module
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-24 16:15:25
 * @author   Yoon Kyung Koo
 */

public class CodeGenerator extends JFrame implements ActionListener {
    private static Logger logger
	= Logger.getLogger(CodeGenerator.class.getName());

    protected MainPanel mainPanel = null;

    // ���� �ٸ� ��Ÿ���� ���� ���� ���̺� ������Ʈ
    protected JLabel statusLabel = null;

    public CodeGenerator(String title) throws ApplicationException {
	super(title);

	getContentPane().setLayout(new BorderLayout());

	mainPanel = new MainPanel(this);
	mainPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	getContentPane().add(new JScrollPane(mainPanel), BorderLayout.CENTER);

	// �޴��� ����
	JMenuBar mb = new JMenuBar();
	setJMenuBar(mb); // �� �޴��� ������Ʈ�� �������� �޴��ٷ� ����
	JMenu m = new JMenu("File");
	m.setMnemonic('f');

	// Exit menu
	m.add(new JMenuItem("Exit", 'x')).addActionListener(this);
	mb.add(m);

	// ���� ��
	statusLabel = new JLabel("Welcome to template code generator!!!");
	statusLabel.setBackground(Color.white);
	statusLabel.setOpaque(true); // �������ؾ� ������ ĥ������.

	getContentPane().add(statusLabel, BorderLayout.SOUTH);

	// �������� ������ ����
 	setBackground(Color.lightGray);

	// ������ ���� ��ư Ŭ�� �� �����ϵ��� �̺�Ʈ ó��
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void showUsage() {
	System.out.println("Code Generator Usage\n");
	System.out.println("  Help(This screen) : -help");
	System.out.println("  GUI Mode : default");
	System.out.println("  Batch Mode 1 : -batch <input xml file.cgn>");
	System.out.println("  Batch Mode 2 : -batch <directory in which 'input xml file.cgn's reside>");
	System.out.println("  Input File(*.cgn) Creation Mode : -edit");
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

	if (args.length > 0) {
	    // check if template root directory is specified
	    checkTemplateOptions(args);

	    if ("-batch".equals(args[0])) {
		// process in batch mode
		new BatchCodeGenerator().generate(args[1]);
		return;
	    } else if ("-edit".equals(args[0])) {
		InputFileEditor.main((String[]) null);
		return;
	    } else if ("-help".equals(args[0])) {
		// show help and exit
		showUsage();
		return;
	    }
	}

	CodeGenerator f = new CodeGenerator("Template Code Generator");

	f.setSize(500, 300);
	f.validate();
	f.setLocationRelativeTo(null); // display center of the screen
	f.show();
    }

    public static void checkTemplateOptions(String[] args) {
	for (int i = 0; i < args.length; i++) {
	    if ("-d".equals(args[i])) {
		TemplateManager.getInstance().setTemplateRootDir(args[++i]);
	    }
	}
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
