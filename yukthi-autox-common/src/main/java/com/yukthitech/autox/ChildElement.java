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

/**
 * Used to define possible child elements of the tag. If specified on setter it will be 
 * treated as single valued element and on adder multi valued element.
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChildElement
{
	/**
	 * Description about the parameter.
	 * @return Description about the parameter.
	 */
	public String description();
	
	/**
	 * For id based adder (mandatory), this should specify name of the key attribute.
	 * @return key name
	 */
	public String key() default "";
	
	/**
	 * For id based adder (mandatory), description about the key.
	 * @return key desc
	 */
	public String keyDescription() default "";

	/**
	 * Indicates whether this is mandatory parameter or not.
	 * @return mandatory or not
	 */
	public boolean required() default false;
}
