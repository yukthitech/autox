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
package com.yukthitech.autox.plugin.mail;

/**
 * Enumeration of supported mail read protocol.
 * @author akiran
 */
public enum MailReadProtocol
{
	/**
	 * IMAPS read protocol.
	 */
	IMAPS("imaps"),
	
	IMAP("imap"),
	
	/**
	 * POP3 read protocol.
	 */
	POP3("pop3"),
	
	/**
	 * Pop3 security protocol.
	 */
	POP3S("pop3s");
	
	/**
	 * String name of the protocol.
	 */
	private String name;

	/**
	 * Instantiates a new mail read protocol.
	 *
	 * @param name the name
	 */
	private MailReadProtocol(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the string name of the protocol.
	 *
	 * @return the string name of the protocol
	 */
	public String getName()
	{
		return name;
	}
}
