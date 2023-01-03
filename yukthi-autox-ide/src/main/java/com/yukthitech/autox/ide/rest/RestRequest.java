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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.actions.RunActions;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.model.Project;

//@Component
public class RestRequest extends JPanel
{
	public RestRequest()
	{}

	private static final long serialVersionUID = 1L;

	@Autowired
	private RestRequestHeaders requestHeaders;

	@Autowired
	private RestRequestBody requestBody;

	@Autowired
	private RestRequestPathVariables restRequestPathVariables;

	@Autowired
	private RestStepTemplateManager restStepTemplateManager;

	@Autowired
	private IdeContext ideContext;

	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;

	@Autowired
	private RunActions runAction;

	private JComboBox<String> methodComboBox;

	private JTextField url;

	private JButton button;

	private JTabbedPane tabbedPane;

	private JLabel lblNewLabel;

	private JComboBox<String> projectsComboBox;

	private JCheckBox chckbxGenerateCode;

	private List<Project> projects = new ArrayList<>();

	/**
	 * Create the panel.
	 */
	@PostConstruct
	private void init()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 65, 112, 70, 59, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 23, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(getLblNewLabel(), gbc_lblNewLabel);
		GridBagConstraints gbc_projectsComboBox = new GridBagConstraints();
		gbc_projectsComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_projectsComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectsComboBox.gridx = 1;
		gbc_projectsComboBox.gridy = 0;
		add(getProjectsComboBox(), gbc_projectsComboBox);
		GridBagConstraints gbc_chckbxGenerateCode = new GridBagConstraints();
		gbc_chckbxGenerateCode.gridwidth = 2;
		gbc_chckbxGenerateCode.anchor = GridBagConstraints.WEST;
		gbc_chckbxGenerateCode.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxGenerateCode.gridx = 2;
		gbc_chckbxGenerateCode.gridy = 0;
		add(getChckbxGenerateCode(), gbc_chckbxGenerateCode);
		GridBagConstraints gbc_methodComboBox = new GridBagConstraints();
		gbc_methodComboBox.anchor = GridBagConstraints.WEST;
		gbc_methodComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_methodComboBox.gridx = 0;
		gbc_methodComboBox.gridy = 1;
		add(getMethodComboBox(), gbc_methodComboBox);
		GridBagConstraints gbc_url = new GridBagConstraints();
		gbc_url.gridwidth = 2;
		gbc_url.fill = GridBagConstraints.HORIZONTAL;
		gbc_url.insets = new Insets(0, 0, 5, 5);
		gbc_url.gridx = 1;
		gbc_url.gridy = 1;
		add(getUrl(), gbc_url);
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.anchor = GridBagConstraints.NORTHWEST;
		gbc_button.gridx = 3;
		gbc_button.gridy = 1;
		add(getButton(), gbc_button);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.gridwidth = 4;
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 2;
		add(getTabbedPane(), gbc_tabbedPane);
		this.setMinimumSize(new Dimension(10, 300));

	}

	private JComboBox<String> getMethodComboBox()
	{
		if(methodComboBox == null)
		{
			methodComboBox = new JComboBox<>();
			methodComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String method = methodComboBox.getSelectedItem().toString();
					if(!method.equals("Put")&&!method.equals("Post")) {
						requestBody.Params.setSelected(true);
						requestBody.rdbtnMultipart.setEnabled(false);
						requestBody.multiPartPanel.setEnabled(false);
					}
					else
					{
						requestBody.Params.setSelected(true);
						requestBody.rdbtnMultipart.setEnabled(true);
						requestBody.multiPartPanel.setEnabled(true);
					}
					if(methodComboBox.getSelectedItem().toString().equals("Get"))
					{
						tabbedPane.setSelectedIndex(0);
						tabbedPane.setEnabledAt(1, false);
					}
					else
					{
						tabbedPane.setSelectedIndex(0);
						tabbedPane.setEnabledAt(1, true);
					}
				}
			});
			methodComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "Get", "Post", "Put", "Delete" }));
		}
		return methodComboBox;
	}

	private JTextField getUrl()
	{
		if(url == null)
		{
			url = new JTextField();
			url.addFocusListener(new FocusAdapter()
			{
				@Override
				public void focusLost(FocusEvent e)
				{
					restRequestPathVariables.addQueryParam(url.getText());

				}
			});
			url.setColumns(10);
		}
		return url;
	}

	private JButton getButton()
	{
		if(button == null)
		{
			button = new JButton("Send");
			button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					RestInvocationModel ctx = new RestInvocationModel();

					ctx.setUri(url.getText());
					ctx.setHeaders(requestHeaders.getHeaderList());
					String stepName = "invoke";

					String method = methodComboBox.getSelectedItem().toString();
					stepName = stepName.concat(method);
					if(!method.toLowerCase().equals("get") && !method.toLowerCase().equals("delete"))
					{
						stepName = stepName.concat("With" + requestBody.getRequestType());
						if(requestBody.getRequestType().equals("raw"))
						{
							ctx.setRawBody(requestBody.getRaw());
						}
						else if(requestBody.getRequestType().equals("Params"))
						{
							ctx.setParamList(requestBody.getParamList());
						}
						else {
							ctx.setMultiPartlist(requestBody.getMultiPartList());
						}
					}
					else
					{
						ctx.setPathVariables(restRequestPathVariables.getPathVariables());
					}
					System.out.println(stepName);

					String code = restStepTemplateManager.generateStep(stepName, ctx);
					code = code.replace(">>]", "]]>");

					System.out.println("-------------Code------------------------");
					System.out.println(code);
					System.out.println("-------------------------------------");

					runAction.executeStepCode(code, projects.get(projectsComboBox.getSelectedIndex()), null);

					if(chckbxGenerateCode.isSelected())
					{

						FileEditor editor = ((FileEditor) fileEditorTabbedPane.getSelectedComponent());
						code = code.replace("<![CDATA[", " ");
						code = code.replace("]]>]", " ");
						editor.insertStepCode(code);

					}

				}
			});
		}
		return button;
	}

	private JTabbedPane getTabbedPane()
	{
		if(tabbedPane == null)
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addTab("Header", null, getRestHeaders(), null);
			tabbedPane.addTab("Body", null, getPostmanBody(), null);
			tabbedPane.addTab("Path Variables", null, getRestRequestPathVariables(), null);
		}
		return tabbedPane;
	}

	private RestRequestHeaders getRestHeaders()
	{
		if(requestHeaders == null)
		{
			requestHeaders = new RestRequestHeaders();
		}
		return requestHeaders;
	}

	private RestRequestBody getPostmanBody()
	{
		if(requestBody == null)
		{
			requestBody = new RestRequestBody();
		}
		return requestBody;
	}

	private JLabel getLblNewLabel()
	{
		if(lblNewLabel == null)
		{
			lblNewLabel = new JLabel("Projects");
		}
		return lblNewLabel;
	}

	private JComboBox<String> getProjectsComboBox()
	{
		if(projectsComboBox == null)
		{
			projectsComboBox = new JComboBox<String>();
		}
		return projectsComboBox;
	}

	private JCheckBox getChckbxGenerateCode()
	{
		if(chckbxGenerateCode == null)
		{
			chckbxGenerateCode = new JCheckBox("Generate Code");
		}
		return chckbxGenerateCode;
	}

	public void addProject(Project project)
	{
		projects.add(project);
		projectsComboBox.addItem(project.getName());
	}

	private RestRequestPathVariables getRestRequestPathVariables()
	{
		if(restRequestPathVariables == null)
		{
			restRequestPathVariables = new RestRequestPathVariables();
		}
		return restRequestPathVariables;
	}
}
