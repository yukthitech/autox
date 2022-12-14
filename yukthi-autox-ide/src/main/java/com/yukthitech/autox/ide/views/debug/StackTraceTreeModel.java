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
package com.yukthitech.autox.ide.views.debug;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused.StackElement;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

public class StackTraceTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	public static class ThreadNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;
		
		public ThreadNode(String name)
		{
			super(name);
		}
	}

	public static class StackElementNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;
		
		private StackElement element;
		private String stringRep;
		
		public StackElementNode(StackElement elem)
		{
			this.element = elem;
			
			File path = new File(elem.getFile());
			this.stringRep = path.getName() + ":" + elem.getLineNumber();
		}
		
		public StackElement getElement()
		{
			return element;
		}
		
		@Override
		public String toString()
		{
			return stringRep;
		}
	}
	
	private LinkedHashMap<String, DefaultMutableTreeNode> pausedExecutions = new LinkedHashMap<>();
	
	private DefaultMutableTreeNode root;
	
	public StackTraceTreeModel()
	{
		super(new DefaultMutableTreeNode("root"));
		
		this.root = (DefaultMutableTreeNode) super.getRoot();
	}

	public void addOrUpdate(ServerMssgExecutionPaused mssg)
	{
		addOrUpdate(mssg, true);
	}
	
	private synchronized void addOrUpdate(ServerMssgExecutionPaused mssg, boolean raiseEvent)
	{
		DefaultMutableTreeNode node = pausedExecutions.get(mssg.getExecutionId());
		boolean existing = (node != null);
		
		if(node == null)
		{
			node = new ThreadNode(mssg.getName());
			pausedExecutions.put(mssg.getExecutionId(), node);
			
			root.add(node);
		}
		else
		{
			node.removeAllChildren();
		}
		
		List<StackElement> stackTrace = new ArrayList<>(mssg.getStackTrace());
		Collections.reverse(stackTrace);
		
		for(ServerMssgExecutionPaused.StackElement elem : stackTrace)
		{
			node.add(new StackElementNode(elem));
		}
		
		if(raiseEvent)
		{
			if(existing)
			{
				super.nodeStructureChanged(node);
			}
			else
			{
				super.nodesWereInserted(root, new int[] {pausedExecutions.size() - 1});
			}
		}
	}
	
	public synchronized void removeStackTrace(String executionId)
	{
		DefaultMutableTreeNode node = pausedExecutions.remove(executionId);
		
		if(node == null)
		{
			return;
		}
		
		int index = root.getIndex(node);
		root.remove(node);
		
		super.nodesWereRemoved(root, new int[] {index}, new Object[] {node});
	}
	
	public synchronized void setEnvironment(ExecutionEnvironment env)
	{
		root.removeAllChildren();
		
		if(env != null)
		{
			env.visitPausedThreads(mssg -> 
			{
				addOrUpdate(mssg, false);
			});
		}
		
		super.nodeStructureChanged(root);
	}
}
