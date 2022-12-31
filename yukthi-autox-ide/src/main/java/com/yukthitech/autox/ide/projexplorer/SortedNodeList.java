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
package com.yukthitech.autox.ide.projexplorer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openqa.selenium.InvalidArgumentException;

public class SortedNodeList
{
	public static class NodeWrapper
	{
		private int index;
		
		private BaseTreeNode node;
		
		private NodeWrapper previous;
		
		private NodeWrapper next;

		private NodeWrapper(int index, BaseTreeNode node, NodeWrapper previous, NodeWrapper next)
		{
			this.index = index;
			this.node = node;
			this.previous = previous;
			this.next = next;
		}

		private void incrementIndex()
		{
			index++;
			
			if(next != null)
			{
				next.incrementIndex();
			}
		}

		private void decrementIndex()
		{
			index--;
			
			if(next != null)
			{
				next.decrementIndex();
			}
		}
		
		public int getIndex()
		{
			return index;
		}
		
		public BaseTreeNode getNode()
		{
			return node;
		}
	}
	
	private TreeMap<String, NodeWrapper> map = new TreeMap<>();
	
	public synchronized int add(BaseTreeNode node)
	{
		String nodeId = node.getId();
		Map.Entry<String, NodeWrapper> floorEntry = map.floorEntry(nodeId);
		
		//floor entry indicates either map is empty or
		// node is being added should be at start.
		if(floorEntry == null)
		{
			Map.Entry<String, NodeWrapper> firstEntry = map.firstEntry();
			
			//set index as -1, as full tree post new node has to increment post addition
			NodeWrapper wrapper = new NodeWrapper(-1, node, null, firstEntry != null ? firstEntry.getValue() : null);
			map.put(nodeId, wrapper);
			
			wrapper.incrementIndex();
			return 0;
		}

		//if id already exist throw exception
		if(floorEntry.getKey().equals(nodeId))
		{
			throw new InvalidArgumentException("A node with specified id already exist: " + nodeId);
		}
		
		//set index same as floor node, as full tree post new node has to increment post addition
		NodeWrapper floorWrapper = floorEntry.getValue();

		NodeWrapper wrapper = new NodeWrapper(floorWrapper.index, node, floorWrapper, floorWrapper.next);
		floorWrapper.next = wrapper;
		map.put(nodeId, wrapper);
		
		wrapper.incrementIndex();
		return wrapper.index;
	}
	
	public synchronized NodeWrapper remove(String id)
	{
		NodeWrapper wrapper = map.remove(id);
		
		if(wrapper == null)
		{
			return null;
		}
		
		NodeWrapper previous = wrapper.previous;
		NodeWrapper next = wrapper.next;
		
		if(previous != null)
		{
			previous.next = next;
		}
		
		if(next != null)
		{
			next.previous = previous;
			next.decrementIndex();
		}
		
		return wrapper;
	}
	
	public synchronized int indexOf(String id)
	{
		NodeWrapper wrapper = map.get(id);
		
		if(wrapper == null)
		{
			return -1;
		}
		
		return wrapper.index;
	}
	
	public synchronized BaseTreeNode get(String id)
	{
		NodeWrapper wrapper = map.get(id);
		
		if(wrapper == null)
		{
			return null;
		}
		
		return wrapper.node;
	}

	public synchronized List<BaseTreeNode> getNodes()
	{
		return map.values().stream().map(wrapper -> wrapper.node).collect(Collectors.toList());
	}
	
	public synchronized void clear()
	{
		map.clear();
	}
	
	public synchronized Set<String> idSet()
	{
		return new HashSet<>(map.keySet());
	}
	
	/**
	 * Calls the specified visitor for each of the node. The visiting will stop end or if the 
	 * visitor returns false.
	 * @param visitor
	 */
	public synchronized void visit(Function<BaseTreeNode, Boolean> visitor)
	{
		for(NodeWrapper wrapper : map.values())
		{
			Boolean res = visitor.apply(wrapper.node);
			
			if(Boolean.FALSE.equals(res))
			{
				break;
			}
		}
	}
}
