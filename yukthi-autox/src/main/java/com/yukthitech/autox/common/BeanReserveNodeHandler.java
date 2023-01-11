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

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.xml.sax.Locator;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;

@NodeName(namePattern = "registerBean")
public class BeanReserveNodeHandler implements IReserveNodeHandler
{
	public static class BeanInfo
	{
		private String id;
		
		private String value;
		
		public void setId(String id)
		{
			this.id = id;
		}
		
		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		return new BeanInfo();
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		BeanInfo info = (BeanInfo) node.getActualBean();
		
		if(StringUtils.isEmpty(info.id))
		{
			throw new InvalidArgumentException("No id specified for bean registration");
		}
		
		if(StringUtils.isBlank(info.value))
		{
			throw new InvalidArgumentException("No value specified for bean registration");
		}
		
		AutomationContext automationContext = AutomationContext.getInstance();
		PrefixExpressionFactory exprFactory = PrefixExpressionFactory.getExpressionFactory();
		
		Object bean = exprFactory.getValueByExpression(automationContext, info.value);
		parserHandler.registerBean(info.id, bean);
	}
}
