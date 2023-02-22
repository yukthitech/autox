/**
 * Copyright (c) 2023 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
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
package com.yukthitech.prism;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.yukthitech.prism.ui.InProgressDialog;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

public class IconChecker extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private final JPanel panel = new JPanel();
	private final JLabel lblNewLabel = new JLabel("File:");
	private final JTextField filePathFld = new JTextField();
	private final JButton browseBut = new JButton("...");
	private final JButton reloadBut = new JButton("Reload");
	private final JComboBox<Integer> sizeFld = new JComboBox<>();
	private final JLabel lblNewLabel_1 = new JLabel("Size:");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JLabel iconLbl = new JLabel("");
	
	private JFileChooser fileChooser = new JFileChooser(); 

	/**
	 * Create the frame.
	 */
	public IconChecker()
	{
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return "SVG Files";
			}
			
			@Override
			public boolean accept(File f)
			{
				if(f.isDirectory())
				{
					return true;
				}
				
				return f.getName().toLowerCase().endsWith(".svg");
			}
		});
		
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		filePathFld.setColumns(30);
		setTitle("Icon Checker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 717, 486);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		contentPane.add(panel, BorderLayout.NORTH);
		
		panel.add(lblNewLabel_1);
		sizeFld.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {20, 40, 60, 80, 100, 120, 140, 160, 180, 200}));
		sizeFld.setSelectedIndex(4);
		
		panel.add(sizeFld);
		sizeFld.addItemListener(e -> 
		{
			onReload(null);
		});
		
		panel.add(lblNewLabel);
		
		panel.add(filePathFld);
		
		filePathFld.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					onReload(null);
				}
			}
		});
		
		panel.add(browseBut);
		browseBut.addActionListener(this::onBrowse);
		
		panel.add(reloadBut);
		reloadBut.addActionListener(this::onReload);
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(iconLbl);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					IconChecker frame = new IconChecker();
					frame.setVisible(true);
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	private void onBrowse(ActionEvent e)
	{
		int res = fileChooser.showOpenDialog(this);
		
		if(res != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		File file = fileChooser.getSelectedFile();
		filePathFld.setText(file.getPath());
		
		onReload(null);
	}
	
	private void onReload(ActionEvent e)
	{
		InProgressDialog.getInstance().display("Loading svg file", () -> 
		{
			File file = new File(filePathFld.getText());
			
			if(!file.exists())
			{
				JOptionPane.showMessageDialog(this, "Specified file does not exist");
				return;
			}
			
			int size = (Integer) sizeFld.getSelectedItem();
			
			try
			{
				BufferedImage img = IdeUtils.loadSvgFile(file.getPath(), size, size);
				ImageIcon icon = new ImageIcon(img);
				iconLbl.setIcon(icon);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "An error occurred while loading svg file.\nError: " + ex);
			}
		});
	}
}
