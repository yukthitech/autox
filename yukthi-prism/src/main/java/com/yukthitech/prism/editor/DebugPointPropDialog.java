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
package com.yukthitech.prism.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.exeenv.debug.DebugPointManager;
import com.yukthitech.prism.exeenv.debug.IdeDebugPoint;
import com.yukthitech.swing.EscapableDialog;

@Component
public class DebugPointPropDialog extends EscapableDialog
{
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblNewLabel = new JLabel("Location:");
	private final JLabel locationFld = new JLabel("Debug Point Location");
	private final JLabel lblNewLabel_1 = new JLabel("<html><body>\r\nCondition:<br/>\r\n<span style=\"color: gray; font-weight: 7px;\">"
			+ "Can be expression filter. By default will be treated as fmarker expression ('expr:' prefix)</span>\r\n</body></html>");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea conditionFld = new JTextArea();
	
	@Autowired
	private DebugPointManager debugPointManager;
	
	private IdeDebugPoint debugPoint;
	
	private boolean result = false;

	/**
	 * Create the dialog.
	 */
	public DebugPointPropDialog()
	{
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		setTitle("Debug Point Properties");
		setBounds(100, 100, 545, 204);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_locationFld = new GridBagConstraints();
		gbc_locationFld.insets = new Insets(0, 0, 5, 0);
		gbc_locationFld.fill = GridBagConstraints.HORIZONTAL;
		gbc_locationFld.gridx = 1;
		gbc_locationFld.gridy = 0;
		locationFld.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPanel.add(locationFld, gbc_locationFld);
		
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel_1.gridwidth = 2;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 2;
		gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 2;
		contentPanel.add(scrollPane, gbc_scrollPane_1);
		
		scrollPane.setViewportView(conditionFld);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this::onUpdate);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this::onCancel);
				buttonPane.add(cancelButton);
			}
		}
	}

	public boolean display(IdeDebugPoint debugPoint)
	{
		result = false;
		
		this.debugPoint = debugPoint;
		locationFld.setText(debugPoint.getFile().getName() + ":" + debugPoint.getLineNo());
		conditionFld.setText(debugPoint.getCondition());
		
		IdeUtils.centerOnScreen(this);
		super.setVisible(true);
		
		return result;
	}
	
	private void onCancel(ActionEvent e)
	{
		super.setVisible(false);
	}
	
	private void onUpdate(ActionEvent ex)
	{
		String existingCondtion = debugPoint.getCondition();
		String newCondition = conditionFld.getText();
		newCondition = newCondition.trim().isEmpty() ? null : newCondition.trim();
		
		//if both are null, consider there is no modification
		if(existingCondtion == null && newCondition == null)
		{
			super.setVisible(false);
			return;
		}
		
		//if one of them is null, update condition
		if(existingCondtion == null || newCondition == null)
		{
			debugPointManager.modifyCondition(debugPoint, newCondition, this);
			result = true;
		}
		//if condition is modified
		else if(!existingCondtion.equals(newCondition))
		{
			debugPointManager.modifyCondition(debugPoint, newCondition, this);
			result = true;
		}
		
		super.setVisible(false);
	}
}
