package com.yukthitech.autox.ide.services;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * A static class which is capable of providing all available
 * spring services in static way.
 * 
 * @author akranthikiran
 */
@Service
public class SpringServiceProvider
{
	/**
	 * Static instance.
	 */
	private static SpringServiceProvider instance;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@PostConstruct
	private void init()
	{
		SpringServiceProvider.instance = this;
	}
	
	public static <T> T getService(Class<T> serviceType)
	{
		if(instance == null)
		{
			throw new InvalidStateException("Spring-service-provider is used before application initialization");
		}
		
		return instance.applicationContext.getBean(serviceType);
	}
}
