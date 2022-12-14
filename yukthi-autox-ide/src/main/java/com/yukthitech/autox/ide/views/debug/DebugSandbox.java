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
package com.yukthitech.autox.ide.views.debug;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.border.EtchedBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.yukthitech.swing.YukthiHtmlPane;

public class DebugSandbox extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final JPanel panel = new JPanel();
	private final JButton btnNewButton = new JButton("Clear");
	private final JSplitPane splitPane = new JSplitPane();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JTextArea expressionPane = new JTextArea();
	private final YukthiHtmlPane exprHistoryPane = new YukthiHtmlPane();

	/**
	 * Create the panel.
	 */
	public DebugSandbox()
	{
		setLayout(new BorderLayout(0, 0));
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		add(panel, BorderLayout.NORTH);
		
		panel.add(btnNewButton);
		splitPane.setResizeWeight(0.75);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		add(splitPane, BorderLayout.CENTER);
		
		splitPane.setRightComponent(scrollPane);
		
		scrollPane.setViewportView(expressionPane);
		
		splitPane.setLeftComponent(scrollPane_1);
		
		scrollPane_1.setViewportView(exprHistoryPane);

	}

}
