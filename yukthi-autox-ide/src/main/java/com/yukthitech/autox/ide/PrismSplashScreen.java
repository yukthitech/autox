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
package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

public class PrismSplashScreen extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private static PrismSplashScreen splashScreen = new PrismSplashScreen();
	
	private JPanel contentPane;
	private final JLabel label = new JLabel("");

	/**
	 * Launch the application.
	 */
	public static void display()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					splashScreen.setVisible(true);
					IdeUtils.centerOnScreen(splashScreen);
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void close()
	{
		splashScreen.setVisible(false);
	}

	/**
	 * Create the frame.
	 */
	public PrismSplashScreen()
	{
		setBackground(Color.WHITE);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.setIconImage(IdeUtils.loadIconWithoutBorder("/ui/icons/autox-logo.png", 64).getImage());
		setBounds(100, 100, 800, 350);
		contentPane = new JPanel();
		//contentPane.setBorder(new LineBorder(new Color(0, 0, 255), 2));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		//label.setBorder(new EmptyBorder(3, 3, 3, 3));
		
		contentPane.add(label, BorderLayout.CENTER);
		
		ImageIcon splashIcon = IdeUtils.loadIcon("/ui/autox-splash-screen.svg", 800, 350, 0, false);
		label.setIcon(splashIcon);
	}

}
