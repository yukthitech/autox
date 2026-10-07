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
package com.yukthitech.autox.plugin.ui.common;

import java.io.Serializable;

import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;

/**
 * Plain serializable representation of a browser cookie. Used to persist/load cookies (instead of
 * persisting playwright cookie objects directly).
 * 
 * @author akiran
 */
public class CookieData implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String name;

	private String value;

	private String domain;

	private String path;

	/**
	 * Unix time in seconds. Value &lt; 0 represents session cookie.
	 */
	private double expires = -1;

	private boolean httpOnly;

	private boolean secure;

	/**
	 * Same-site attribute name (Strict, Lax, None). Can be null.
	 */
	private String sameSite;

	public CookieData()
	{}

	/**
	 * Creates cookie data from playwright cookie.
	 * @param cookie playwright cookie
	 * @return converted cookie data
	 */
	public static CookieData from(Cookie cookie)
	{
		CookieData data = new CookieData();

		data.name = cookie.name;
		data.value = cookie.value;
		data.domain = cookie.domain;
		data.path = cookie.path;
		data.expires = (cookie.expires != null) ? cookie.expires : -1;
		data.httpOnly = (cookie.httpOnly != null) && cookie.httpOnly;
		data.secure = (cookie.secure != null) && cookie.secure;
		data.sameSite = (cookie.sameSite != null) ? cookie.sameSite.name() : null;

		return data;
	}

	/**
	 * Converts current data into playwright cookie.
	 * @return playwright cookie
	 */
	public Cookie toCookie()
	{
		Cookie cookie = new Cookie(name, value);

		cookie.setDomain(domain);
		cookie.setPath(path == null ? "/" : path);
		cookie.setHttpOnly(httpOnly);
		cookie.setSecure(secure);

		if(expires > 0)
		{
			cookie.setExpires(expires);
		}

		if(sameSite != null)
		{
			try
			{
				cookie.setSameSite(SameSiteAttribute.valueOf(sameSite));
			}catch(IllegalArgumentException ex)
			{
				// ignore invalid same-site values
			}
		}

		return cookie;
	}

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

	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public double getExpires()
	{
		return expires;
	}

	public void setExpires(double expires)
	{
		this.expires = expires;
	}

	public boolean isHttpOnly()
	{
		return httpOnly;
	}

	public void setHttpOnly(boolean httpOnly)
	{
		this.httpOnly = httpOnly;
	}

	public boolean isSecure()
	{
		return secure;
	}

	public void setSecure(boolean secure)
	{
		this.secure = secure;
	}

	public String getSameSite()
	{
		return sameSite;
	}

	public void setSameSite(String sameSite)
	{
		this.sameSite = sameSite;
	}

	@Override
	public String toString()
	{
		return String.format("Cookie[name: %s, domain: %s, path: %s, expires: %s, httpOnly: %s, secure: %s]", 
				name, domain, path, expires, httpOnly, secure);
	}
}
