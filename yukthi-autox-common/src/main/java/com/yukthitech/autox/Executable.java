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
package com.yukthitech.autox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.autox.plugin.IPlugin;

/**
 * Used to mark a step/validation as executable, so that they can be found dynamically.
 * @author akiran
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Executable
{
	/**
	 * Provides name of executable.
	 * @return Executable name.
	 */
	public String name();
	
	/**
	 * Message representing the target.
	 * @return Message
	 */
	public String message();
	
	/**
	 * Plugin types required by current executable (step or validator)
	 * @return required plugin type.
	 */
	public Class<? extends IPlugin<?, ?>>[] requiredPluginTypes() default {};
	
	/**
	 * Group to which current executable belongs to. 
	 * @return
	 */
	public Group group();
	
	/**
	 * Used to indicate current step must be followed by specified step type. And will be executed
	 * in conjunction with preceding step.
	 * @return
	 */
	public Class<? extends IMultiPartStep>[] partOf() default {};
}
