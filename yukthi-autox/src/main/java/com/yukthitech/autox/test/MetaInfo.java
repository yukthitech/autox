package com.yukthitech.autox.test;

import java.util.Map;
import java.util.Set;

public class MetaInfo
{
	private String filePath;
	
	private int lineNumber;
	
	private String uqId;
	
	private Set<String> groups;
	
	private Map<String, String> tags;

	public String getFilePath()
	{
		return filePath;
	}

	public MetaInfo setFilePath(String filePath)
	{
		this.filePath = filePath;
		return this;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}

	public MetaInfo setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
		return this;
	}

	public Set<String> getGroups()
	{
		return groups;
	}

	public MetaInfo setGroups(Set<String> groups)
	{
		this.groups = groups;
		return this;
	}

	public Map<String, String> getTags()
	{
		return tags;
	}

	public MetaInfo setTags(Map<String, String> tags)
	{
		this.tags = tags;
		return this;
	}

	public String getUqId()
	{
		return uqId;
	}

	public MetaInfo setUqId(String uqId)
	{
		this.uqId = uqId;
		return this;
	}
}
