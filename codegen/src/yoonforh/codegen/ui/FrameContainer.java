/*
 * $Id: FrameContainer.java,v 1.1.1.1 2003/05/26 01:53:54 cvsuser Exp $
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

import java.io.File;
import javax.swing.JFrame;

/**
 *
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 10:51:43
 * @author   Yoon Kyung Koo
 */

public interface FrameContainer {
    public JFrame getJFrame();
    public void refreshUI();
}
