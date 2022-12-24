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
package com.yukthitech.autox.ide.search;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.swing.EscapableDialog;

@Component
public class SearchDialog extends EscapableDialog
{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private final JPanel contentPanel = new JPanel();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final TextSearchPanel textSearchPanel = new TextSearchPanel();
	private final XmlSearchPanel xmlSearchPanel = new XmlSearchPanel();
	
	public SearchDialog()
	{
		setTitle("File Search");
		setBounds(100, 100, 521, 328);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		contentPanel.add(tabbedPane, BorderLayout.CENTER);
		
		tabbedPane.addTab("Text Search", null, textSearchPanel, null);
		tabbedPane.addTab("Xml Search", null, xmlSearchPanel, null);
	}
	
	@PostConstruct
	private void init()
	{
		IdeUtils.autowireBean(applicationContext, textSearchPanel);
		IdeUtils.autowireBean(applicationContext, xmlSearchPanel);
	}
	
	public void display()
	{
		textSearchPanel.resetForDisplay();
		xmlSearchPanel.resetForDisplay();
		super.setVisible(true);
	}

}
