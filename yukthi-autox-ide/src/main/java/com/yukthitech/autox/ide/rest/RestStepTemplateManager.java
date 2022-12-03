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
package com.yukthitech.autox.ide.rest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openqa.selenium.InvalidArgumentException;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to manage rest step templates.
 * @author akiran
 */
@Component
public class RestStepTemplateManager
{
	private Map<String, String> templates = new HashMap<>();
	
	@PostConstruct
	private void init()
	{
		try
		{
			InputStream is = RestStepTemplateManager.class.getResourceAsStream("/rest-step-templates.xml");
			XMLBeanParser.parse(is, this);
			is.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading rest step templates", ex);
		}
	}
	
	public void addStepTemplate(String name, String value)
	{
		templates.put(name, value);
	}
	
	/**
	 * Generates step code for specified step name with specified context.
	 * @param stepName step name to execute
	 * @param context context to be used to process step.
	 * @return generated step code.
	 */
	public String generateStep(String stepName, Object context)
	{
		String template = templates.get(stepName);
		
		if(template == null)
		{
			throw new InvalidArgumentException("Invalid rest step name specified for template processing: " + stepName);
		}
		
		return FreeMarkerMethodManager.replaceExpressions("rest-step-template:" + stepName, context, template);
	}
}
