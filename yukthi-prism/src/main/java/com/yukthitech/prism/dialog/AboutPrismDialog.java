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
import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.Version;
import com.yukthitech.prism.swing.IdeDialogPanel;
import com.yukthitech.swing.HtmlPanel;

public class AboutPrismDialog extends IdeDialogPanel
{
	private static final long serialVersionUID = 1L;
	
	private static ImageIcon PRISM_ICON = IdeUtils.loadIcon("/ui/icons/prism.svg", 80);
	
	private static Color BG_COLOR = new Color(245, 245, 245);
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblNewLabel = new JLabel("");
	private final HtmlPanel txtpnYukthiTechsoftPvt = new HtmlPanel();
	
	private String version;
	private String autoxVersion;

	/**
	 * Create the dialog.
	 */
	public AboutPrismDialog()
	{
		super.setDialogResizable(false);
		super.setModalityType(ModalityType.APPLICATION_MODAL);
		
		version = Version.getLocalVersion();
		autoxVersion = Version.getLocalAutoxVersion();

		super.setTitle("About Prism");
		super.setDialogBounds(100, 100, 576, 251);
		super.setBounds(100, 100, 576, 251);
		
		super.setBackground(BG_COLOR);
		super.setCenterOnScreen(true);
		
		setLayout(new BorderLayout());
		contentPanel.setBackground(BG_COLOR);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {0, 0};
		gbl_contentPanel.rowHeights = new int[] {0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0};
		gbl_contentPanel.rowWeights = new double[]{1.0};
		contentPanel.setLayout(gbl_contentPanel);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 15, 5, 15);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		lblNewLabel.setOpaque(true);
		lblNewLabel.setBackground(BG_COLOR);
		
		lblNewLabel.setIcon(PRISM_ICON);
		
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_txtpnYukthiTechsoftPvt = new GridBagConstraints();
		gbc_txtpnYukthiTechsoftPvt.fill = GridBagConstraints.BOTH;
		gbc_txtpnYukthiTechsoftPvt.gridx = 1;
		gbc_txtpnYukthiTechsoftPvt.gridy = 0;
		txtpnYukthiTechsoftPvt.setContent(
				String.format("<html><body>"
						+ "<span style=\"color: #ff33cc;font-weight: bold;\">Prism - Open Source IDE for AutoX </span><br/><br/>"
						+ "<b>Website:</b> <a href=\"https://autox.yukthitech.com\">https://autox.yukthitech.com</a><br/>"
						+ "<b>Source Location:</b> <a href=\"https://github.com/yukthitech/autox\">https://github.com/yukthitech/autox</a><br/>"
						+ "<b>Version:</b> %s<br/>"
						+ "<b>AutoX Version:</b> %s<br/><br/>"
						+ "<i>Copyright © 2023 Yukthi Techsoft Pvt. Ltd. All rights reserved.</i></body></html>", version, autoxVersion)
			);
		txtpnYukthiTechsoftPvt.getTextPane().setBorder(null);
		txtpnYukthiTechsoftPvt.getTextPane().setBackground(BG_COLOR);
		txtpnYukthiTechsoftPvt.getScrollPane().setBorder(null);
		txtpnYukthiTechsoftPvt.setCaretEnabled(false);
		txtpnYukthiTechsoftPvt.getTextPane().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		contentPanel.add(txtpnYukthiTechsoftPvt, gbc_txtpnYukthiTechsoftPvt);
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(BG_COLOR);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				
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
		super.closeDialog();
	}
	
	public void display()
	{
		super.displayInDialog();
	}
}
