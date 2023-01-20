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
package com.yukthitech.autox.prefix;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Expression parser details.
 * 
 * @author akiran
 */
public class PrefixExpressionDetails
{
	/**
	 * Parameter of the parser.
	 * @author akiran
	 */
	public static class Param
	{
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
		 * Flag indicating if this param is mandatory.
		 */
		private boolean mandatory;
		
		/**
		 * Description.
		 */
		private String description;
		
		/**
		 * Instantiates a new param.
		 */
		public Param()
		{}

		/**
		 * Instantiates a new param.
		 *
		 * @param name
		 *            name of the param.
		 * @param type
		 *            type of the param.
		 * @param defaultValue
		 *            default value.
		 * @param description
		 *            description.
		 */
		public Param(String name, String type, String defaultValue, boolean mandatory, String description)
		{
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
			this.mandatory = mandatory;
			this.description = description;
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
	 * Type of the expression supported.
	 */
	private String type;
	
	/**
	 * Description about the parser.
	 */
	private String description;
	
	/**
	 * Example of the parser.
	 */
	private String example;
	
	/**
	 * Enclosing object.
	 */
	private Object enclosingObject;
	
	/**
	 * Parser method.
	 */
	private Method method;
	
	/**
	 * Expected content type of the expression.
	 */
	private PrefixExpressionContentType contentType;
	
	/**
	 * Params supported by this parser.
	 */
	private List<Param> params;
	
	/**
	 * Additional information about the parser.
	 */
	private String additionalInfo;
	
	/**
	 * Instantiates a new expression parser details.
	 */
	public PrefixExpressionDetails()
	{}
	
	/**
	 * Instantiates a new expression parser details.
	 *
	 * @param type the type
	 * @param description the description
	 * @param example the example
	 * @param enclosingObject the enclosing object
	 * @param method the method
	 * @param contentType the content type
	 */
	public PrefixExpressionDetails(String type, String description, String example, Object enclosingObject, Method method, PrefixExpressionContentType contentType)
	{
		this.type = type;
		this.description = description;
		this.example = example;
		this.enclosingObject = enclosingObject;
		this.method = method;
		this.contentType = contentType;
	}

	/**
	 * Gets the type of the expression supported.
	 *
	 * @return the type of the expression supported
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type of the expression supported.
	 *
	 * @param type the new type of the expression supported
	 */
	public void setType(String type)
	{
		this.type = type;
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
	 * Gets the example of the parser.
	 *
	 * @return the example of the parser
	 */
	public String getExample()
	{
		return example;
	}

	/**
	 * Sets the example of the parser.
	 *
	 * @param example the new example of the parser
	 */
	public void setExample(String example)
	{
		this.example = example;
	}

	/**
	 * Gets the enclosing object.
	 *
	 * @return the enclosing object
	 */
	public Object getEnclosingObject()
	{
		return enclosingObject;
	}

	/**
	 * Sets the enclosing object.
	 *
	 * @param enclosingObject the new enclosing object
	 */
	public void setEnclosingObject(Object enclosingObject)
	{
		this.enclosingObject = enclosingObject;
	}

	/**
	 * Gets the parser method.
	 *
	 * @return the parser method
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * Sets the parser method.
	 *
	 * @param method the new parser method
	 */
	public void setMethod(Method method)
	{
		this.method = method;
	}
	
	/**
	 * Fetches if the parser can take of expression value conversion.
	 * @return
	 */
	public boolean isConversionHandled()
	{
		return (method.getParameterTypes().length == 3);
	}
	
	/**
	 * Invokes underlying parser method and returns the result.
	 * @param context context to be used
	 * @param expression expression to be executed
	 * @return result
	 */
	public PrefixEpression invoke(PrefixExpressionContext context, String expression, String expectedType[])
	{
		try
		{
			if(method.getParameterTypes().length == 2)
			{
				return (PrefixEpression) method.invoke(enclosingObject, context, expression);
			}
			else
			{
				return (PrefixEpression) method.invoke(enclosingObject, context, expression, expectedType);
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing expression '{}' of type '{}' using parser method: {}.{}", 
					expression, type, method.getDeclaringClass().getName(), method.getName());
		}
	}
	
	/**
	 * Sets the expected content type of the expression.
	 *
	 * @param contentType the new expected content type of the expression
	 */
	public void setContentType(PrefixExpressionContentType contentType)
	{
		this.contentType = contentType;
	}
	
	/**
	 * Gets the expected content type of the expression.
	 *
	 * @return the expected content type of the expression
	 */
	public PrefixExpressionContentType getContentType()
	{
		return contentType;
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
	
	/**
	 * Gets the additional information about the parser.
	 *
	 * @return the additional information about the parser
	 */
	public String getAdditionalInfo()
	{
		return additionalInfo;
	}

	/**
	 * Sets the additional information about the parser.
	 *
	 * @param additionalInfo
	 *            the new additional information about the parser
	 */
	public void setAdditionalInfo(String additionalInfo)
	{
		this.additionalInfo = additionalInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Type: ").append(type);
		builder.append(",").append("Method: ").append(method.getDeclaringClass().getName()).append(".").append(method.getName()).append("()");

		builder.append("]");
		return builder.toString();
	}

}
