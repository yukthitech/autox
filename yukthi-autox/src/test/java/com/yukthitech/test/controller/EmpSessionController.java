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
package com.yukthitech.test.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yukthitech.utils.CommonUtils;

/**
 * Same like emp controller but with session support.
 * @author akranthikiran
 */
@RestController
@RequestMapping("/empSession")
public class EmpSessionController
{
	private static final String HEADER_AUTH_TOKEN = "Auth-Token";
	
	private static class Session
	{
		private String id = UUID.randomUUID().toString();
		private Date date = new Date();
		
		public boolean isValid()
		{
			long diff = System.currentTimeMillis() - date.getTime();
			
			if(diff > 5000)
			{
				return false;
			}
			
			return true;
		}
	}
	
	private Map<Integer, Employee> empMap = new HashMap<>();
	
	private Map<String, Session> sessions = new HashMap<>();
	
	public EmpSessionController()
	{
		System.out.println("=======> Initializing emp session controller..");
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public synchronized Map<String, Object> login(@RequestBody LoginRequest req)
	{
		Session session = new Session();
		sessions.put(session.id, session);
		
		return CommonUtils.toMap("token", session.id);
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public synchronized Employee getEmplyoee(@PathVariable("id") int id, @RequestHeader(HEADER_AUTH_TOKEN) String token, HttpServletResponse response)
	{
		if(token == null || !sessions.containsKey(token) || !sessions.get(token).isValid())
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		
		if(!empMap.containsKey(id))
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		
		return empMap.get(id);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public synchronized SaveResult saveEmployee(@RequestBody Employee emp, @RequestHeader(HEADER_AUTH_TOKEN) String token, HttpServletResponse response)
	{
		if(token == null || !sessions.containsKey(token) || !sessions.get(token).isValid())
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		
		emp.setId(empMap.size() + 1);
		empMap.put(emp.getId(), emp);
		
		return new SaveResult("" + emp.getId());
	}
}
