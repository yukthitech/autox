package com.yukthitech.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.yukthitech.autox.event.AutomationEvent;
import com.yukthitech.autox.event.ExecutionInfo;
import com.yukthitech.autox.event.IAutomationListener;

public class TestAutomationListener implements IAutomationListener
{
	private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat();
	
	private Date startTime;
	
	private Date endTime;
	
	private List<String> messages = new LinkedList<>();
	
	private Map<String, Set<String>> testSuites = new TreeMap<>();
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void handleEvent(AutomationEvent e)
	{
		switch(e.getType())
		{
			case AUTOMATION_STARTED:
				startTime = new Date();
				break;
			case AUTOMATION_ENDED:
				endTime = new Date();
				break;
			case AUTOMATION_INITIALIZED:
				extractExecutionTreee(e);
				break;
			case EXECUTION_STARTED:
				ExecutionInfo parent = e.getExecutionInfo().getParentExecution();
				messages.add(String.format("[%s] STARTED - %s for executor: %s [Parent: %s]", 
						TIME_FORMAT.format(new Date()), e.getExecutionType(), e.getExecutionInfo().getRepresentation(), 
						parent != null ? parent.getRepresentation() : "null" 
						));
				break;
			case EXECUTION_ENDED:
				messages.add(String.format("[%s] ENDED - %s for executor: %s", TIME_FORMAT.format(new Date()), e.getExecutionType(), e.getExecutionInfo().getRepresentation()));
				break;
		}
	}
	
	private void extractExecutionTreee(AutomationEvent e)
	{
		ExecutionInfo root = e.getExecutionInfo();
		List<ExecutionInfo> testSuiteLst = root.getChildExecutions();
		
		for(ExecutionInfo tsInfo : testSuiteLst)
		{
			List<ExecutionInfo> testCaseLst = tsInfo.getChildExecutions();
			Set<String> tcNames = new TreeSet<>();
			
			for(ExecutionInfo tcInfo : testCaseLst)
			{
				tcNames.add(tcInfo.getRepresentation());
			}
			
			testSuites.put(tsInfo.getRepresentation(), tcNames);
		}
	}

	public Date getStartTime()
	{
		return startTime;
	}
	
	public Date getEndTime()
	{
		return endTime;
	}
	
	public List<String> getMessages()
	{
		return messages;
	}
	
	public void print()
	{
		System.out.println("\n\n\n=========================================================\n");
		System.out.println(String.format("From: %s, To: %s", TIME_FORMAT.format(startTime), TIME_FORMAT.format(endTime)));
		System.out.println("---------------------------------------");
		
		testSuites.entrySet().stream().forEach(entry -> System.out.println(entry.getKey() + " => " + entry.getValue()));
		System.out.println("---------------------------------------");
		
		messages.stream().forEach(mssg -> System.out.println(mssg));
		System.out.println("\n=========================================================\n\n\n");
	}
}
