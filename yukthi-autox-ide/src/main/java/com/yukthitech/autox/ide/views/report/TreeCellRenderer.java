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
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.yukthitech.autox.ide.IdeUtils;

public class TreeCellRenderer extends DefaultTableCellRenderer implements IReportConstants
{
	private static final long serialVersionUID = 1L;
	
	private static final EmptyBorder DEF_BORDER = new EmptyBorder(3, 3, 3, 3);
	private static final EmptyBorder TEST_CASE_BORDER = new EmptyBorder(3, 10, 3, 3);
	private static final EmptyBorder LOG_BORDER = new EmptyBorder(3, 30, 3, 3);
	
	private static ImageIcon MINIMIZED_ICON  = IdeUtils.loadIcon("/ui/icons/minimized.svg", 10);
	private static ImageIcon EXPANDED_ICON  = IdeUtils.loadIcon("/ui/icons/expanded.svg", 10);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Object rowBean = ((ReportTreeTableModel)table.getModel()).getRow(row);
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
		label.setBorder(DEF_BORDER);
		
		if(!isSelected)
		{
			setDefaultStyle(rowBean, isSelected, label);
		}
		
		if(rowBean instanceof MinimizableRow)
		{
			if(rowBean instanceof TestCaseRow)
			{
				label.setBorder(TEST_CASE_BORDER);
			}
			
			MinimizableRow<?> rowVal = (MinimizableRow<?>) rowBean;
			
			if(rowVal.isMinimized())
			{
				label.setIcon(MINIMIZED_ICON);
			}
			else
			{
				label.setIcon(EXPANDED_ICON);
			}
		}
		else
		{
			String finalHtml = value == null ? "" : "" + value;
			finalHtml = "<html><body>" + finalHtml + "</body></html>";

			label.setIcon(null);
			label.setBorder(LOG_BORDER);
			label.setText(finalHtml);
		}
		
		return label;
	}

}
