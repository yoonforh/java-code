/*
 * $Id: FileSelectionCellEditor.java,v 1.1.1.1 2003/05/26 01:53:55 cvsuser Exp $
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

import java.util.EventObject;
import java.util.logging.*;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

/**
 * table cell editor for file path
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2003-03-31 17:55:24
 * @author   Yoon Kyung Koo
 */

public class FileSelectionCellEditor implements TableCellEditor {
    private static Logger logger
	= Logger.getLogger(FileSelectionCellEditor.class.getName());
    protected EventListenerList listenerList = new EventListenerList();
    transient protected ChangeEvent changeEvent = null;
    protected JComponent editor = null;

    /*
     * TableCellEditor interface methods
     */
    public Component getTableCellEditorComponent(JTable table,
						 Object value,
						 boolean isSelected,
						 int row,
						 int column) {
	logger.finest("getTableCellEditorComponent(table - " + table
		      + ", value - " + value
		      + ", isSelected - " + isSelected
		      + ", row - " + row
		      + ", column - " + column
		      + ")");

	HashMapTableModel model = (HashMapTableModel) table.getModel();
	if (model.getValueTypeAt(row) == HashMapTableModel.DIRECTORY_VALUE_TYPE) {
	    editor = new PathSelectorComponent((String) value);
	} else if (model.getValueTypeAt(row) == HashMapTableModel.FILE_VALUE_TYPE) {
	    editor = new PathSelectorComponent((String) value, false);
	} else if (model.getValueTypeAt(row) == HashMapTableModel.EMPTY_VALUE_TYPE) {
	    editor = new JLabel("EMPTY");
	} else {
	    editor = new JTextField((String) value);
	}

	return editor;
    }

    /*
     * CellEditor interface methods
     */
    /**
     * Returns the value contained in the editor.
     * @return the value contained in the editor
     */
    public Object getCellEditorValue() {
	if (editor instanceof PathSelectorComponent) {
	    return ((PathSelectorComponent) editor).getValue();
	} else if (editor instanceof JLabel) {
	    return null;
	}
	return ((JTextField) editor).getText();
    }

    /**
     * Asks the editor if it can start editing using <code>anEvent</code>.
     * <code>anEvent</code> is in the invoking component coordinate system.
     * The editor can not assume the Component returned by
     * <code>getCellEditorComponent</code> is installed.  This method
     * is intended for the use of client to avoid the cost of setting up
     * and installing the editor component if editing is not possible.
     * If editing can be started this method returns true.
     * 
     * @param	anEvent		the event the editor should use to consider
     *				whether to begin editing or not
     * @return	true if editing can be started
     * @see #shouldSelectCell
     */
    public boolean isCellEditable(EventObject anEvent) {
	return true;
    }

    /**
     * Returns true if the editing cell should be selected, false otherwise.
     * Typically, the return value is true, because is most cases the editing
     * cell should be selected.  However, it is useful to return false to
     * keep the selection from changing for some types of edits.
     * eg. A table that contains a column of check boxes, the user might
     * want to be able to change those checkboxes without altering the
     * selection.  (See Netscape Communicator for just such an example) 
     * Of course, it is up to the client of the editor to use the return
     * value, but it doesn't need to if it doesn't want to.
     *
     * @param	anEvent		the event the editor should use to start
     *				editing
     * @return	true if the editor would like the editing cell to be selected;
     *    otherwise returns false
     * @see #isCellEditable
     */
    public boolean shouldSelectCell(EventObject anEvent) {
	return true;
    }

    /**
     * Tells the editor to stop editing and accept any partially edited
     * value as the value of the editor.  The editor returns false if
     * editing was not stopped; this is useful for editors that validate
     * and can not accept invalid entries.
     *
     * @return	true if editing was stopped; false otherwise
     */
    public boolean stopCellEditing() {
	fireEditingStopped(); 
	return true;
    }

    /**
     * Tells the editor to cancel editing and not accept any partially
     * edited value.
     */
    public void cancelCellEditing() {
	fireEditingCanceled(); 
    }

    /**
     * Adds a listener to the list that's notified when the editor 
     * stops, or cancels editing.
     *
     * @param	l		the CellEditorListener
     */  
    public void addCellEditorListener(CellEditorListener l) {
	listenerList.add(CellEditorListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified
     *
     * @param	l		the CellEditorListener
     */  
    public void removeCellEditorListener(CellEditorListener l) {
	listenerList.remove(CellEditorListener.class, l);
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is created lazily.
     *
     * @see EventListenerList
     */
    protected void fireEditingStopped() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
	    }	       
	}
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is created lazily.
     *
     * @see EventListenerList
     */
    protected void fireEditingCanceled() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
	    }	       
	}
    }

}
