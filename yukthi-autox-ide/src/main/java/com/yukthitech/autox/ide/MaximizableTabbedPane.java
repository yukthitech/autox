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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

/**
 * Tabbed pane which support data for maximization.
 * @author akiran
 */
public class MaximizableTabbedPane extends JTabbedPane
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Parent split pane containing the split pane.
	 */
	private JSplitPane parentSplitPane;
	
	/**
	 * Flag indicating if this is left component or right.
	 */
	private boolean leftComponent;
	
	protected IMaximizationListener maximizationListener;
	
	private boolean viewsCloseable = true;
	
	public MaximizableTabbedPane()
	{
	}
	
	public void setParentDetails(IMaximizationListener listener, JSplitPane parentPane, boolean leftComp)
	{
		this.maximizationListener = listener;
		this.parentSplitPane = parentPane;
		this.leftComponent = leftComp;
	}

	public JSplitPane getParentSplitPane()
	{
		return parentSplitPane;
	}

	public boolean isLeftComponent()
	{
		return leftComponent;
	}
	
	public void setViewsCloseable(boolean viewsCloseable)
	{
		this.viewsCloseable = viewsCloseable;
	}
	
	public void flipMaximization()
	{
		maximizationListener.flipMaximizationStatus(this);
	}

	@Override
	public void addTab(String title, Component component)
	{
		int nextTabIndex = super.getTabCount();
		super.addTab(title, component);
		
		super.setTabComponentAt(nextTabIndex, new MaximizableTabbedPaneTab(title, this, component, maximizationListener, viewsCloseable));
	}
	
	@Override
	public void addTab(String title, Icon icon, Component component)
	{
		int nextTabIndex = super.getTabCount();
		super.addTab(title, icon, component);
		
		super.setTabComponentAt(nextTabIndex, new MaximizableTabbedPaneTab(title, this, component, maximizationListener, viewsCloseable));
	}
	
	@Override
	public void addTab(String title, Icon icon, Component component, String tip)
	{
		int nextTabIndex = super.getTabCount();
		super.addTab(title, icon, component, tip);
		
		super.setTabComponentAt(nextTabIndex, new MaximizableTabbedPaneTab(title, this, component, maximizationListener, viewsCloseable));
	}
}
