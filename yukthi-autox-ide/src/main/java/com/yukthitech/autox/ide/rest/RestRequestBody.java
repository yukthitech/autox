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
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.model.Param;

@Component
public class RestRequestBody extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table1;

	private JPanel paramsPanel;
	private RSyntaxTextArea rSyntaxTextArea;
	private DefaultTableModel formDataModel;
	private ButtonGroup requeryType;
	public JRadioButton Params;
	JRadioButton raw_radioButton;
	public JRadioButton rdbtnMultipart;
	public JPanel multiPartPanel;
	private JScrollPane scrollPane_1;
	public JTable table;
	private MultiPartTableModel model;
	/**
	 * Create the panel.
	 */
	String type = "Params";

	public RestRequestBody()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 112, 112, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		Params = new JRadioButton("Params");
		Params.setSelected(true);
		Params.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				type = "Params";
				paramsPanel.setVisible(true);
			}
		});
		GridBagConstraints gbc_Params = new GridBagConstraints();
		gbc_Params.anchor = GridBagConstraints.WEST;
		gbc_Params.insets = new Insets(0, 0, 5, 5);
		gbc_Params.gridx = 0;
		gbc_Params.gridy = 1;
		add(Params, gbc_Params);

		raw_radioButton = new JRadioButton("raw");
		raw_radioButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				type = "raw";
				paramsPanel.setVisible(false);
				rSyntaxTextArea.setVisible(true);
			}
		});
		GridBagConstraints gbc_rdbtnParam = new GridBagConstraints();
		gbc_rdbtnParam.anchor = GridBagConstraints.WEST;
		gbc_rdbtnParam.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnParam.gridx = 1;
		gbc_rdbtnParam.gridy = 1;
		add(raw_radioButton, gbc_rdbtnParam);

		rdbtnMultipart = new JRadioButton("Multi-part");
		rdbtnMultipart.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				type = "multiPart";
				rSyntaxTextArea.setVisible(false);
				paramsPanel.setVisible(false);
				multiPartPanel.setVisible(true);
			}
		});
		GridBagConstraints gbc_rdbtnMultipart = new GridBagConstraints();
		gbc_rdbtnMultipart.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnMultipart.gridx = 2;
		gbc_rdbtnMultipart.gridy = 1;
		add(rdbtnMultipart, gbc_rdbtnMultipart);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		add(panel, gbc_panel);
		panel.setLayout(new CardLayout(0, 0));

		paramsPanel = new JPanel();
		panel.add(paramsPanel, "name_15379027355504");
		String columnNames[] = { "Key", "Value" };
		formDataModel = new DefaultTableModel(columnNames, 1);
		table1 = new JTable(formDataModel);
		table1.getTableHeader();
		paramsPanel.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane(table1);
		paramsPanel.add(scrollPane);

		table1.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(table1.getSelectedRow() == table1.getRowCount() - 1)
				{
					formDataModel.addRow(new Object[] { "", "" });
					table1.validate();
				}

			}
		});
		requeryType = new ButtonGroup();
		requeryType.add(Params);
		requeryType.add(raw_radioButton);
		requeryType.add(rdbtnMultipart);

		rSyntaxTextArea = new RSyntaxTextArea();
		panel.add(rSyntaxTextArea, "name_15370265096477");

		multiPartPanel = new JPanel();
		panel.add(multiPartPanel, "name_3355769031298");

		scrollPane_1 = new JScrollPane(getTable());
		multiPartPanel.add(scrollPane_1);

	}

	@PostConstruct
	public void init()
	{
		TableColumn col = table.getColumnModel().getColumn(0);
		String[] items = new String[] { "select","raw", "file", "template" };
		col.setCellEditor(new DefaultCellEditor(new JComboBox<>(items)));
		//col.setCellRenderer(new ComboBoxCellRenderer());

		TableColumn valueCol = table.getColumnModel().getColumn(3);
		valueCol.setCellEditor(new TextAreaCellEditor());
		valueCol.setCellRenderer(new TextAreaCellRenderer());
	}

	public String getRaw()
	{
		return rSyntaxTextArea.getText();
	}

	public String getRequestType()
	{
		return type;
	}

	public List<Param> getParamList()
	{
		List<Param> paramList = new ArrayList<>();
		for(int i = 0; i < formDataModel.getRowCount() - 1; i++)
		{
			String name = (String) formDataModel.getValueAt(i, 0);
			String value = (String) formDataModel.getValueAt(i, 1);
			System.out.println("name:" + name + "value:" + value);
			if(!name.trim().isEmpty())
			{
				paramList.add(new Param((String) name, (String) value));
			}
		}
		return paramList;
	}

	private JTable getTable()
	{
		if(table == null)
		{
			table = new JTable();
			model = new MultiPartTableModel();
			model.addRow(new Object[] {
					"raw"
					,"deepak","application/json","value"
			});
			table.setModel(model);
		}
		table.setRowHeight(50);
		return table;
	}

	public void hideMultiPart()
	{
		multiPartPanel.disable();
	}

	public void showMultiPart()
	{
		multiPartPanel.enable();
	}

	public List<MultiPart> getMultiPartList()
	{
		List<MultiPart> list = new ArrayList<>();
		MultiPart part;
		for(int i = 0; i < model.getRowCount(); i++)
		{
//			JComboBox obj = (JComboBox) table.getModel().getValueAt(i, 0);
//			String type = obj.getSelectedItem().toString();
//			System.out.println("selected item is"+type);
//			part = new MultiPart(type, model.getValueAt(i, 1).toString(), model.getValueAt(i, 2).toString(), model.getValueAt(i, 3).toString());
			part = new MultiPart(model.getValueAt(i, 0).toString(), model.getValueAt(i, 1).toString(), model.getValueAt(i, 2).toString(), model.getValueAt(i, 3).toString());
			list.add(part);
		}
		return list;
	}
}