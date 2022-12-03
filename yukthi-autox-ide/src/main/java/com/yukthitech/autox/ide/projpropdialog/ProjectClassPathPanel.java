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
package com.yukthitech.autox.ide.projpropdialog;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ide.model.Project;

public class ProjectClassPathPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JPanel panel_1;
	private JButton btnNewButton;
	private JButton btnAddClasspath;
	private JButton btnRemove;
	private JFileChooser addJarfileChooser = new JFileChooser();
	private JFileChooser addClassPathFileChooser;
	private DefaultListModel<String> listModel = new DefaultListModel<>();

	private Project project;

	private JList<String> list;
	private JScrollPane scrollPane;

	/**
	 * Create the panel.
	 */
	public ProjectClassPathPanel()
	{
		setLayout(new BorderLayout(0, 0));
		// add(getList_1(), BorderLayout.CENTER);
		add(getScrollPane(), BorderLayout.CENTER);
		add(getPanel_1(), BorderLayout.EAST);

		addJarfileChooser.setAcceptAllFileFilterUsed(false);
		
		addJarfileChooser.setFileFilter(new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return "*.jar *.zip";
			}

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() || f.getName().endsWith(".jar") || f.getName().endsWith(".zip");
			}
		});
		
		addClassPathFileChooser = new JFileChooser();
		addClassPathFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	private JPanel getPanel_1()
	{
		if(panel_1 == null)
		{
			panel_1 = new JPanel();
			panel_1.setBorder(new EmptyBorder(5, 5, 5, 5));
			GridBagLayout gbl_panel_1 = new GridBagLayout();
			gbl_panel_1.columnWidths = new int[] { 101, 0 };
			gbl_panel_1.rowHeights = new int[] {20, 20, 20};
			gbl_panel_1.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0 };
			panel_1.setLayout(gbl_panel_1);
			GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
			gbc_btnNewButton.gridx = 0;
			gbc_btnNewButton.gridy = 0;
			panel_1.add(getBtnNewButton(), gbc_btnNewButton);
			GridBagConstraints gbc_btnAddClasspath = new GridBagConstraints();
			gbc_btnAddClasspath.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnAddClasspath.insets = new Insets(0, 0, 5, 0);
			gbc_btnAddClasspath.gridx = 0;
			gbc_btnAddClasspath.gridy = 1;
			panel_1.add(getBtnAddClasspath(), gbc_btnAddClasspath);
			GridBagConstraints gbc_btnRemove = new GridBagConstraints();
			gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
			gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnRemove.gridx = 0;
			gbc_btnRemove.gridy = 2;
			panel_1.add(getBtnRemove(), gbc_btnRemove);
		}
		return panel_1;
	}

	private JButton getBtnNewButton()
	{
		if(btnNewButton == null)
		{
			btnNewButton = new JButton("Add Jar");
			btnNewButton.addActionListener(this::onAddJar);
		}
		return btnNewButton;
	}

	private JButton getBtnAddClasspath()
	{
		if(btnAddClasspath == null)
		{
			btnAddClasspath = new JButton("Add Directory");
			btnAddClasspath.addActionListener(this::onAddDir);
		}
		
		return btnAddClasspath;
	}

	private JButton getBtnRemove()
	{
		if(btnRemove == null)
		{
			btnRemove = new JButton("Remove");
			btnRemove.addActionListener(this::onRemove);
		}
		return btnRemove;
	}
	
	private JList<String> getList_1()
	{
		if(list == null)
		{
			list = new JList<String>();
			list.setModel(listModel);
		}
		
		return list;
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane(getList_1());
		}
		
		return scrollPane;
	}

	private void onAddJar(ActionEvent e)
	{
		int result = addJarfileChooser.showOpenDialog(getParent());
		
		if(result != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		String jarPath = addJarfileChooser.getSelectedFile().getAbsolutePath();
		
		if(!listModel.contains(jarPath))
		{
			listModel.addElement(jarPath);
		}
	}

	private void onAddDir(ActionEvent e)
	{
		int result = addClassPathFileChooser.showOpenDialog(getParent());
		
		if(result != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		String jarPath = addClassPathFileChooser.getSelectedFile().getAbsolutePath();
		
		if(!listModel.contains(jarPath))
		{
			listModel.addElement(jarPath);
		}
	}
	
	private void onRemove(ActionEvent e)
	{
		int idx = list.getSelectedIndex();
		
		if(idx < 0)
		{
			return;
		}
		
		listModel.remove(list.getSelectedIndex());
	}
	
	public void setProject(Project project)
	{
		this.project = project;
		
		Set<String> set = project.getClassPathEntries();
		listModel.removeAllElements();
		
		if(CollectionUtils.isNotEmpty(set))
		{
			set.forEach(path -> listModel.addElement(path));
		}
		
		super.setVisible(true);
	}

	public void applyChanges()
	{
		LinkedHashSet<String> setOfEntries = new LinkedHashSet<>();
		
		for(int i = 0; i < listModel.size(); i++)
		{
			setOfEntries.add(listModel.getElementAt(i));
		}

		project.setClassPathEntries(setOfEntries);
	}
}
