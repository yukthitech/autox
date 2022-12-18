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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;

@Component
public class RestResponse extends JPanel
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(ProjectExplorer.class);

	@Autowired
	private UiLayout uiLayout;

	@Autowired
	private ActionCollection actionCollection;

	@Autowired
	private IdeContext ideContext;

	private JPopupMenu responsePopup;

	private ExecutionEnvironment activeEnvironment;
	private JLabel lblResponse;
	private JLabel lblStatusCode;
	private JScrollPane scrollPane;
	private JTree tree;

	/**
	 * Create the panel.
	 */
	public RestResponse()
	{
		logger.info("post construct init method called");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 336, 156, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 220, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		GridBagConstraints gbc_lblResponse = new GridBagConstraints();
		gbc_lblResponse.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblResponse.insets = new Insets(0, 0, 5, 5);
		gbc_lblResponse.gridx = 0;
		gbc_lblResponse.gridy = 0;
		add(getLblResponse(), gbc_lblResponse);
		GridBagConstraints gbc_lblStatusCode = new GridBagConstraints();
		gbc_lblStatusCode.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblStatusCode.insets = new Insets(0, 0, 5, 0);
		gbc_lblStatusCode.gridx = 1;
		gbc_lblStatusCode.gridy = 0;
		add(getLblStatusCode(), gbc_lblStatusCode);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(getScrollPane(), gbc_scrollPane);
//		GridBagConstraints gbc_textArea = new GridBagConstraints();
//		gbc_textArea.fill = GridBagConstraints.BOTH;
//		gbc_textArea.gridx = 0;
//		gbc_textArea.gridy = 2;
//		add(getTextArea(), gbc_textArea);

	}

	@PostConstruct
	public void init()
	{
		//responsePopup = uiLayout.getPopupMenu("restResponsePopup").toPopupMenu(actionCollection);
//		textArea.setPopupMenu(responsePopup);

		/*
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void activeEnvironmentChanged(ExecutionEnvironment activeEnvironment)
			{

				RestResponse.this.activeEnvironment = activeEnvironment;
			}

			@Override
			public void environmentChanged(EnvironmentEvent event)
			{
				if(activeEnvironment != event.getEnvironment() || event.getEventType() != EnvironmentEventType.CONTEXT_ATTRIBUTE_ADDED)
				{
					return;
				}
				ContextAttributeDetails ctx = event.getNewContextAttribute();
				if(ctx.getName().equals("response"))
				{
					// String json = "{\"name\":\"mkyong\"}";
					System.out.println(ctx.getValue());
					System.out.println(ctx.getValue().getClass());
					Gson gson = new Gson();
					String json =gson.toJson(ctx.getValue(),ctx.getValue().getClass());
					System.out.println(json.toString());
					JsonObject jsonobj = null;
					try
					{
						JsonParser parser = new JsonParser();
						jsonobj = (JsonObject) parser.parse(json);
					} catch(JsonSyntaxException ex)
					{
						System.out.println("NOt a valid Json");
					}
					ObjectMapper mapper = new ObjectMapper();
					try
					{
						JsonNode rootJsonNode = mapper.readTree(json);
						System.out.println(rootJsonNode.toString());
						DefaultMutableTreeNode root = buildTree("{}", rootJsonNode);
						tree.setModel(new DefaultTreeModel(root));
					} catch(IOException e)
					{
						e.printStackTrace();
					}
					
				}
				if(ctx.getName().equals("result"))
				{
					RestResult result = (RestResult) ctx.getValue();
					lblStatusCode.setText("status Code: " + result.getStatusCode());

					String json = result.getValue().toString();
					JsonObject jsonobj = null;
					try
					{
						JsonParser parser = new JsonParser();
						jsonobj = (JsonObject) parser.parse(json);
//						textArea.setText(json);
					} catch(JsonSyntaxException ex)
					{
						System.out.println("NOt a valid Json");
					}
					
				}
			}

			private DefaultMutableTreeNode generateTree(String string, String json)
			{
				DefaultMutableTreeNode root=null;
				JsonParser parser = new JsonParser();
				JsonObject obj = (JsonObject) parser.parse(json);
				if(obj.isJsonObject()) {
					root=new DefaultMutableTreeNode(obj.toString());
				}
				return root;
			}
		});
	*/
	}

//	private RSyntaxTextArea getTextArea()
//	{
//		if(textArea == null)
//		{
//			textArea = new RSyntaxTextArea();
//		}
//		String json = "{" + "price: " + "{ " + "type: 'number'," + "minimum: 0," + "'exclusiveMinimum': true" + " }" + "}";
//		try
//		{
//			JsonParser parser = new JsonParser();
//			parser.parse(json);
//			textArea.setText(json);
//		} catch(JsonSyntaxException ex)
//		{
//			System.out.println("NOt a valid Json");
//		}
//
//		return textArea;
//	}

	private JLabel getLblResponse()
	{
		if(lblResponse == null)
		{
			lblResponse = new JLabel("Response");
		}
		return lblResponse;
	}

	private JLabel getLblStatusCode()
	{
		if(lblStatusCode == null)
		{
			lblStatusCode = new JLabel("status Code:");
		}
		return lblStatusCode;
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTree());
		}
		return scrollPane;
	}

	private JTree getTree()
	{
		if(tree == null)
		{
			tree = new JTree(new DefaultMutableTreeNode("{}"));
		}
		return tree;
	}

	private DefaultMutableTreeNode buildTree(String name, JsonNode node)
	{
		
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(name);

		Iterator<Entry<String, JsonNode>> it = node.fields();
		while(it.hasNext())
		{
			Entry<String, JsonNode> entry = it.next();
			treeNode.add(buildTree(entry.getKey(), entry.getValue()));
		}

		if(node.isArray())
		{
			for(int i = 0; i < node.size(); i++)
			{
				JsonNode child = node.get(i);
				if(child.isValueNode())
					treeNode.add(new DefaultMutableTreeNode(child.asText()));
				else
					treeNode.add(buildTree(String.format("[%d]", i), child));
			}
		}
		else if(node.isValueNode())
		{
			treeNode.add(new DefaultMutableTreeNode(node.asText()));
		}

		return treeNode;
	}

}
