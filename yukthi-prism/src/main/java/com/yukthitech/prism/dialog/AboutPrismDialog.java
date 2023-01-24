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
package com.yukthitech.prism.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.Version;

public class AboutPrismDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private static ImageIcon PRISM_ICON = IdeUtils.loadIcon("/ui/icons/prism.svg", 80);
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblNewLabel = new JLabel("");
	private final JLabel lblNewLabel_1 = new JLabel("");
	private final JTextPane txtpnYukthiTechsoftPvt = new JTextPane();
	
	private String version;
	private String autoxVersion;

	/**
	 * Create the dialog.
	 */
	public AboutPrismDialog()
	{
		setResizable(false);
		super.setModalityType(ModalityType.APPLICATION_MODAL);
		
		version = Version.getLocalVersion();
		autoxVersion = Version.getLocalAutoxVersion();

		setTitle("About Prism");
		setBounds(100, 100, 576, 286);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		lblNewLabel.setOpaque(true);
		lblNewLabel.setBackground(Color.WHITE);
		
		lblNewLabel.setIcon(PRISM_ICON);
		
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		lblNewLabel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		GridBagConstraints gbc_txtpnYukthiTechsoftPvt = new GridBagConstraints();
		gbc_txtpnYukthiTechsoftPvt.fill = GridBagConstraints.BOTH;
		gbc_txtpnYukthiTechsoftPvt.gridx = 0;
		gbc_txtpnYukthiTechsoftPvt.gridy = 2;
		txtpnYukthiTechsoftPvt.setForeground(Color.BLACK);
		txtpnYukthiTechsoftPvt.setFont(new Font("Tahoma", Font.BOLD, 12));
		txtpnYukthiTechsoftPvt.setText(
				String.format("Prism\r\n"
						+ "Version: %s\r\n"
						+ "AutoX Version: %s\r\n"
						+ "Copyright © 2023 Yukthi Techsoft Pvt. Ltd. All rights reserved.", version, autoxVersion)
			);
		txtpnYukthiTechsoftPvt.setEditable(false);
		contentPanel.add(txtpnYukthiTechsoftPvt, gbc_txtpnYukthiTechsoftPvt);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(Color.WHITE);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				okButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						close(e);
					}
				});
			}
		}
	}

	private void close(ActionEvent e)
	{
		super.setVisible(false);
	}
	
	public void display()
	{
		super.setVisible(true);
	}
}
