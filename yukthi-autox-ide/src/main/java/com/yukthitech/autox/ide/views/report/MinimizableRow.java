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
package com.yukthitech.autox.ide.views.report;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MinimizableRow<T>
{
	private boolean minimized;
	
	private List<T> children = new LinkedList<>();

	public boolean isMinimized()
	{
		return minimized;
	}

	public void setMinimized(boolean minimized)
	{
		this.minimized = minimized;
	}
	
	public void flipMinimizedStatus()
	{
		this.minimized = !minimized;
	}
	
	public void addChild(T child)
	{
		children.add(child);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void populateChildRows(Consumer<Object> consumer)
	{
		consumer.accept(this);
		
		if(minimized)
		{
			return;
		}
		
		for(Object child : children)
		{
			if(child instanceof MinimizableRow)
			{
				((MinimizableRow) child).populateChildRows(consumer);
			}
			else
			{
				consumer.accept(child);
			}
		}
	}
}
