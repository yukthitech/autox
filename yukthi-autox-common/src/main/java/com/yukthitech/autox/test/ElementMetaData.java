package com.yukthitech.autox.test;

/**
 * Helps in holding meta info of the enclosing element. This info is not used in
 * execution.
 */
public class ElementMetaData
{
	/**
	 * Name of the meta info.
	 */
	private String name;

	/**
	 * Value of the meta info.
	 */
	private String value;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}
