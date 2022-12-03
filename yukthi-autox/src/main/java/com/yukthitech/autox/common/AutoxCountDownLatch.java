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
package com.yukthitech.autox.common;

import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simple count down latch implementation with callback.
 * @author akranthikiran
 */
public class AutoxCountDownLatch
{
	private AtomicInteger count;
	
	private Runnable callback;
	
	public AutoxCountDownLatch(int count)
	{
		if(count <= 0)
		{
			throw new InvalidArgumentException("Invalid count value specified: " + count);
		}
		
		this.count = new AtomicInteger(count);
	}
	
	public void setCallback(Runnable callback)
	{
		this.callback = callback;
	}
	
	public void countDown()
	{
		if(count.get() <= 0)
		{
			throw new InvalidStateException("Count down is already over");
		}
		
		int nextVal = count.decrementAndGet();
		
		if(nextVal == 0)
		{
			callback.run();
		}
	}
}
