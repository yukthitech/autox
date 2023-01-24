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
package com.yukthitech.prism.state;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.prism.events.IdeClosingEvent;
import com.yukthitech.prism.events.IdeOpeningEvent;
import com.yukthitech.prism.model.IdeState;
import com.yukthitech.prism.services.ClassScannerService;
import com.yukthitech.prism.services.IdeEventHandler;

/**
 * Responsible for storing and loading state of different components.
 * @author akranthikiran
 */
@Service
public class ComponentStateManager
{
	@Autowired
	private ClassScannerService classScannerService;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@IdeEventHandler
	public void onIdeOpening(IdeOpeningEvent event)
	{
		Set<Class<?>> clsLst = classScannerService.getClassesWithAnnotation(PersistableState.class);
		
		IdeState ideState = event.getIdeState();
		
		for(Class<?> cls : clsLst)
		{
			Object service = applicationContext.getBean(cls);
			BeanState beanState = (BeanState) ideState.getAttribute(cls.getName() + ".state");
			
			if(beanState != null)
			{
				beanState.loadState(service, cls);
			}
		}
	}

	@IdeEventHandler
	public void onIdeClosing(IdeClosingEvent event)
	{
		Set<Class<?>> clsLst = classScannerService.getClassesWithAnnotation(PersistableState.class);
		
		IdeState ideState = event.getIdeState();
		
		for(Class<?> cls : clsLst)
		{
			Object service = applicationContext.getBean(cls);
			BeanState beanState = new BeanState(service, cls);
			
			ideState.setAtribute(cls.getName() + ".state", beanState);
		}
	}
}
