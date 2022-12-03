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

import com.yukthitech.autox.exec.report.ExecutionLogData;

public class TestCaseRow extends MinimizableRow<LogReportRow> implements IReportRow
{
	private String name;

	public TestCaseRow(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void addLogEntry(ExecutionLogData.Message mssg)
	{
		String mssgHtml = mssg.getMessage();
		
		if(mssg instanceof ExecutionLogData.ImageMessage)
		{
			ExecutionLogData.ImageMessage imgMssg = (ExecutionLogData.ImageMessage) mssg;
			mssgHtml += String.format("<br/><img src='./logs/%s' style='width: 200px; border: 1px solid black;'/>", imgMssg.getImageFileName());
		}
		
		LogReportRow logRow = new LogReportRow(mssg.getLogLevel(), mssg.getSource(), mssgHtml, mssg.getTime());
		super.addChild(logRow);
	}
	
	@Override
	public Object getValueAt(int col)
	{
		return (col == 0) ? name : "";
	}
}
