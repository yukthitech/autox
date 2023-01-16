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

import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.search.xml.XmlElement;
import com.yukthitech.autox.ide.state.PersistableState;
import com.yukthitech.autox.ide.swing.IdeDialogPanel;
import com.yukthitech.swing.HtmlPanel;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.doc.ClassDoc;
import com.yukthitech.utils.doc.DocInfoGenerator;

@Component
@PersistableState(directState = true, fields = true)
public class SearchDialog extends IdeDialogPanel
{
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private final JPanel contentPanel = new JPanel();
	
	@PersistableState
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	@PersistableState(fields = true, directState = false)
	private final TextSearchPanel textSearchPanel = new TextSearchPanel();
	
	@PersistableState(fields = true, directState = false)
	private final XmlSearchPanel xmlSearchPanel = new XmlSearchPanel();
	
	private HtmlPanel helpPanel = new HtmlPanel();
	
	public SearchDialog()
	{
		super.setTitle("File Search");
		super.setDialogBounds(100, 100, 521, 328);
		
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		contentPanel.add(tabbedPane, BorderLayout.CENTER);
		
		tabbedPane.addTab("Text Search", null, textSearchPanel, null);
		tabbedPane.addTab("Xml Search", null, xmlSearchPanel, null);
		tabbedPane.addTab("Help", null, helpPanel, null);
		
		ClassDoc xmlElemDoc = DocInfoGenerator.generateClassDoc(XmlElement.class);
		
		helpPanel.setContentProcessor(content -> 
		{
			return FreeMarkerMethodManager.replaceExpressions("/help/search-help.html", CommonUtils.toMap("xmlElement", xmlElemDoc), content);
		});

		helpPanel.setResource("/help/search-help.html");
	}
	
	@PostConstruct
	private void init()
	{
		IdeUtils.autowireBean(applicationContext, textSearchPanel);
		IdeUtils.autowireBean(applicationContext, xmlSearchPanel);
	}
	
	public void display()
	{
		textSearchPanel.resetForDisplay(tabbedPane.getSelectedIndex() == 0);
		xmlSearchPanel.resetForDisplay(tabbedPane.getSelectedIndex() == 1);
		
		super.displayInDialog();
	}
}
