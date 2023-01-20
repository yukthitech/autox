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
package com.yukthitech.autox.ide.services;

import java.nio.charset.Charset;

import javax.swing.text.JTextComponent;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.doc.DocGenerator;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.FreeMarkerMethodDocInfo;
import com.yukthitech.autox.doc.ParamInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.events.ActiveComponentChangedEvent;
import com.yukthitech.autox.ide.help.HelpPanel;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages global state of the ide, on which other components may depend up on.
 * 
 * @author akranthikiran
 */
@Service
public class GlobalStateManager
{
	@Autowired
	private IdeEventManager ideEventManager;
	
	/**
	 * Maintains the current active editor which has focus.
	 * This in turn is used by find operation.
	 */
	private JTextComponent activeComponent;
	
	/**
	 * Autox doc information.
	 */
	private DocInformation docInformation;
	
	public synchronized void focusGained(JTextComponent component)
	{
		if(activeComponent == component)
		{
			return;
		}
		
		this.activeComponent = component;
		ideEventManager.raiseAsyncEvent(new ActiveComponentChangedEvent(component));
	}
	
	public synchronized void componentRemoved(JTextComponent component)
	{
		if(this.activeComponent == component)
		{
			this.activeComponent = null;
			ideEventManager.raiseAsyncEvent(new ActiveComponentChangedEvent(null));
		}
	}
	
	public JTextComponent getActiveComponent()
	{
		return activeComponent;
	}
	
	private DocInformation buildDocInfo() throws Exception
	{
		String documentTemplate = null;
		String fmMethTemp = null;
		String paramTemp = null;
		
		try
		{
			documentTemplate = IOUtils.toString(HelpPanel.class.getResourceAsStream("/autocomp-doc-templates/documentation.html"), Charset.defaultCharset());
			paramTemp = IOUtils.toString(HelpPanel.class.getResourceAsStream("/autocomp-doc-templates/param-doc.html"), Charset.defaultCharset());
			fmMethTemp = IOUtils.toString(HelpPanel.class.getResourceAsStream("/autocomp-doc-templates/fm-method-doc.html"), Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading templates", ex);
		}

		String[] basepackage = { "com.yukthitech" };
		DocInformation docInfo = DocGenerator.buildDocInformation(basepackage);
		
		String finalDoc = null;
		
		for(StepInfo step : docInfo.getSteps())
		{
			addDocForStep(step, documentTemplate, paramTemp);
		}

		for(ValidationInfo validation : docInfo.getValidations())
		{
			addDocForStep(validation, documentTemplate, paramTemp);
		}
		
		for(FreeMarkerMethodDocInfo method : docInfo.getFreeMarkerMethods())
		{
			finalDoc = IIdeConstants.FREE_MARKER_ENGINE.processTemplate("/autocomp-doc-templates/fm-method-doc.html", 
					fmMethTemp, 
					CommonUtils.toMap("node", method)
				);
			
			method.setDocumentation(finalDoc);
		}

		return docInfo;
	}
	
	private void addDocForStep(StepInfo step, String documentTemplate, String paramTemp)
	{
		String finalDoc = IIdeConstants.FREE_MARKER_ENGINE.processTemplate("/autocomp-doc-templates/documentation.html", 
				documentTemplate, 
				CommonUtils.toMap("node", step)
			);
		
		step.setDocumentation(finalDoc);
		
		if(step.getParams() == null)
		{
			return;
		}
		
		for(ParamInfo param : step.getParams())
		{
			String paramDoc = IIdeConstants.FREE_MARKER_ENGINE.processTemplate("/autocomp-doc-templates/param-doc.html", 
					paramTemp, 
					CommonUtils.toMap("param", param)
				);
			
			param.setDocumentation(paramDoc);
		}
	}
	
	public synchronized DocInformation getDocInformation()
	{
		if(docInformation != null)
		{
			return docInformation;
		}
		
		docInformation = ResourceCache.getInstance().getFromCache(
			() -> buildDocInfo(), 
			"autox.docInformation"
		);
		
		return docInformation;
	}
}
