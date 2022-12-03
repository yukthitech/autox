/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.autox.ide.rest;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class TextAreaCellEditor implements TableCellEditor
{
	JTextArea textArea;
	JScrollPane scrollPane;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TextAreaCellEditor()
	{
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
		
	}
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		textArea.setText(value!=null?value.toString():"");
		return scrollPane;
	}

	@Override
	public Object getCellEditorValue()
	{
		// TODO Auto-generated method stub
		return textArea.getText();
	}

	@Override
	public boolean isCellEditable(EventObject anEvent)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean stopCellEditing()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void cancelCellEditing()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCellEditorListener(CellEditorListener l)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l)
	{
		// TODO Auto-generated method stub
		
	}

	

	
}
