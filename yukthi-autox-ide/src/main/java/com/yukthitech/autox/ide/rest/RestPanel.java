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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSplitPane splitPane;
	
	@Autowired
	private RestRequest restRequest;
	
	@Autowired
	private RestResponse restResponse;

	/**
	 * Create the panel.
	 */
	public RestPanel()
	{

	}

	@PostConstruct
	public void init()
	{
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		add(getSplitPane());
	}

	private JSplitPane getSplitPane()
	{
		if(splitPane == null)
		{
			splitPane = new JSplitPane();
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setLeftComponent(restRequest);
			splitPane.setRightComponent(getPostmanResponse());
			splitPane.setMinimumSize(new Dimension(10, 10));
		}
		return splitPane;
	}

	private RestResponse getPostmanResponse()
	{
		return restResponse;
	}
}
