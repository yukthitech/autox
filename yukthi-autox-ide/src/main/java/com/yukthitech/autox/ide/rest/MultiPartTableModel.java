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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class MultiPartTableModel extends DefaultTableModel
{
	List<MultiPart> multiPartList = new ArrayList<>();
	private static final long serialVersionUID = 1L;
	
	private String[] ColumnNames= {"Type","Name","Content Type", "Value"};
	

	@Override
	public int getColumnCount()
	{
		// TODO Auto-generated method stub
		return ColumnNames.length;
	}
 	
	

	@Override
	public String getColumnName(int column)
	{
		// TODO Auto-generated method stub
		return ColumnNames[column];
	}



//	public void addRow(MultiPart multiPart)
//	{
//		
//		// TODO Auto-generated method stub
//		Object[] rowData=new Object[4];
//		rowData[0]=new JComboBox<String>(new String[] {"raw","file"});
//		rowData[1]=multiPart.getName();
//		rowData[2]=multiPart.getContentType();
//		rowData[3]=multiPart.getValue();
//		multiPartList.add(new MultiPart(((JComboBox)rowData[0]).getSelectedItem().toString(), multiPart.getName(), multiPart.getContentType(), multiPart.getValue()));
//		super.addRow(rowData);		
//	}


	
	@Override
	public void setValueAt(Object aValue, int row, int column)
	{
		switch(column) {
			case 0:
				super.setValueAt(aValue, row, column);
				break;
			case 1:
				super.setValueAt(aValue, row, column);
				break;
			case 2:
				super.setValueAt(aValue, row, column);
				break;
			case 3:
				super.setValueAt(aValue, row, column);
				break;
		}
		
	}



	



//	@Override
//	public Object getValueAt(int row, int column)
//	{
//		MultiPart part = multiPartList.get(row);
//		Object returnValue=null;
//		switch(column) {
//			case 0:
//				returnValue = combo;
//				break;
//			case 1:
//				returnValue =part.getName();
//				break;
//			case 2:
//				returnValue =part.getContentType();
//				break;
//			case 3:
//				returnValue= part.getValue();
//				break;
//		}
//		return returnValue;
//	}
	
	
}