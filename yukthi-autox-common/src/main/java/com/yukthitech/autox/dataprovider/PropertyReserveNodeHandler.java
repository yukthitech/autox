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
package com.yukthitech.autox.dataprovider;

import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.Locator;

import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.IHybridTextBean;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;
import com.yukthitech.utils.exceptions.InvalidStateException;

@NodeName(namePattern = "property")
public class PropertyReserveNodeHandler implements IReserveNodeHandler
{
	private static final String ATTR_NAME = "name";
	
	public static class TextBean implements IHybridTextBean
	{
		@SuppressWarnings("unused")
		private String name;
		private StringBuilder text = new StringBuilder();
		
		public void setName(String name) 
		{
			this.name = name;
		}

		@Override
		public void addText(String text) 
		{
			this.text.append(text);
		}
	}
	
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		return new TextBean();
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		//replace the modified bean on the node. So that, that will take effect
		TextBean cloneInfo = (TextBean) node.getActualBean();
		Object bean = PrefixExpressionFactory.getExpressionFactory().getValueByExpression(AutomationContext.getInstance(), cloneInfo.text.toString());
		
		String propName = att.get(ATTR_NAME, null);
		Object parent = node.getParent();
		
		if(parent instanceof DynamicBean)
		{
			((DynamicBean) parent).add(propName, bean);
			return;
		}
		
		try
		{
			PropertyUtils.setProperty(parent, propName, bean);
		}catch(Exception ex)
		{
			String className = parent != null ? parent.getClass().getName() : "null";
			throw new InvalidStateException("An error occurred while setting property '{}' on bean of type: {}", propName, className, ex);
		}
	}
}
