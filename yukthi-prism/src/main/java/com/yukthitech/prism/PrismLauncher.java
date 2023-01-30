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
package com.yukthitech.prism;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yukthitech.prism.context.IdeContext;
import com.yukthitech.prism.editor.FileEditorTabbedPane;
import com.yukthitech.prism.events.IdeClosingEvent;
import com.yukthitech.prism.events.IdeOpeningEvent;
import com.yukthitech.prism.events.IdePreStateLoadEvent;
import com.yukthitech.prism.events.IdeStartedEvent;
import com.yukthitech.prism.help.HelpPanel;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionCollection;
import com.yukthitech.prism.layout.ActionHolder;
import com.yukthitech.prism.layout.UiLayout;
import com.yukthitech.prism.model.IdeState;
import com.yukthitech.prism.projexplorer.ProjectExplorer;
import com.yukthitech.prism.services.IdeEventManager;
import com.yukthitech.prism.services.IdeStateManager;
import com.yukthitech.prism.services.ResourceCache;
import com.yukthitech.prism.swing.IdeDialogPanel;
import com.yukthitech.prism.views.console.ConsolePanel;
import com.yukthitech.prism.views.debug.DebugPanel;
import com.yukthitech.prism.views.search.SearchResultPanel;

@ActionHolder
public class PrismLauncher extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(PrismLauncher.class);
	
	private static final String PANEL_MAIN = "mainPanel";
	
	private static final String PANEL_MAXIMIZED = "maximizedPanel";
	
	private JPanel contentPane;
	
	@Autowired
	private ProjectExplorer projectExplorer;
	
	//@Autowired
	//private RestPanel restPanel;
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	@Autowired
	private UiLayout uiLayout;
	
	@Autowired
	private ActionCollection actionCollection;
	
	/**
	 * Ide context.
	 */
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ExeEnvironmentPanel exeEnvironmentPanel;
	
	@Autowired
	private ConsolePanel consolePanel;
	
	@Autowired
	private DebugPanel debugPanel;
	
	@Autowired
	private SearchResultPanel searchResultPanel;

	@Autowired
	private HelpPanel helpPanel;
	
	@Autowired
	private IdeEventManager ideEventManager;
	
	@Autowired
	private IdeStateManager ideStateManager;

	/**
	 * Top panel to hold tool bar and env panel.
	 */
	private JPanel topPanel = new JPanel();
	
	/**
	 * Container to be used for maximized panes.
	 */
	private JPanel maximizedPanel = new JPanel();
	
	private JSplitPane verticalSplitPane;
	
	private JSplitPane horizontalSplitPane;
	
	private MaximizableTabbedPane leftTabbedPane;
	
	private MaximizableTabbedPane rightBottomTabbedPane;
	
	/**
	 * Status indicating if any tab is currently maximized.
	 */
	private boolean maximized = false;
	
	private CardLayout cardLayoutForMaximization;
	
	/**
	 * Panel containing the main tabbed panels and maximized panels as cards.
	 */
	private JPanel cardedMainPanel;
	
	/**
	 * Launch the application.
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception
	{
		PrismSplashScreen.display();
		
		//wait for splash screen to display
		Thread.sleep(200);
		
		StopWatch watch = new StopWatch();
		watch.start();
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-autox-context.xml");
		
		PrismLauncher ide = (PrismLauncher) context.getBean(PrismLauncher.class);
		IdeDialogPanel.setMainAppWindow(ide);
		
		ide.initIde();
		
		watch.stop();
		logger.info("Time taken for IDE startup: {}", watch.formatTime());
	}
	
	private void initIde()
	{
		ideEventManager.init();
		
		init();
		
		//load previous state from file
		IdeState ideState = ideStateManager.getState();
		
		//send the pre-state-load event
		ideEventManager.raiseAsyncEvent(new IdePreStateLoadEvent(ideState));

		//old way of setting loaded distributed state
		ideContext.getProxy().loadState(ideState);
		
		//send the opening event
		ideEventManager.raiseAsyncEvent(new IdeOpeningEvent(ideState));

		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					setVisible(true);
					PrismSplashScreen.close();
				} catch(Exception e)
				{
					e.printStackTrace();
				}
				
				//send the started event
				EventQueue.invokeLater(() -> 
				{
					ideEventManager.raiseAsyncEvent(new IdeStartedEvent(ideState));					
				});
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	private void init()
	{
		super.setSize(640, 480);
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		super.setIconImage(IdeUtils.loadIconWithoutBorder("/ui/icons/prism.svg", 64).getImage());
		super.setTitle("AutoX IDE");
		
		super.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				closeIde();
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel mainPanel = createMainPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JLabel lblNewLabel = new JLabel("Prism successfully opened");
		panel.add(lblNewLabel);
		
		topPanel.setLayout(new BorderLayout());
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{1.0};
		topPanel.setLayout(gridBagLayout);
		
		GridBagConstraints envPanelConst = new GridBagConstraints();
		envPanelConst.fill = GridBagConstraints.BOTH;
		envPanelConst.gridx = 1;
		envPanelConst.gridy = 0;
		topPanel.add(exeEnvironmentPanel, envPanelConst);

		contentPane.add(topPanel, BorderLayout.NORTH);
		
		loadLayout();
	}
	
	private JPanel createMainPanel()
	{
		cardedMainPanel = new JPanel();
		
		cardLayoutForMaximization = new CardLayout();
		cardedMainPanel.setLayout(cardLayoutForMaximization);

		cardedMainPanel.add(createSplitPanes(), PANEL_MAIN);
		
		//create and maximized panel
		maximizedPanel.setLayout(new BorderLayout());
		cardedMainPanel.add(maximizedPanel, PANEL_MAXIMIZED);
		
		return cardedMainPanel;
	}
	
	private JSplitPane createSplitPanes()
	{
		IMaximizationListener maximizeListener = new IMaximizationListener()
		{
			@Override
			public void minimize(MaximizableTabbedPane tabPane)
			{
				minimizeTabPane(tabPane);
			}
			
			@Override
			public void flipMaximizationStatus(MaximizableTabbedPane tabPane)
			{
				flipMaximize(tabPane);
			}
		};
		
		verticalSplitPane = new JSplitPane();
		verticalSplitPane.setResizeWeight(0.2);

		leftTabbedPane = new MaximizableTabbedPane();
		verticalSplitPane.setLeftComponent(leftTabbedPane);
		leftTabbedPane.setParentDetails(maximizeListener, verticalSplitPane, true);
		leftTabbedPane.setViewsCloseable(false);

		leftTabbedPane.addTab("Project Explorer", null, projectExplorer, null);
		//leftTabbedPane.addTab("Rest", null, restPanel, null);

		horizontalSplitPane = new JSplitPane();
		horizontalSplitPane.setResizeWeight(0.8);
		horizontalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		verticalSplitPane.setRightComponent(horizontalSplitPane);

		// editor = new JTabbedPane(JTabbedPane.TOP);
		horizontalSplitPane.setLeftComponent(fileEditorTabbedPane);
		fileEditorTabbedPane.setParentDetails(maximizeListener, horizontalSplitPane, true);

		rightBottomTabbedPane = new MaximizableTabbedPane();
		horizontalSplitPane.setRightComponent(rightBottomTabbedPane);
		rightBottomTabbedPane.setParentDetails(maximizeListener, horizontalSplitPane, false);
		rightBottomTabbedPane.setViewsCloseable(false);

		rightBottomTabbedPane.addTab("Console", null, consolePanel, null);
		consolePanel.setParent(rightBottomTabbedPane);
		
		rightBottomTabbedPane.addTab("Debug", null, debugPanel, null);
		debugPanel.setParent(rightBottomTabbedPane);
		
		rightBottomTabbedPane.addTab("Search", null, searchResultPanel, null);
		searchResultPanel.setParent(rightBottomTabbedPane);

		rightBottomTabbedPane.addTab("Help", null, helpPanel, null);
		helpPanel.setParent(rightBottomTabbedPane);
		
		return verticalSplitPane;
	}
	
	private void loadLayout()
	{
		JMenuBar menuBar = uiLayout.getMenuBar().toJMenuBar(actionCollection);
		super.setJMenuBar(menuBar);
		
		JPanel wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		wrapperPanel.setBorder(new EtchedBorder());
		
		JToolBar toolBar = uiLayout.getToolBar().toJToolBar(actionCollection);
		wrapperPanel.add(toolBar);
		
		GridBagConstraints toolBarConst = new GridBagConstraints();
		toolBarConst.gridx = 0;
		toolBarConst.gridy = 0;
		toolBarConst.anchor = GridBagConstraints.WEST;
		toolBarConst.fill = GridBagConstraints.BOTH;
		topPanel.add(wrapperPanel, toolBarConst);
	}
	
	@Action
	public void closeIde()
	{
		boolean res = fileEditorTabbedPane.checkForUnsavedFiles();
		
		if(!res)
		{
			return;
		}
		
		logger.debug("Closing the ide..");
		
		ResourceCache.getInstance().saveCache();
		
		IdeState ideState = ideStateManager.getState();

		//send the closing event
		ideEventManager.raiseSyncEvent(new IdeClosingEvent(ideState));
		
		//old way of getting distributed state
		ideContext.getProxy().saveState(ideState);
		
		//save the final state
		ideStateManager.saveState(ideState);
		
		System.exit(-1);
	}
	
	private synchronized void minimizeTabPane(MaximizableTabbedPane tabPane)
	{
		if(!maximized)
		{
			return;
		}

		if(tabPane.isLeftComponent())
		{
			tabPane.getParentSplitPane().setLeftComponent(tabPane);
		}
		else
		{
			tabPane.getParentSplitPane().setRightComponent(tabPane);
		}
		
		cardLayoutForMaximization.show(cardedMainPanel, PANEL_MAIN);
		
		maximized = false;
	}
	
	private synchronized void flipMaximize(MaximizableTabbedPane tabPane)
	{
		if(maximized)
		{
			minimizeTabPane(tabPane);
		}
		else
		{
			maximizedPanel.add(tabPane, BorderLayout.CENTER);
			cardLayoutForMaximization.show(cardedMainPanel, PANEL_MAXIMIZED);
			
			maximized = true;
		}
	}
}