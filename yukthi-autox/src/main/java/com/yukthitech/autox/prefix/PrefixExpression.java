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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method as expression parser method.
 * @author akiran
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrefixExpression
{
	/**
	 * Provides expression type.
	 * @return expression type.
	 */
	public String type();
	
	/**
	 * Description of the parser.
	 * @return description
	 */
	public String description();

	/**
	 * Example.
	 * @return example
	 */
	public String example();
	
	/**
	 * Type of content expected.
	 * @return content type
	 */
	public PrefixExpressionContentType contentType() default PrefixExpressionContentType.NONE;
	
	/**
	 * Parameters supported by this parser.
	 * @return parameters
	 */
	public PrefixExprParam[] params() default {};
}
