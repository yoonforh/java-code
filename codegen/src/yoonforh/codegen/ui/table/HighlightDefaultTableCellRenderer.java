/*
 * $Id: HighlightDefaultTableCellRenderer.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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


package yoonforh.codegen.ui.table;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.*;

/**
 *
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-04-01 10:37:56
 * @author   Yoon Kyung Koo
 */

public class HighlightDefaultTableCellRenderer extends DefaultTableCellRenderer {
    private Object defaultValue = null;

    public HighlightDefaultTableCellRenderer(Object defaultValue) {
	this.defaultValue = defaultValue;
    }

    /**
     * override default impl. which returns the default table cell renderer.
     *
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at
     *			<code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param isFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
						   boolean isSelected, boolean hasFocus,
						   int row, int column) {
	Component comp = super.getTableCellRendererComponent(table, value, isSelected,
							     hasFocus, row, column);
	Font font = table.getFont().deriveFont(Font.BOLD);
	comp.setFont(font);
	return comp;
    }

}
