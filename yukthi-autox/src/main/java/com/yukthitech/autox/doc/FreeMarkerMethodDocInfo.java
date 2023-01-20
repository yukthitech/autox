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
package com.yukthitech.autox.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Documentation of free marker method.
 * 
 * @author akiran
 */
public class FreeMarkerMethodDocInfo extends AbstractDocInfo
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the document.
	 */
	private String name;

	/**
	 * Return type of the function.
	 */
	private String returnType;

	/**
	 * Description about the method.
	 */
	private String description;

	/**
	 * Description of return value.
	 */
	private String returnDescription;

	/**
	 * Parameters of the method.
	 */
	private List<ParamInfo> parameters;

	/**
	 * Examples of usage.
	 */
	private List<Example> examples;

	private String parameterString;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getReturnDescription()
	{
		return returnDescription;
	}

	public void setReturnDescription(String returnDescription)
	{
		this.returnDescription = returnDescription;
	}

	public List<ParamInfo> getParameters()
	{
		return parameters;
	}

	public void setParameters(List<ParamInfo> parameters)
	{
		this.parameters = parameters;
	}
	
	public void addParameter(ParamInfo param)
	{
		if(this.parameters == null)
		{
			this.parameters = new ArrayList<>();
		}
		
		this.parameters.add(param);
	}
	
	public boolean hasParameters()
	{
		return CollectionUtils.isNotEmpty(parameters);
	}

	public List<Example> getExamples()
	{
		return examples;
	}

	public void setExamples(List<Example> examples)
	{
		this.examples = examples;
	}
	
	public void addExample(Example example)
	{
		if(this.examples == null)
		{
			this.examples = new ArrayList<>();
		}
		
		this.examples.add(example);
	}
	
	public void addExamples(Collection<Example> examples)
	{
		if(examples == null)
		{
			return;
		}
		
		if(this.examples == null)
		{
			this.examples = new ArrayList<Example>();
		}
		
		this.examples.addAll(examples);
	}

	public boolean hasExamples()
	{
		return CollectionUtils.isNotEmpty(examples);
	}

	public void setParameterString(String parameterString)
	{
		this.parameterString = parameterString;
	}

	public String getParameterString()
	{
		if(parameterString != null)
		{
			return parameterString;
		}

		if(parameters == null || parameters.size() == 0)
		{
			parameterString = "()";
			return parameterString;
		}

		StringBuilder builder = new StringBuilder("(");

		for(ParamInfo param : parameters)
		{
			builder.append(param.getName()).append(", ");
		}

		builder.delete(builder.length() - 2, builder.length() - 1);
		builder.append(")");

		parameterString = builder.toString();
		return parameterString;
	}
	
	@Override
	public boolean hasReturnInfo()
	{
		return true;
	}
}
