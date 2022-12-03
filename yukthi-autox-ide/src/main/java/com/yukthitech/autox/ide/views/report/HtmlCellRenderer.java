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
package com.yukthitech.autox.ide.views.report;

import java.awt.Component;

import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.html.HTMLDocument;

import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

public class HtmlCellRenderer extends DefaultTableCellRenderer implements IReportConstants
{
	private static final long serialVersionUID = 1L;
	
	private ExecutionEnvironment activeEnvironment;
	
	private boolean minWidth;
	
	public HtmlCellRenderer(boolean minWidth)
	{
		this.minWidth = minWidth;
	}
	
	public void setActiveEnvironment(ExecutionEnvironment activeEnvironment)
	{
		this.activeEnvironment = activeEnvironment;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Object rowBean = ((ReportTreeTableModel)table.getModel()).getRow(row);
		
		//JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		
		if(isSelected)
		{
			editorPane.setBackground(table.getSelectionBackground());
			editorPane.setForeground(table.getSelectionBackground());
		}
		else
		{
			setDefaultStyle(rowBean, isSelected, editorPane);
		}

		String finalHtml = value == null ? "" : "" + value;
		finalHtml = "<html><body style=\"padding: 3px;\">" + finalHtml + "</body></html>";

		editorPane.setText(finalHtml);
		
		if(finalHtml.contains("Screen shot"))
		{
			System.out.println(finalHtml);
		}
		
		try
		{
			if(activeEnvironment != null)
			{
				((HTMLDocument)editorPane.getDocument()).setBase(activeEnvironment.getReportFolder().toURI().toURL());
			}
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while setting base folder", ex);
		}
		//label.setText(finalHtml);
		
		//table.setPreferredScrollableViewportSize(table.getPreferredSize());
		int prefHeight = editorPane.getPreferredSize().height;
		int defHeight = table.getRowHeight(row);
		
		if(prefHeight > defHeight)
		{
			table.setRowHeight(row, prefHeight);
		}
		
		return editorPane;
	}

}
