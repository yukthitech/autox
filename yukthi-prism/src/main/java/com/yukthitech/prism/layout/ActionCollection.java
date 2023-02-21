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
package com.yukthitech.prism.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.JComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.services.GlobalKeyboardListener;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Collection of actions.
 * @author akiran
 */
@Component
public class ActionCollection
{
	private static Logger logger = LogManager.getLogger(ActionCollection.class);
	
	/**
	 * Executable actions.
	 * @author akiran
	 */
	public static class ExecutableAction
	{
		/**
		 * Object which holds the action method.
		 */
		private Object object;
		
		/**
		 * Method to be invoked.
		 */
		private Method method;
		
		private boolean actionEventRequired = false;

		public ExecutableAction(Object object, Method method, boolean actionEventRequired)
		{
			this.object = object;
			this.method = method;
			this.actionEventRequired = actionEventRequired;
		}
	}
	
	private class IdeActionListener implements ActionListener
	{
		private String action;
		private IdePopupMenu idePopupMenu;
		
		public IdeActionListener(String action, IdePopupMenu idePopupMenu)
		{
			this.action = action;
			this.idePopupMenu = idePopupMenu;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			invokeAction(action, idePopupMenu != null ? idePopupMenu.getLastSource() : null);			
		}
	}
	
	private Map<String, ExecutableAction> actions = new HashMap<>();
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private GlobalKeyboardListener globalKeyboardListener;
	
	@PostConstruct
	private void init()
	{
		Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ActionHolder.class);
		
		for(Object bean : beanMap.values())
		{
			addActionHolder(bean, AopProxyUtils.ultimateTargetClass(bean));
		}
	}
	
	private void addActionHolder(Object actionHolder, Class<?> type)
	{
		logger.debug("Registering action holder of type: {}", type.getName());
		
		Method methods[] = type.getMethods();
		boolean actionEventRequired = false;
		
		for(Method met : methods)
		{
			if(Modifier.isStatic(met.getModifiers()) || met.getAnnotation(Action.class) == null || met.getParameterTypes().length > 1)
			{
				continue;
			}
			
			actionEventRequired = false;
			
			if(met.getParameterTypes().length == 1)
			{
				if(!IdeActionEvent.class.equals(met.getParameterTypes()[0]))
				{
					continue;
				}
				
				actionEventRequired = true;
			}
			
			logger.debug("Registering action '{}' defined by: {}", met.getName(), met.getDeclaringClass().getName());
			actions.put(met.getName(), new ExecutableAction(actionHolder, met, actionEventRequired));
		}
	}
	
	public void invokeAction(String name, Object actionSource)
	{
		//logger.debug("Invoking action with name: {}", name);

		IdeUtils.execute(() -> 
		{
			try
			{
				ExecutableAction action = actions.get(name);
				
				if(action.actionEventRequired)
				{
					action.method.invoke(action.object, new IdeActionEvent(actionSource));
				}
				else
				{
					action.method.invoke(action.object);
				}
			}catch(Exception ex)
			{
				logger.error("An error occurred while invoking action: {}", name, ex);
			}
		}, 0);
	}
	
	public void registerGlobalAction(ShortKey shortKey, String action, JComponent component)
	{
		globalKeyboardListener.addGlobalKeyListener(shortKey, getActionListener(action, null), component);
	}
	
	public ActionListener getActionListener(String action, IdePopupMenu idePopup)
	{
		if(!actions.containsKey(action))
		{
			throw new InvalidArgumentException("Invalid action name specified: " + action);
		}
		
		return new IdeActionListener(action, idePopup);
	}
}
