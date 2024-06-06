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
package com.yukthitech.autox.exec.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yukthitech.autox.test.MetaInfo;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents an execution status report.
 * @author akranthikiran
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionStatusReport implements Cloneable
{
	/**
	 * Name of execution.
	 */
	private String name;
	
	/**
	 * Author of the execution.
	 */
	private String author;
	
	/**
	 * Extra meta info of executable.
	 */
	private MetaInfo metaInfo;

	/**
	 * List of child reports.
	 */
	private List<ExecutionStatusReport> childReports;
	
	/**
	 * Setup execution details.
	 */
	private ExecutionDetails setupExecutionDetails;

	/**
	 * Main execution details.
	 */
	private ExecutionDetails mainExecutionDetails;

	/**
	 * Cleanup execution details.
	 */
	private ExecutionDetails cleanupExecutionDetails;
	
	/**
	 * Type of this report entry. Used in final report
	 * for certain distinctions.
	 */
	private ExecutionStatusReportType type = ExecutionStatusReportType.STANDARD;
	
	private Map<String, String> monitorLogs;
	
	public ExecutionStatusReport()
	{}
	
	public ExecutionStatusReport(String name, ExecutionDetails setupExecutionDetails, ExecutionDetails cleanupExecutionDetails, ExecutionStatusReportType type)
	{
		this.name = name;
		
		this.mainExecutionDetails = new ExecutionDetails();
		this.mainExecutionDetails.setEndDetails(
				TestStatus.getEffectiveStatus(
						setupExecutionDetails != null ? setupExecutionDetails.getStatus() : null, 
						cleanupExecutionDetails != null ? cleanupExecutionDetails.getStatus() : null), 
				"N/A");
		
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public List<ExecutionStatusReport> getChildReports()
	{
		return childReports;
	}

	public void setChildReports(List<ExecutionStatusReport> childReports)
	{
		this.childReports = childReports;
	}

	public ExecutionDetails getSetupExecutionDetails()
	{
		return setupExecutionDetails;
	}

	public void setSetupExecutionDetails(ExecutionDetails setupExecutionDetails)
	{
		this.setupExecutionDetails = setupExecutionDetails;
	}

	public ExecutionDetails getMainExecutionDetails()
	{
		return mainExecutionDetails;
	}

	public void setMainExecutionDetails(ExecutionDetails mainExecutionDetails)
	{
		this.mainExecutionDetails = mainExecutionDetails;
	}
	
	public ExecutionStatusReportType getType()
	{
		return type;
	}

	public void setType(ExecutionStatusReportType type)
	{
		this.type = type;
	}

	public ExecutionDetails getCleanupExecutionDetails()
	{
		return cleanupExecutionDetails;
	}

	public void setCleanupExecutionDetails(ExecutionDetails cleanupExecutionDetails)
	{
		this.cleanupExecutionDetails = cleanupExecutionDetails;
	}
	
	public MetaInfo getMetaInfo()
	{
		return metaInfo;
	}

	public void setMetaInfo(MetaInfo metaInfo)
	{
		this.metaInfo = metaInfo;
	}

	public void addChidReport(ExecutionStatusReport report)
	{
		if(childReports == null)
		{
			childReports = new ArrayList<>();
		}
		
		childReports.add(report);
	}
	
	/**
	 * Fetches number of child reports having specified status.
	 * @param status Status to be checked.
	 * @return Number of child reports with specified status.
	 */
	private int getStatusCount(TestStatus status)
	{
		if(CollectionUtils.isEmpty(childReports))
		{
			return 0;
		}
		
		int count = 0;
		
		for(ExecutionStatusReport result : this.childReports)
		{
			if(result.getMainExecutionDetails() != null && result.getMainExecutionDetails().getStatus() == status)
			{
				count ++;
			}
		}
		
		return count;
	}

	/**
	 * Gets the child reports success count in this suite.
	 *
	 * @return the child reports success count in this suite
	 */
	public int getSuccessCount()
	{
		return getStatusCount(TestStatus.SUCCESSFUL);
	}

	/**
	 * Gets the child reports failure count in this suite.
	 *
	 * @return the child reports failure count in this suite
	 */
	public int getFailureCount()
	{
		return getStatusCount(TestStatus.FAILED);
	}

	/**
	 * Gets the child reports error count in this suite.
	 *
	 * @return the child reports error count in this suite
	 */
	public int getErrorCount()
	{
		return getStatusCount(TestStatus.ERRORED);
	}
	
	/**
	 * Gets the child reports skip count in this suite.
	 *
	 * @return the child reports skip count in this suite
	 */
	public int getSkipCount()
	{
		return getStatusCount(TestStatus.SKIPPED);
	}
	
	/**
	 * Fetches number of child reports.
	 * @return count
	 */
	public int getTotalCount()
	{
		if(childReports == null)
		{
			return 0;
		}
		
		return childReports.size();
	}

	public void addMonitorLog(String name, String fileName)
	{
		if(this.monitorLogs == null)
		{
			this.monitorLogs = new LinkedHashMap<>();
		}
		
		this.monitorLogs.put(name, fileName);
	}

	public Map<String, String> getMonitorLogs()
	{
		return monitorLogs;
	}

	public void setMonitorLogs(Map<String, String> monitorLogs)
	{
		this.monitorLogs = monitorLogs;
	}
	
	public ExecutionStatusReport clone()
	{
		try
		{
			return (ExecutionStatusReport) super.clone();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred during report cloning", ex);
		}
	}
}
