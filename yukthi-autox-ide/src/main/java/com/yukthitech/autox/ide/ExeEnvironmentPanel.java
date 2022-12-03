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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

@Component
public class ExeEnvironmentPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ExeEnvironmentPanel.class);
	
	private static Icon ACTIVE_ICON = IdeUtils.loadIcon("/ui/icons/green-dot.svg", 16);
	
	private static Icon INACTIVE_ICON = IdeUtils.loadIcon("/ui/icons/gray-dot.svg", 16);
	
	private static Icon INTERACTIVE_ICON = IdeUtils.loadIcon("/ui/icons/interactive.svg", 16);
	
	private static class ExeEnvLabel extends DefaultListCellRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			ExecutionEnvironment exeEnv = (ExecutionEnvironment) value;
			
			if(exeEnv == null)
			{
				return label;
			}
			
			if(exeEnv.isTerminated())
			{
				label.setIcon(INACTIVE_ICON);
			}
			else
			{
				if(exeEnv.isInteractive())
				{
					label.setIcon(INTERACTIVE_ICON);
				}
				else
				{
					label.setIcon(ACTIVE_ICON);
				}
			}
			
			return label;
		}
	}
	
	private JComboBox<ExecutionEnvironment> envComboBox = new JComboBox<ExecutionEnvironment>();
	
	private final JButton stopBut = new JButton("");
	private final JButton clearBut = new JButton("");
	private final JButton clearAllBut = new JButton("");
	private final JLabel lblEnvironments = new JLabel("Environments: ");

	@Autowired
	private IdeContext ideContext;
	
	/**
	 * Create the panel.
	 */
	public ExeEnvironmentPanel()
	{
		setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), new EmptyBorder(5, 0, 5, 0)));
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		
		envComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				changeEnvironment();
			}
		});
		
		add(lblEnvironments);

		add(envComboBox);
		envComboBox.setRenderer(new ExeEnvLabel());
		
		stopBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stopEnvironment();
			}
		});
		
		stopBut.setToolTipText("Stop");
		stopBut.setBorder(null);
		stopBut.setIcon(IdeUtils.loadIcon("/ui/icons/kill.svg", 16));
		add(stopBut);
		
		clearBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearEnvironment();
			}
		});
		clearBut.setToolTipText("Clear Environment");

		clearBut.setBorder(null);
		clearBut.setIcon(IdeUtils.loadIcon("/ui/icons/clear.svg", 16));
		add(clearBut);
		
		clearAllBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearAllEnvironments();
			}
		});
		
		clearAllBut.setToolTipText("Clear All Environments");

		clearAllBut.setBorder(null);
		clearAllBut.setIcon(IdeUtils.loadIcon("/ui/icons/clearAll.svg", 16));
		add(clearAllBut);
	}
	
	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void newEnvironmentStarted(ExecutionEnvironment environment)
			{
				newEnvironmentAdded(environment);
			}
			
			@Override
			public void environmentTerminated(ExecutionEnvironment environment)
			{
				IdeUtils.execute(() -> {
					envComboBox.revalidate();
					envComboBox.getParent().revalidate();
					
					int selIdx = envComboBox.getSelectedIndex();
					envComboBox.setSelectedItem(null);
					
					if(selIdx >= 0)
					{
						envComboBox.setSelectedIndex(selIdx);
					}
					
					checkForButtons();
				}, 200);
			}
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				logger.debug("As ide is shutting down, stopping all executing environments...");
				
				int count = envComboBox.getItemCount();
				
				for(int i = 0; i < count; i++)
				{
					ExecutionEnvironment env = envComboBox.getItemAt(i);
					env.terminate();
				}
			}
		});
		
		checkForButtons();
	}

	private void stopEnvironment()
	{
		ExecutionEnvironment activeEnv = (ExecutionEnvironment) envComboBox.getSelectedItem();
		
		if(activeEnv == null)
		{
			return;
		}

		activeEnv.terminate();
	}

	private synchronized void clearEnvironment()
	{
		ExecutionEnvironment activeEnv = (ExecutionEnvironment) envComboBox.getSelectedItem();
		
		if(activeEnv == null)
		{
			return;
		}
		
		if(activeEnv.isTerminated())
		{
			envComboBox.removeItem(activeEnv);
			ideContext.getProxy().activeEnvironmentChanged(null);
			
			checkForButtons();
		}
	}

	private synchronized void clearAllEnvironments()
	{
		int count = envComboBox.getItemCount();
		
		if(count <= 0)
		{
			return;
		}
		
		boolean activeEnvRemoved = false;
		List<ExecutionEnvironment> envToRemove = new ArrayList<>();
		ExecutionEnvironment env = null;
		ExecutionEnvironment activeEnv = (ExecutionEnvironment) envComboBox.getSelectedItem();
		
		for(int i = 0; i < count; i++)
		{
			env = envComboBox.getItemAt(i);
			
			if(env.isTerminated())
			{
				envToRemove.add(env);
				
				if(env == activeEnv)
				{
					activeEnvRemoved = true;
				}
			}
		}
		
		for(ExecutionEnvironment renv : envToRemove)
		{
			envComboBox.removeItem(renv);
		}
		
		if(activeEnvRemoved)
		{
			ideContext.getProxy().activeEnvironmentChanged(null);
		}
		
		checkForButtons();
	}

	private synchronized void changeEnvironment()
	{
		ExecutionEnvironment env = (ExecutionEnvironment) envComboBox.getSelectedItem();
		ideContext.getProxy().activeEnvironmentChanged(env);
	}
	
	private void checkForButtons()
	{
		clearAllBut.setEnabled(false);
		clearBut.setEnabled(false);
		stopBut.setEnabled(false);
		
		int itemCount = envComboBox.getItemCount();
		
		if(itemCount <= 0)
		{
			return;
		}
		
		if(envComboBox.getSelectedItem() != null)
		{
			ExecutionEnvironment env = (ExecutionEnvironment) envComboBox.getSelectedItem();
			
			if(env.isTerminated())
			{
				clearBut.setEnabled(true);
			}
			else
			{
				stopBut.setEnabled(true);
			}
		}
		
		if(clearBut.isEnabled())
		{
			clearAllBut.setEnabled(true);
			return;
		}
		
		for(int i = 0; i < itemCount; i++)
		{
			ExecutionEnvironment env = envComboBox.getItemAt(i);
			
			if(env.isTerminated())
			{
				clearAllBut.setEnabled(true);
				break;
			}
		}
	}
	
	private synchronized void newEnvironmentAdded(ExecutionEnvironment environment)
	{
		clearAllEnvironments();
		
		envComboBox.addItem(environment);
		envComboBox.setSelectedItem(environment);
		
		checkForButtons();
		
		ideContext.getProxy().activeEnvironmentChanged(environment);
	}
}
