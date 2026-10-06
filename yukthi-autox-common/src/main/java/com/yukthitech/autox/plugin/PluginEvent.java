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
package com.yukthitech.autox.plugin;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.yukthitech.autox.Param;

/**
 * Represents event available by this plugin.
 * @author akranthikiran
 */
@Repeatable(PluginEvents.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface PluginEvent
{
	/**
	 * Name of the event.
	 * @return
	 */
	public String name();
	
	/**
	 * Description of the event and its occurrence.
	 * @return
	 */
	public String description();
	
	/**
	 * Parameters that will be available during this plugin invocation.
	 * @return
	 */
	public Param[] params() default {};
	
	/**
	 * Provides the description on how the return value from handler will be used. 
	 * @return
	 */
	public String returnDescription() default "";
}
