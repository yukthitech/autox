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
package com.yukthitech.autox.plugin.rest.steps;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.rest.RestRequestWithBody;

/**
 * Base class for the rest based steps with attachments.
 * @author akiran
 */
public abstract class AbstractRestWithAttachmentsStep<T extends RestRequestWithBody<T>> extends AbstractRestStep  implements Validateable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
	 */
	private List<HttpPart> parts = new ArrayList<>();
	
	/**
	 * List of files to be attachment with this request. Values are resources.
	 */
	private List<HttpAttachment> attachments = new ArrayList<>();

	/**
	 * Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.
	 * @param part part to be set.
	 */
	@ChildElement(description = "Parts to be set on the request. If non-string is specified, object will be converted to json and content-type of part will be set as JSON.")
	public void addPart(HttpPart part)
	{
		parts.add(part);
	}
	
	/**
	 * Adds the specified file as attachment.
	 * @param attachment Attachment to add.
	 */
	@ChildElement(description = "List of files to be attached with this request. Values are resources")
	public void addAttachment(HttpAttachment attachment)
	{
		attachments.add(attachment);
	}
	
	@Override
	public void executeRestStep(AutomationContext context, IExecutionLogger exeLogger) throws Exception
	{
		T postRestRequest = newRequest(uri);
		postRestRequest.setMultipartRequest(true);
		
		IResource partResource = null;
		
		for(HttpPart partEntry : parts)
		{
			if(StringUtils.isNotBlank(partEntry.getCondition()))
			{
				boolean condRes = AutomationUtils.evaluateCondition(context, partEntry.getCondition());
			
				if(!condRes)
				{
					context.getExecutionLogger().debug("Excluding part with name '{}' as condition evaluated to false.", partEntry.getName());
					continue;
				}
			}

			exeLogger.debug("Adding part with name: {}", partEntry.getName());
			
			if(partEntry.getValue() instanceof String)
			{
				partResource = ResourceFactory.getResource(context, (String) partEntry.getValue(), exeLogger, true);
				postRestRequest.addTextPart(partEntry.getName(), partResource.toText(), partEntry.getContentType());
			}
			else
			{
				postRestRequest.addJsonPart(partEntry.getName(), partEntry.getValue());
			}
		}
		
		File file = null;
		
		//add attachments
		IResource resource = null;
		List<File> filesToDelete = new ArrayList<>();
		
		for(HttpAttachment attachment : attachments)
		{
			if(StringUtils.isNotBlank(attachment.getCondition()))
			{
				boolean condRes = AutomationUtils.evaluateCondition(context, attachment.getCondition());
			
				if(!condRes)
				{
					context.getExecutionLogger().debug("Excluding attachment with name '{}' as condition evaluated to false.", attachment.getName());
					continue;
				}
			}

			exeLogger.debug("Adding attachment with name: {}", attachment.getName());
			
			resource = ResourceFactory.getResource(context, attachment.getFile(), exeLogger, attachment.isParseAsTemplate());
			
			file = File.createTempFile(attachment.getName(), ".tmp");
			
			FileOutputStream fos = new FileOutputStream(file);
			IOUtils.copy(resource.getInputStream(), fos);
			
			fos.flush();
			fos.close();
			resource.close();
			
			postRestRequest.addAttachment(attachment.getName(), attachment.getFileName(), file, null);
			filesToDelete.add(file);
		}
		
		super.populate(context, postRestRequest, exeLogger);
		super.invoke(context, postRestRequest, exeLogger);
		
		for(File fileToDel : filesToDelete)
		{
			if(!fileToDel.delete())
			{
				exeLogger.debug("Failed to delete temp file: {}", fileToDel.getPath());
			}
		}
	}
	
	public abstract T newRequest(String uri); 
}
