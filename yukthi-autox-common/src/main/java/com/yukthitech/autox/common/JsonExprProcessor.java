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

import com.yukthitech.transform.TransformEngine;
import com.yukthitech.transform.template.JsonTemplateFactory;
import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Processes JEL/json-expression templates via {@link TransformEngine}.
 * @author akiran
 */
public final class JsonExprProcessor
{
	private static final TransformEngine TRANSFORM_ENGINE = new TransformEngine(FreeMarkerMethodManager.getFreeMarkerEngine());

	private static final JsonTemplateFactory JSON_TEMPLATE_FACTORY = new JsonTemplateFactory(FreeMarkerMethodManager.getFreeMarkerEngine());

	private JsonExprProcessor()
	{
	}

	/**
	 * Process the specified json with specified context and returns the result as json string.
	 * @param json json to process
	 * @param context context to be used
	 * @return processed json
	 */
	public static String processJson(String json, Object context)
	{
		TransformTemplate template = JSON_TEMPLATE_FACTORY.parseTemplate("json", json);
		return TRANSFORM_ENGINE.processAsString(template, context);
	}

	/**
	 * Process the specified json with specified context and returns the result as object (not json).
	 * @param json json to process
	 * @param context context to be used
	 * @return processed object
	 */
	public static Object processJsonAsObject(String json, Object context)
	{
		TransformTemplate template = JSON_TEMPLATE_FACTORY.parseTemplate("json", json);
		return TRANSFORM_ENGINE.process(template, context);
	}

	/**
	 * Process the specified object with specified context and returns the result.
	 * @param object object to process
	 * @param context context to be used
	 * @return processed object
	 */
	public static Object processObject(Object object, Object context)
	{
		String json;

		try
		{
			json = IAutomationConstants.OBJECT_MAPPER.writeValueAsString(object);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while writing json content.", ex);
		}

		return processJsonAsObject(json, context);
	}
}
