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
package com.yukthitech.prism.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.utils.ObjectWrapper;

public class InProgressDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private static InProgressDialog instance = new InProgressDialog();
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblPleaseWait = new JLabel("Please Wait! Work in Progress...");
	private final JLabel lblSubMessage = new JLabel("");
	
	/**
	 * Create the dialog.
	 */
	public InProgressDialog()
	{
		setModalityType(ModalityType.APPLICATION_MODAL);
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
	
	public synchronized <T> T display(String message, final Supplier<T> jobToExecute, Consumer<Exception> errorHandler)
	{
		lblPleaseWait.setText(message);
		
		ObjectWrapper<T> resWrapper = new ObjectWrapper<>();
		ObjectWrapper<Exception> exWrapper = new ObjectWrapper<>();

		IdeUtils.execute(new Runnable()
		{
			@Override
			public void run()
			{
				//wait till current dialog becomes visible
				while(!InProgressDialog.this.isVisible())
				{
					AutomationUtils.sleep(5);
				}
				
				//once visible execute the actual operation
				try
				{
					T res = jobToExecute.get();
					resWrapper.setValue(res);
				}catch(Exception ex)
				{
					exWrapper.setValue(ex);
					//logger.error("An error occurred while executing in-progress action", ex);
				}
				
				//post operation close the dialog
				InProgressDialog.this.setVisible(false);
			}
		}, 1);

		IdeUtils.centerOnScreen(this);
		super.setVisible(true);
		
		if(exWrapper.getValue() != null)
		{
			errorHandler.accept(exWrapper.getValue());
		}
		
		return resWrapper.getValue();
	}
	
	public synchronized void display(String message, final Runnable jobToExecute, Consumer<Exception> errorHandler)
	{
		Supplier<Object> wrapper = () -> 
		{
			jobToExecute.run();
			return null;
		};
		
		display(message, wrapper, errorHandler);
	}

	public synchronized void display(String message, final Runnable jobToExecute)
	{
		display(message, jobToExecute, null);
	}
	
	public synchronized static InProgressDialog getInstance()
	{
		return instance;
	}
}
