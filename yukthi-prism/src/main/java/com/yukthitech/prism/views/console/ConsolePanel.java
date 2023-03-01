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
package com.yukthitech.prism.views.console;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.prism.IViewPanel;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.actions.FileActions;
import com.yukthitech.prism.exeenv.EnvironmentActivationEvent;
import com.yukthitech.prism.exeenv.EnvironmentTerminatedEvent;
import com.yukthitech.prism.exeenv.ExecutionEnvironment;
import com.yukthitech.prism.services.GlobalStateManager;
import com.yukthitech.prism.services.IdeEventHandler;
import com.yukthitech.prism.swing.HyperLinkEvent;
import com.yukthitech.prism.swing.YukthiHtmlPane;
import com.yukthitech.swing.IconButton;

@Component
public class ConsolePanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(ConsolePanel.class);
	
	private static Pattern LOC_PATTERN = Pattern.compile("([\\w\\-\\.]+\\.xml)\\:(\\d+)", Pattern.CASE_INSENSITIVE);

	private final JPanel panel = new JPanel();
	private final JLabel lblEnvironment = new JLabel("Environment: ");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel buttonPanel = new JPanel();
	private final YukthiHtmlPane consoleDisplayArea = new YukthiHtmlPane();
	
	private final IconButton btnClear = new IconButton();

	@Autowired
	private FileActions fileAction;
	
	@Autowired
	private GlobalStateManager globalStateManager;

	//private JTabbedPane parentTabbedPane;

	private ExecutionEnvironment activeEnvironment;
	private final IconButton btnOpenReport = new IconButton();
	
	private JTabbedPane parentTabbedPane;
	
	private int currentEnvironmentId = -1;
	
	private long lastUpdatedOn = -1;

	/**
	 * Create the panel.
	 */
	public ConsolePanel()
	{
		setBorder(new EmptyBorder(3, 3, 3, 3));
		setLayout(new BorderLayout(0, 0));

		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {444, 0};
		gbl_panel.rowHeights = new int[] {21};
		gbl_panel.columnWeights = new double[] { 1.0, 0.0 };
		gbl_panel.rowWeights = new double[] { 0.0 };
		panel.setLayout(gbl_panel);

		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.anchor = GridBagConstraints.EAST;
		gbc_buttonPanel.fill = GridBagConstraints.VERTICAL;
		gbc_buttonPanel.gridx = 1;
		gbc_buttonPanel.gridy = 0;
		FlowLayout fl_buttonPanel = (FlowLayout) buttonPanel.getLayout();
		fl_buttonPanel.setAlignment(FlowLayout.LEFT);
		fl_buttonPanel.setVgap(2);
		fl_buttonPanel.setHgap(2);
		panel.add(buttonPanel, gbc_buttonPanel);

		btnClear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearConsole();
			}
		});
		
		btnClear.setToolTipText("Clear");
		btnClear.setIcon(IdeUtils.loadIconWithoutBorder("/ui/icons/clear-console.svg", 20));

		buttonPanel.add(btnClear);
		btnOpenReport.addActionListener(this::openReport);
		btnOpenReport.setEnabled(false);
		btnOpenReport.setIcon(IdeUtils.loadIconWithoutBorder("/ui/icons/report.svg", 20));
		btnOpenReport.setToolTipText("Open Report");
		
		buttonPanel.add(btnOpenReport);
		lblEnvironment.setBorder(new EmptyBorder(3, 3, 3, 3));
		lblEnvironment.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblEnvironment.setOpaque(true);
		lblEnvironment.setBackground(UIManager.getColor("info"));
		lblEnvironment.setVisible(false);

		GridBagConstraints gbc_lblEnvironment = new GridBagConstraints();
		gbc_lblEnvironment.fill = GridBagConstraints.BOTH;
		gbc_lblEnvironment.gridx = 0;
		gbc_lblEnvironment.gridy = 0;
		panel.add(lblEnvironment, gbc_lblEnvironment);

		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(consoleDisplayArea);
		
		consoleDisplayArea.addHyperLinkListener(this::onHyperLinkClick);
		
		consoleDisplayArea.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				consoleDisplayArea.getCaret().setVisible(true);
				globalStateManager.focusGained(consoleDisplayArea);
			}
		});
		
		IdeUtils.scheduleJob(this::refreshConsoleText, 500);
	}

	@IdeEventHandler
	private void onEnvironmentActivate(EnvironmentActivationEvent event)
	{
		ExecutionEnvironment activeEnvironment = event.getNewActiveEnvironment();
		
		if(activeEnvironment != null)
		{
			lblEnvironment.setVisible(true);
			lblEnvironment.setText("Environment: " + activeEnvironment.getName());
		}
		else
		{
			lblEnvironment.setVisible(false);
		}

		ConsolePanel.this.activeEnvironment = activeEnvironment;
		refreshConsoleText();
	}
	
	@IdeEventHandler
	private void onEnvironmentTerminated(EnvironmentTerminatedEvent event)
	{
		ExecutionEnvironment environment = event.getExecutionEnvironment();
		
		if(activeEnvironment != environment)
		{
			return;
		}
		
		boolean repFile = (environment.isReportFileAvailable());
		btnOpenReport.setEnabled(repFile);
	}
	
	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
		this.parentTabbedPane = parentTabPane;
	}

	private synchronized void refreshConsoleText()
	{
		if(activeEnvironment == null)
		{
			if(currentEnvironmentId == -1)
			{
				return;
			}
			
			consoleDisplayArea.setText("");
			currentEnvironmentId = -1;
			return;
		}
		
		if(activeEnvironment.getId() == currentEnvironmentId 
				&& activeEnvironment.getLastConsoleChangeTime() == lastUpdatedOn)
		{
			return;
		}
		
		String html = activeEnvironment.getConsoleHtml();
		String code = injectLinks(html);

		EventQueue.invokeLater(() -> 
		{
			try
			{
				consoleDisplayArea.setText("<html><body id=\"body\">" + code + "</body></html>");
			}catch(Exception ex)
			{
				logger.error("An error occurred while setting content in console panel. Code:\n{}", code, ex);
			}
			
			moveToEnd();
		});
		
		currentEnvironmentId = activeEnvironment.getId();
		lastUpdatedOn = activeEnvironment.getLastConsoleChangeTime();
	}
	
	private String injectLinks(CharSequence html)
	{
		Matcher matcher = LOC_PATTERN.matcher(html);
		StringBuffer buff = new StringBuffer();
		
		while(matcher.find())
		{
			matcher.appendReplacement(buff, "<a href=\"" + matcher.group(0) + "\">" + matcher.group(0) + "</a>");
		}
		
		matcher.appendTail(buff);
		return buff.toString();
	}
	
	private void moveToEnd()
	{
		IdeUtils.execute(() -> 
		{
			EventQueue.invokeLater(() -> 
			{
				// move scroll pane to the end
				JScrollBar vertical = scrollPane.getVerticalScrollBar();
				vertical.setValue(vertical.getMaximum());
				
				JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
				horizontal.setValue(0);
			});
		}, 200);
	}

	private synchronized void clearConsole()
	{
		if(activeEnvironment == null)
		{
			return;
		}
		
		activeEnvironment.clearConsole();
		refreshConsoleText();
	}
	
	private void onHyperLinkClick(HyperLinkEvent event)
	{
		Matcher matcher = LOC_PATTERN.matcher(event.getHref());
		
		if(!matcher.matches())
		{
			return;
		}
		
		String file = matcher.group(1);
		int lineNo = Integer.parseInt(matcher.group(2));
		
		fileAction.gotoFile(activeEnvironment.getProject(), file, lineNo, true);
	}
	
	private void openReport(ActionEvent e)
	{
		File file = activeEnvironment.getReportFile();
		
		if(file.exists())
		{
			try
			{
				Desktop.getDesktop().open(file);
			}catch(Exception ex)
			{
				logger.error("An error occurred while opening file: " + file.getPath(), ex);
				JOptionPane.showMessageDialog(this, "An error occurred while opening file: " + file.getPath() + "\nError: " + ex);
			}
		}
	}
}
