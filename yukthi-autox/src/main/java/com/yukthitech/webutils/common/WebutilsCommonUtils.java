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
package com.yukthitech.webutils.common;

import java.lang.reflect.Method;

/**
 * Contains common util methods required by webutils.
 * @author akiran
 */
public class WebutilsCommonUtils
{
	/**
	 * Generates bare minimal method signature that can be used to identify method uniquely.
	 * @param method Method for which signature needs to be generated
	 * @return Bare minimal method signature
	 */
	public static String getMethodSignature(Method method)
	{
		StringBuilder builder = new StringBuilder(method.getName());
		
		builder.append("(");
		
		Class<?> paramTypes[] = method.getParameterTypes();
		
		if(paramTypes.length > 0)
		{
			for(Class<?> ptype : paramTypes)
			{
				if(ptype.getName().contains("MultipartHttpServletRequest"))
				{
					ptype = Object.class;
				}
				
				builder.append(ptype.getName()).append(",");
			}
			
			builder.deleteCharAt(builder.length() - 1);
		}
		
		builder.append(")");
		return builder.toString();
	}
}
