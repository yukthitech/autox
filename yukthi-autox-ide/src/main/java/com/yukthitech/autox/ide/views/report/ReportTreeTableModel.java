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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.debug.common.MonitorLogServerMssg;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

public class ReportTreeTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;

	private final static String[] columnNames = { "", "Message"};
	
	private static final String GLOBAL_SETUP = "_global_setup";
	
	private static final String GLOBAL_CLEANUP = "_global_cleanup";

	private List<TestSuiteRow> testSuiteLst = new ArrayList<>();
	
	private Map<String, TestSuiteRow> testSuiteMap = new HashMap<>();

	private List<Object> rows = new ArrayList<>();

	public synchronized void reload(ExecutionEnvironment newEnv)
	{
		testSuiteLst.clear();
		testSuiteMap.clear();
		
		if(newEnv != null)
		{
			for(MonitorLogServerMssg log : newEnv.getReportMessages())
			{
				addLog(log, false);
			}
		}
		
		refreshRows();
	}
	
	private void addTestSuiteRow(int index, TestSuiteRow testSuiteRow)
	{
		this.testSuiteLst.add(index, testSuiteRow);
		this.testSuiteMap.put(testSuiteRow.getName(), testSuiteRow);
	}
	
	public void addLog(MonitorLogServerMssg log, boolean refresh)
	{
		String testSuiteName = log.getTestSuite();
		String testCaseName = log.getTestCase();
		String dataName = log.getTestDataName();
		
		if(testSuiteName == null)
		{
			if(log.isSetup())
			{
				testSuiteName = GLOBAL_SETUP;
				testCaseName = "_setup";
			}
			else if(log.isCleanup())
			{
				testSuiteName = GLOBAL_CLEANUP;
				testCaseName = "_cleanup";
			}
			else
			{
				return;
			}
		}
		
		if(StringUtils.isBlank(testCaseName))
		{
			if(log.isSetup())
			{
				testCaseName = "_setup";
			}
			else if(log.isCleanup())
			{
				testCaseName = "_cleanup";
			}
		}
		
		if(dataName != null)
		{
			testCaseName = testCaseName + " [" + dataName + "]";
		}
		
		TestSuiteRow testSuiteRow = testSuiteMap.get(testSuiteName);
		
		if(testSuiteRow == null)
		{
			if(GLOBAL_SETUP.equals(testSuiteName))
			{
				testSuiteRow = new TestSuiteRow(GLOBAL_SETUP);
				addTestSuiteRow(0, testSuiteRow);
			}
			else if(GLOBAL_CLEANUP.equals(testSuiteName))
			{
				testSuiteRow = new TestSuiteRow(GLOBAL_CLEANUP);
				addTestSuiteRow(this.testSuiteLst.size(), testSuiteRow);
			}
			else
			{
				testSuiteRow = new TestSuiteRow(testSuiteName);
				addTestSuiteRow(this.testSuiteLst.size(), testSuiteRow);
			}
		}
		
		testSuiteRow.addLog(testCaseName, log.getMessage());
		
		if(refresh)
		{
			refreshRows();
		}
	}
	
	public synchronized void refreshRows()
	{
		List<Object> newRows = new ArrayList<>(rows.size());
		
		for(TestSuiteRow testSuite : this.testSuiteLst)
		{
			testSuite.populateChildRows(object ->
			{
				newRows.add(object);
			});
		}
		
		this.rows.clear();
		this.rows = newRows;
		
		super.fireTableDataChanged();
	}
	
	@Override
	public int getRowCount()
	{
		return rows.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}
	
	public Object getRow(int row)
	{
		return rows.get(row);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object row = rows.get(rowIndex);
		
		if(!(row instanceof IReportRow))
		{
			return "";
		}
		
		IReportRow reportRow = (IReportRow) row;
		return reportRow.getValueAt(columnIndex);
	}

}
