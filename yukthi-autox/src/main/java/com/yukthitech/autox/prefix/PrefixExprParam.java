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
package com.yukthitech.autox.prefix;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to document parser parameters.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PrefixExprParam
{
	/**
	 * Name of the parameter.
	 * @return name
	 */
	public String name();
	
	/**
	 * Type of parameter.
	 * @return type
	 */
	public String type();
	
	/**
	 * Default value of the parameter.
	 * @return default value
	 */
	public String defaultValue() default "";
	
	/**
	 * Description of the parameter.
	 * @return description.
	 */
	public String description();
	
	/**
	 * Flag indicating if this is mandatory param.
	 * @return
	 */
	public boolean required() default false;
}
