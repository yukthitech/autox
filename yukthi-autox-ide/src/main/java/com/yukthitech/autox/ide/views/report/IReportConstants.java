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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * Constants related to reports.
 * @author akiran
 */
public interface IReportConstants
{
	/**
	 * Background color for test suite.
	 */
	public Color TEST_SUITE_BG = new Color(133, 133, 173);

	/**
	 * Background color for test suite.
	 */
	public Color TEST_CASE_BG = new Color(174, 174, 210);
	
	public Font BIG_FONT = new Font(Font.DIALOG, Font.BOLD, 14);
	
	public default void setDefaultStyle(Object row, boolean isSelected, Component component)
	{
		if(row instanceof TestSuiteRow)
		{
			component.setBackground(IReportConstants.TEST_SUITE_BG);
			component.setForeground(Color.white);
			component.setFont(IReportConstants.BIG_FONT);
		}
		else if(row instanceof TestCaseRow)
		{
			component.setBackground(IReportConstants.TEST_CASE_BG);
			component.setForeground(Color.white);
			component.setFont(IReportConstants.BIG_FONT);
		}
		else
		{
			component.setBackground(Color.white);
			component.setForeground(Color.black);
		}
	}
}
