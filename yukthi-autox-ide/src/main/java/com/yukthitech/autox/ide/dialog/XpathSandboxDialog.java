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
package com.yukthitech.autox.ide.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.charset.Charset;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.utils.CommonUtils;

import java.awt.FlowLayout;

@Component
public class XpathSandboxDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private ObjectMapper objectMapper = new ObjectMapper();

	private final JPanel contentPanel = new JPanel();
	private final JSplitPane splitPane = new JSplitPane();
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	private final JLabel lblContextJsonFor = new JLabel("Context Json for Evalutation: ");
	private final JPanel panel_2 = new JPanel();
	private final JButton btnFormat = new JButton("Format");
	private final RTextScrollPane textScrollPane = new RTextScrollPane();
	private final RSyntaxTextArea contextJsonFld = new RSyntaxTextArea();
	private final JPanel panel_3 = new JPanel();
	private final JPanel panel_4 = new JPanel();
	private final JLabel lblPleaseProvideXpath = new JLabel("Please Provide Xpath To Evaluate: ");
	private final RTextScrollPane textScrollPane_1 = new RTextScrollPane();
	private final RSyntaxTextArea outputJsonFld = new RSyntaxTextArea();
	private final JTextField xpathFld = new JTextField();
	private final JButton btnEvaluate = new JButton("Evaluate");
	private final JCheckBox multiValuedCbox = new JCheckBox("Multi Valued");
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final JPanel panel_5 = new JPanel();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextPane helpPane = new JTextPane();

	private String htmlContent;
	private final JPanel panel_6 = new JPanel();
	private final JButton btnNewButton = new JButton("Remove");

	/**
	 * Create the dialog.
	 */
	public XpathSandboxDialog()
	{
		try
		{
			htmlContent = IOUtils.toString(XpathSandboxDialog.class.getResourceAsStream("/help/xpath-help.html"), Charset.defaultCharset());
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while loading help content", ex);
		}

		setTitle("JXPath Sandbox");
		setBounds(100, 100, 958, 597);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		contentPanel.add(tabbedPane, BorderLayout.CENTER);
		xpathFld.setMargin(new Insets(5, 5, 5, 5));
		xpathFld.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					evaluateXpath();
				}
			}
		});
		xpathFld.setFont(new Font("Tahoma", Font.BOLD, 11));
		xpathFld.setColumns(10);
		tabbedPane.addTab("Evaluator", null, splitPane, null);
		splitPane.setResizeWeight(0.3);

		splitPane.setLeftComponent(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.anchor = GridBagConstraints.WEST;
		gbc_panel_1.fill = GridBagConstraints.VERTICAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel_1.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 145, 0 };
		gbl_panel_1.rowHeights = new int[] { 14, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		GridBagConstraints gbc_lblContextJsonFor = new GridBagConstraints();
		gbc_lblContextJsonFor.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblContextJsonFor.gridx = 0;
		gbc_lblContextJsonFor.gridy = 0;
		panel_1.add(lblContextJsonFor, gbc_lblContextJsonFor);

		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 0;
		panel.add(panel_2, gbc_panel_2);
		btnFormat.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				formatContextJson();
			}
		});

		panel_2.add(btnFormat);

		GridBagConstraints gbc_textScrollPane = new GridBagConstraints();
		gbc_textScrollPane.gridwidth = 2;
		gbc_textScrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_textScrollPane.fill = GridBagConstraints.BOTH;
		gbc_textScrollPane.gridx = 0;
		gbc_textScrollPane.gridy = 1;
		panel.add(textScrollPane, gbc_textScrollPane);
		contextJsonFld.setText("{\r\n\t\"attr\" : {\r\n\t\t\"attrName\": {} \r\n\t}\r\n}");

		contextJsonFld.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
		contextJsonFld.setCodeFoldingEnabled(true);

		textScrollPane.setViewportView(contextJsonFld);

		splitPane.setRightComponent(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		panel_4.setBorder(new EmptyBorder(5, 5, 5, 5));

		panel_3.add(panel_4, BorderLayout.NORTH);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 168, 0, 0 };
		gbl_panel_4.rowHeights = new int[] { 14, 0, 0, 0 };
		gbl_panel_4.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);

		GridBagConstraints gbc_lblPleaseProvideXpath = new GridBagConstraints();
		gbc_lblPleaseProvideXpath.insets = new Insets(0, 0, 5, 5);
		gbc_lblPleaseProvideXpath.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblPleaseProvideXpath.gridx = 0;
		gbc_lblPleaseProvideXpath.gridy = 0;
		panel_4.add(lblPleaseProvideXpath, gbc_lblPleaseProvideXpath);

		GridBagConstraints gbc_xpathFld = new GridBagConstraints();
		gbc_xpathFld.gridwidth = 2;
		gbc_xpathFld.insets = new Insets(0, 10, 5, 0);
		gbc_xpathFld.fill = GridBagConstraints.HORIZONTAL;
		gbc_xpathFld.gridx = 0;
		gbc_xpathFld.gridy = 1;
		panel_4.add(xpathFld, gbc_xpathFld);

		GridBagConstraints gbc_multiValuedCbox = new GridBagConstraints();
		gbc_multiValuedCbox.anchor = GridBagConstraints.WEST;
		gbc_multiValuedCbox.insets = new Insets(0, 0, 0, 5);
		gbc_multiValuedCbox.gridx = 0;
		gbc_multiValuedCbox.gridy = 2;
		panel_4.add(multiValuedCbox, gbc_multiValuedCbox);

		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 1;
		gbc_panel_6.gridy = 2;
		FlowLayout flowLayout = (FlowLayout) panel_6.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setVgap(0);
		panel_4.add(panel_6, gbc_panel_6);
		panel_6.add(btnEvaluate);
		btnNewButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				removeByXpath();
			}
		});

		panel_6.add(btnNewButton);
		btnEvaluate.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				evaluateXpath();
			}
		});

		panel_3.add(textScrollPane_1, BorderLayout.CENTER);
		outputJsonFld.setEditable(false);
		outputJsonFld.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
		outputJsonFld.setCodeFoldingEnabled(true);

		textScrollPane_1.setViewportView(outputJsonFld);

		tabbedPane.addTab("Help", null, panel_5, null);
		panel_5.setLayout(new BorderLayout(0, 0));

		panel_5.add(scrollPane, BorderLayout.CENTER);
		helpPane.setContentType("text/html");
		helpPane.setEditable(false);
		helpPane.setText(htmlContent);

		scrollPane.setViewportView(helpPane);
	}

	private void evaluateXpath()
	{
		String expression = xpathFld.getText().trim();

		if(StringUtils.isBlank(expression))
		{
			JOptionPane.showMessageDialog(this, "Please provide xpath and then try!");
			return;
		}

		Object ctx = parseContext();

		if(ctx == null)
		{
			return;
		}

		boolean isMulti = multiValuedCbox.isSelected();
		Object result = null;

		try
		{
			if(isMulti)
			{
				result = JXPathContext.newContext(ctx).selectNodes(expression);
			}
			else
			{
				result = JXPathContext.newContext(ctx).getValue(expression);
			}
		} catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Failed to evaluate specified xpath.\nError: " + ex);
		}

		try
		{
			outputJsonFld.setText(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
		} catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Failed to convert result into json.\nError: " + ex);
		}
	}

	private void removeByXpath()
	{
		String expression = xpathFld.getText().trim();

		if(StringUtils.isBlank(expression))
		{
			JOptionPane.showMessageDialog(this, "Please provide xpath and then try!");
			return;
		}

		Object ctx = parseContext();

		if(ctx == null)
		{
			return;
		}

		boolean isMulti = multiValuedCbox.isSelected();

		try
		{
			if(isMulti)
			{
				JXPathContext.newContext(ctx).removeAll(expression);
			}
			else
			{
				JXPathContext.newContext(ctx).removePath(expression);
			}
		} catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Failed to execute remove-op with specified xpath.\nError: " + ex);
		}

		try
		{
			outputJsonFld.setText(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ctx));
		} catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Failed to convert result into json.\nError: " + ex);
		}
	}

	private Object parseContext()
	{
		String jsonContent = contextJsonFld.getText();

		if(StringUtils.isBlank(jsonContent))
		{
			JOptionPane.showMessageDialog(this, "Please provide context json and then try!");
			return null;
		}

		try
		{
			Object ctx = objectMapper.readValue(jsonContent, Object.class);
			return ctx;
		} catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Failed to parse context json. Please correct the error and then try.\n" + "Error: " + ex);
		}

		return null;
	}

	private void formatContextJson()
	{
		Object ctx = parseContext();

		if(ctx == null)
		{
			return;
		}

		try
		{
			String formatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ctx);
			contextJsonFld.setText(formatedJson);
		} catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Failed to format context json.\n" + "Error: " + ex);
		}
	}

	public void display()
	{
		super.setVisible(true);
	}

	public void display(String attrName, String value)
	{
		Object parsedValue = null;
		
		try
		{
			parsedValue = IAutomationConstants.OBJECT_MAPPER.readValue(value, Object.class);
		}catch(Exception ex)
		{
			parsedValue = value;
		}
		
		Object contextTree = CommonUtils.toMap("attr", CommonUtils.toMap(attrName, parsedValue));
		String formattedJson = "";
		
		try
		{
			formattedJson = IAutomationConstants.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(contextTree);		
		}catch(Exception ex)
		{
			
		}
		
		contextJsonFld.setText(formattedJson);
		xpathFld.setText("/attr/" + attrName);
		
		super.setVisible(true);
	}
}
