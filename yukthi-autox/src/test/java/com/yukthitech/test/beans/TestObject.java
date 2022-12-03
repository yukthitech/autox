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
package com.yukthitech.test.beans;

import java.io.Serializable;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

public class TestObject implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	String name;
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toText(Object obj)
	{
		return name + ":" + obj;
	}
	
	public String toText(NameBean other)
	{
		return name + "=>" + other;
	}

	public void throwError()
	{
		throw new InvalidArgumentException("MESSAGE");
	}
	
	public String validate(NameBean bean)
	{
		String name = bean.getName();
		
		if(name.length() <= 3)
		{
			throw new InvalidArgumentException("Name should be greater than 3");
		}

		if(name.length() >= 6)
		{
			throw new InvalidArgumentException("Name should be less than 6");
		}
		
		return bean.getName();
	}
}
