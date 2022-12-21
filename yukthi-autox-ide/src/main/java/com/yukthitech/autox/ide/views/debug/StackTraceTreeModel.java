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
import javax.swing.tree.TreePath;

import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused.StackElement;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class StackTraceTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	public static class BaseNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;
		
		private String executionId;

		public BaseNode(String executionId)
		{
			this.executionId = executionId;
		}
		
		public String getExecutionId()
		{
			return executionId;
		}
	}
	
	public static class ThreadNode extends BaseNode
	{
		private static final long serialVersionUID = 1L;
		
		private String name;
		
		public ThreadNode(String executionId, String name)
		{
			super(executionId);
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}

	public static class StackElementNode extends BaseNode
	{
		private static final long serialVersionUID = 1L;
		
		private StackElement element;
		private String stringRep;
		
		public StackElementNode(String executionId, StackElement elem)
		{
			super(executionId);
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
	
	private LinkedHashMap<String, ThreadNode> pausedExecutions = new LinkedHashMap<>();
	
	/**
	 * Used to maintain executions in sorted order.
	 */
	private List<String> executionNames = new ArrayList<>();
	
	private DefaultMutableTreeNode root;
	
	public StackTraceTreeModel()
	{
		super(new DefaultMutableTreeNode("root"));
		
		this.root = (DefaultMutableTreeNode) super.getRoot();
	}
	
	public TreePath getPath(String executionId)
	{
		DefaultMutableTreeNode threadNode = pausedExecutions.get(executionId);
		
		if(threadNode == null)
		{
			return null;
		}
		
		return new TreePath(new Object[] {root, threadNode});
	}

	public void addOrUpdate(ServerMssgExecutionPaused mssg)
	{
		addOrUpdate(mssg, true);
	}
	
	private synchronized void addOrUpdate(ServerMssgExecutionPaused mssg, boolean raiseEvent)
	{
		ThreadNode node = pausedExecutions.get(mssg.getExecutionId());
		boolean existing = (node != null);
		int idx = 0;
		
		if(node == null)
		{
			node = new ThreadNode(mssg.getExecutionId(), mssg.getName());
			pausedExecutions.put(mssg.getExecutionId(), node);
			
			idx = Collections.binarySearch(executionNames, mssg.getName());
			
			if(idx >= 0)
			{
				//this is never suppose to happen
				throw new InvalidStateException("Multiple executions found with same name: {}", mssg.getName());
			}
			
			idx = (0 - idx) - 1;
			root.insert(node, idx);
			
			executionNames.add(idx, mssg.getName());
		}
		else
		{
			node.removeAllChildren();
		}
		
		List<StackElement> stackTrace = new ArrayList<>(mssg.getStackTrace());
		
		for(ServerMssgExecutionPaused.StackElement elem : stackTrace)
		{
			node.add(new StackElementNode(mssg.getExecutionId(), elem));
		}
		
		if(raiseEvent)
		{
			if(existing)
			{
				super.nodeStructureChanged(node);
			}
			else
			{
				super.nodesWereInserted(root, new int[] {idx});
				//super.reload();
			}
		}
	}
	
	public synchronized void removeStackTrace(String executionId)
	{
		ThreadNode node = pausedExecutions.remove(executionId);
		
		if(node == null)
		{
			return;
		}
		
		int index = root.getIndex(node);
		root.remove(node);
		
		executionNames.remove(node.name);
		
		super.nodesWereRemoved(root, new int[] {index}, new Object[] {node});
	}
	
	public synchronized void setEnvironment(ExecutionEnvironment env)
	{
		executionNames.clear();
		pausedExecutions.clear();

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
