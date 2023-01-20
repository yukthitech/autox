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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.Param;

/**
 * Information about a step.
 * @author akiran
 */
public class StepInfo extends AbstractDocInfo implements Comparable<StepInfo>, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the step.
	 */
	private String name;
	
	/**
	 * Title of the executable.
	 */
	private String title;
	
	/**
	 * Group to which this step belongs to.
	 */
	private String group;
	
	/**
	 * Description about the step.
	 */
	private String description;
	
	/**
	 * Java class representing this step.
	 */
	private String javaType;
	
	/**
	 * List of params accepted by this step.
	 */
	private Map<String, ParamInfo> params = new TreeMap<>();
	
	/**
	 * Child elements of this step.
	 */
	private Map<String, ElementInfo> childElements = new TreeMap<>();
	
	/**
	 * Plugins required by this step.
	 */
	private Set<String> requiredPlugins = new TreeSet<>();
	
	/**
	 * Name to be used with hyphens.
	 */
	private String nameWithHyphens;
	
	/**
	 * Examples for this step info.
	 */
	private List<Example> examples;
	
	/**
	 * Instantiates a new step info.
	 *
	 * @param stepClass the step class
	 * @param executablAnnot the executabl annot
	 */
	public StepInfo(Class<? extends IStep> stepClass, Executable executablAnnot, List<Example> examples)
	{
		setDetails(executablAnnot.name(), executablAnnot.message());

		this.javaType = stepClass.getName();
		this.group = executablAnnot.group().toString();

		loadParams(stepClass);

		for(Class<?> pluginType : executablAnnot.requiredPluginTypes())
		{
			this.requiredPlugins.add(pluginType.getSimpleName());
		}
		
		this.examples = examples;
	}
	
	protected StepInfo()
	{}
	
	protected void setDetails(String name, String description)
	{
		this.name = name;
		this.description = description;
		this.nameWithHyphens = name.replaceAll("([A-Z])", "-$1").toLowerCase();
		this.title = name.replaceAll("([A-Z])", " $1").toLowerCase();
	}

	protected void loadParams(Class<?> stepClass)
	{
		Class<?> curType = stepClass;
		Param param = null;
		
		while(curType != null)
		{
			if(curType.getName().startsWith("java"))
			{
				break;
			}
			
			for(Field field : curType.getDeclaredFields())
			{
				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				
				param = field.getAnnotation(Param.class);
				
				if(param == null)
				{
					continue;
				}
				
				ParamInfo paramInfo = new ParamInfo(field, param); 
				this.params.put(paramInfo.getName(), paramInfo);
			}
			
			curType = curType.getSuperclass();
		}
		
		ChildElement childElement = null;
		
		for(Method method : stepClass.getMethods())
		{
			if(Modifier.isStatic(method.getModifiers()))
			{
				continue;
			}
			
			childElement = method.getAnnotation(ChildElement.class);
			
			if(childElement == null)
			{
				continue;
			}
			
			ElementInfo elemInfo = new ElementInfo(method, childElement);
			this.childElements.put(elemInfo.getName(), elemInfo);
		}
	}

	/**
	 * Gets the name of the step.
	 *
	 * @return the name of the step
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the description about the step.
	 *
	 * @return the description about the step
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Gets the java class representing this step.
	 *
	 * @return the java class representing this step
	 */
	public String getJavaType()
	{
		return javaType;
	}

	/**
	 * Gets the list of params accepted by this step.
	 *
	 * @return the list of params accepted by this step
	 */
	public Collection<ParamInfo> getParams()
	{
		return params.values();
	}
	
	/**
	 * Fetches param info for specified name.
	 * @param name name of param to fetch
	 * @return matching param
	 */
	public ParamInfo getParam(String name)
	{
		if(params == null)
		{
			return null;
		}
		
		return params.get(name);
	}
	
	/**
	 * Gets the child elements of this step.
	 *
	 * @return the child elements of this step
	 */
	public Collection<ElementInfo> getChildElements()
	{
		return childElements.values();
	}
	
	/**
	 * Fetches child element info with specified name.
	 * @param name name of child fetch.
	 * @return matching child element.
	 */
	public ElementInfo getChildElement(String name)
	{
		return childElements.get(name);
	}
	
	/**
	 * Gets the plugins required by this step.
	 *
	 * @return the plugins required by this step
	 */
	public Set<String> getRequiredPlugins()
	{
		return requiredPlugins;
	}
	
	/**
	 * Gets the name to be used with hyphens.
	 *
	 * @return the name to be used with hyphens
	 */
	public String getNameWithHyphens()
	{
		return nameWithHyphens;
	}
	
	/**
	 * Returns flag indicating if this is executable step.
	 * @return
	 */
	public boolean isExecutable()
	{
		return true;
	}
	
	/**
	 * Sets the examples for this step info.
	 *
	 * @param examples the new examples for this step info
	 */
	protected void setExamples(List<Example> examples)
	{
		this.examples = examples;
	}
	
	/**
	 * Gets the examples for this step info.
	 *
	 * @return the examples for this step info
	 */
	public List<Example> getExamples()
	{
		return examples;
	}
	
	public boolean hasExamples()
	{
		return CollectionUtils.isNotEmpty(examples);
	}
	
	/**
	 * Gets the title of the executable.
	 *
	 * @return the title of the executable
	 */
	public String getTitle()
	{
		return title;
	}
	
	public String getGroup()
	{
		return group;
	}
	
	public String getType()
	{
		return "Step";
	}

	@Override
	public int compareTo(StepInfo o)
	{
		return name.compareTo(o.name);
	}
}
