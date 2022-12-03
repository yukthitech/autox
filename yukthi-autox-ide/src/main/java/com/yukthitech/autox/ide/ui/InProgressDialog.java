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
package com.yukthitech.autox.ide.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.IdeUtils;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

public class InProgressDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private static InProgressDialog instance;
	
	private static Logger logger = LogManager.getLogger(InProgressDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblPleaseWait = new JLabel("Please Wait! Work in Progress...");
	private final JLabel lblSubMessage = new JLabel("");

	/**
	 * Create the dialog.
	 */
	public InProgressDialog(Window window)
	{
		super(window);
//		super(window,ModalityType.APPLICATION_MODAL);
//		setModalityType(ModalityType.APPLICATION_MODAL);
		setUndecorated(true);
		setResizable(false);
		setBounds(100, 100, 531, 143);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), new EmptyBorder(5, 5, 5, 5)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		lblPleaseWait.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPleaseWait.setHorizontalAlignment(SwingConstants.CENTER);
		
		contentPanel.add(lblPleaseWait, BorderLayout.CENTER);
		lblSubMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSubMessage.setHorizontalAlignment(SwingConstants.CENTER);
		
		contentPanel.add(lblSubMessage, BorderLayout.SOUTH);
	}
	
	public void setSubmessage(String mssg)
	{
		lblSubMessage.setText(mssg);
	}
	
	public synchronized void display(String message, final Runnable jobToExecute)
	{
		lblPleaseWait.setText(message);

		super.setVisible(true);
		IdeUtils.centerOnScreen(this);
		
		IdeUtils.execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					jobToExecute.run();
				}catch(Exception ex)
				{
					logger.error("An error occurred while executing in-progress action", ex);
				}
				
				InProgressDialog.this.setVisible(false);
			}
		}, 1);
	}
	
	public synchronized static InProgressDialog getInstance(){
		if(instance==null){
			instance=new InProgressDialog(IdeUtils.getCurrentWindow());
		}
		return instance;
	}
}
