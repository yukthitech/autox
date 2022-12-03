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

import java.lang.reflect.Field;

import com.yukthitech.utils.cli.CliArgument;

/**
 * Represents information about the command line argument.
 * 
 * @author akiran
 */
public class CommandLineArgInfo implements Comparable<CommandLineArgInfo>
{
	/**
	 * Short name of the argument.
	 */
	private String shortName;

	/**
	 * Long name of the argument.
	 */
	private String longName;

	/**
	 * Description about the plugin.
	 */
	private String description;

	/**
	 * Flag indicating if this argument is mandatory or not.
	 */
	private boolean mandatory;

	/**
	 * Java type of the argument.
	 */
	private String type;

	/**
	 * Instantiates a new command line arg info.
	 *
	 * @param field the field
	 * @param argAnnot the arg annot
	 */
	public CommandLineArgInfo(Field field, CliArgument argAnnot)
	{
		this.shortName = argAnnot.name();
		this.longName = argAnnot.longName();
		this.description = argAnnot.description();
		this.mandatory = argAnnot.required();

		this.type = field.getType().getName();
	}

	/**
	 * Gets the short name of the argument.
	 *
	 * @return the short name of the argument
	 */
	public String getShortName()
	{
		return shortName;
	}

	/**
	 * Gets the long name of the argument.
	 *
	 * @return the long name of the argument
	 */
	public String getLongName()
	{
		return longName;
	}

	/**
	 * Gets the description about the plugin.
	 *
	 * @return the description about the plugin
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Checks if is flag indicating if this argument is mandatory or not.
	 *
	 * @return the flag indicating if this argument is mandatory or not
	 */
	public boolean isMandatory()
	{
		return mandatory;
	}

	/**
	 * Gets the java type of the argument.
	 *
	 * @return the java type of the argument
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(CommandLineArgInfo o)
	{
		return shortName.compareTo(o.shortName);
	}
}
