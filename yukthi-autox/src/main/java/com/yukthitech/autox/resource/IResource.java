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
package com.yukthitech.autox.resource;

import java.io.InputStream;

/**
 * Represents resource that can be load.
 * @author akiran
 */
public interface IResource
{
	/**
	 * Fetches the name of resource if any.
	 * @return name of the resource.
	 */
	public String getName();
	
	/**
	 * opens the resource.
	 * @return Input stream to read resource.
	 */
	public InputStream getInputStream();
	
	/**
	 * Fetches string from underlying stream and returns the same. This will close the underlying stream.
	 * @return content represented by this stream.
	 */
	public String toText();
	
	/**
	 * Fetches flag indicating if this resource is raw type.
	 * @return
	 */
	public boolean isRawType();
	
	/**
	 * Closes the stream.
	 */
	public void close();
}
