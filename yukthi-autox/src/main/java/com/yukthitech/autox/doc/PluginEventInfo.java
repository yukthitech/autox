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
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Param;
import com.yukthitech.autox.plugin.PluginEvent;

/**
 * Information about a plugin event.
 * 
 * @author akiran
 */
public class PluginEventInfo implements Comparable<PluginEventInfo>, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the event.
	 */
	private String name;

	/**
	 * Description about the event.
	 */
	private String description;

	/**
	 * Provides the description on how the return value from handler will be
	 * used..
	 */
	private String returnDescription;

	/**
	 * List of params available in the handler for this event.
	 */
	private Set<ParamInfo> params;

	/**
	 * Instantiates a new plugin info.
	 *
	 * @param pluginClass the plugin class
	 * @param executablAnnot the executabl annot
	 */
	public PluginEventInfo(PluginEvent eventAnnot)
	{
		this.name = eventAnnot.name();
		this.description = eventAnnot.description();

		this.returnDescription = eventAnnot.returnDescription();
		this.returnDescription = StringUtils.isBlank(this.returnDescription) ? null : this.returnDescription;

		Param paramAnnots[] = eventAnnot.params();

		if(paramAnnots.length > 0)
		{
			this.params = new TreeSet<>();

			for(Param paramAnnot : paramAnnots)
			{
				this.params.add(new ParamInfo(paramAnnot.name(), paramAnnot.description(), null));
			}
		}
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public String getReturnDescription()
	{
		return returnDescription;
	}

	public Set<ParamInfo> getParams()
	{
		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PluginEventInfo o)
	{
		return name.compareTo(o.name);
	}
}
