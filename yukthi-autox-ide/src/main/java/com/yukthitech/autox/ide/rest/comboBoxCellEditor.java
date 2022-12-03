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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class comboBoxCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{

	private String type;
	private List<String> typelist;
	private JComboBox<String> jComboBox=null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public comboBoxCellEditor(List<String> typelist)
	{
		// TODO Auto-generated constructor stub
		this.typelist=typelist;
	}
	
	@Override
	public Object getCellEditorValue()
	{
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		if(value instanceof String) {
			this.type=(String) value;
		}
		JComboBox combo = new JComboBox<String>();
		for(String type:typelist) {
			combo.addItem(type);
		}
		combo.setSelectedItem(type);
		combo.addActionListener(this);
		return jComboBox;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JComboBox<String> combo = (JComboBox<String>) e.getSource();
		this.type=(String) combo.getSelectedItem();
	}

	

}
