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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.prefix.PrefixExpressionContentType;

/**
 * Expression parser details.
 * @author akiran
 */
public class PrefixExpressionDoc extends AbstractDocInfo
{
	private static final long serialVersionUID = 1L;

	/**
	 * Parameter of the parser.
	 * @author akiran
	 */
	public static class Param implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Name of the param.
		 */
		private String name;
		
		/**
		 * Type of the param.
		 */
		private String type;
		
		/**
		 * Default value.
		 */
		private String defaultValue;
		
		/**
		 * Description.
		 */
		private String description;
		
		/**
		 * Flag indicating if this param is mandatory.
		 */
		private boolean mandatory;
		
		public Param()
		{}

		public Param(String name, String type, String defaultValue, boolean mandatory, String description)
		{
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
			this.description = description;
			this.mandatory = mandatory;
		}

		/**
		 * Gets the name of the param.
		 *
		 * @return the name of the param
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the param.
		 *
		 * @param name the new name of the param
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Gets the type of the param.
		 *
		 * @return the type of the param
		 */
		public String getType()
		{
			return type;
		}

		/**
		 * Sets the type of the param.
		 *
		 * @param type the new type of the param
		 */
		public void setType(String type)
		{
			this.type = type;
		}

		/**
		 * Gets the default value.
		 *
		 * @return the default value
		 */
		public String getDefaultValue()
		{
			return defaultValue;
		}

		/**
		 * Sets the default value.
		 *
		 * @param defaultValue the new default value
		 */
		public void setDefaultValue(String defaultValue)
		{
			this.defaultValue = defaultValue;
		}

		/**
		 * Gets the description.
		 *
		 * @return the description
		 */
		public String getDescription()
		{
			return description;
		}

		/**
		 * Sets the description.
		 *
		 * @param description the new description
		 */
		public void setDescription(String description)
		{
			this.description = description;
		}

		public boolean isMandatory()
		{
			return mandatory;
		}

		public void setMandatory(boolean mandatory)
		{
			this.mandatory = mandatory;
		}
	}
	
	/**
	 * Name of the expression parser.
	 */
	private String name;
	
	/**
	 * Description about the parser.
	 */
	private String description;
	
	/**
	 * Expected content type of the parser.
	 */
	private PrefixExpressionContentType contentType;
	
	/**
	 * Examples of the parser.
	 */
	private List<Example> examples = new ArrayList<>();
	
	/**
	 * Params supported by this parser.
	 */
	private List<Param> params;

	/**
	 * Instantiates a new expression parser doc.
	 */
	public PrefixExpressionDoc()
	{}
	
	/**
	 * Instantiates a new expression parser doc.
	 *
	 * @param name the name
	 * @param description the description
	 */
	public PrefixExpressionDoc(String name, String description, PrefixExpressionContentType contentType)
	{
		this.name = name;
		this.description = description;
		this.contentType = contentType;
	}

	/**
	 * Gets the name of the expression parser.
	 *
	 * @return the name of the expression parser
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the expression parser.
	 *
	 * @param name the new name of the expression parser
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description about the parser.
	 *
	 * @return the description about the parser
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about the parser.
	 *
	 * @param description the new description about the parser
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the examples of the parser.
	 *
	 * @return the examples of the parser
	 */
	public List<Example> getExamples()
	{
		return examples;
	}

	/**
	 * Sets the examples of the parser.
	 *
	 * @param examples the new examples of the parser
	 */
	public void setExamples(List<Example> examples)
	{
		this.examples = examples;
	}
	
	/**
	 * Adds the example.
	 *
	 * @param ex the ex
	 */
	public void addExample(Example ex)
	{
		this.examples.add(ex);
	}
	
	public void addExamples(Collection<Example> examples)
	{
		if(examples == null)
		{
			return;
		}
		
		this.examples.addAll(examples);
	}

	public boolean hasExamples()
	{
		return CollectionUtils.isNotEmpty(examples);
	}

	public PrefixExpressionContentType getContentType()
	{
		return contentType;
	}

	public void setContentType(PrefixExpressionContentType contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * Adds the param.
	 *
	 * @param param the param
	 */
	public void addParam(Param param)
	{
		if(this.params == null)
		{
			this.params = new ArrayList<>();
		}
		
		this.params.add(param);
	}
	
	/**
	 * Gets the params supported by this parser.
	 *
	 * @return the params supported by this parser
	 */
	public List<Param> getParams()
	{
		return params;
	}

	/**
	 * Sets the params supported by this parser.
	 *
	 * @param params the new params supported by this parser
	 */
	public void setParams(List<Param> params)
	{
		this.params = params;
	}
	
	public boolean hasParams()
	{
		return CollectionUtils.isNotEmpty(params);
	}
}
