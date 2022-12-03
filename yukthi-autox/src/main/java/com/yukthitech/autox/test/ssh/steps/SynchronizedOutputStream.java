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
package com.yukthitech.autox.test.ssh.steps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Thread safe output stream.
 * @author akiran
 */
public class SynchronizedOutputStream extends OutputStream
{
	/**
	 * Buffered stream.
	 */
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	@Override
	public synchronized void write(int b) throws IOException
	{
		bos.write(b);
	}

	@Override
	public synchronized void write(byte[] b) throws IOException
	{
		bos.write(b);
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException
	{
		bos.write(b, off, len);
	}

	@Override
	public synchronized void flush() throws IOException
	{
		bos.flush();
	}

	/**
	 * Converts to byte array.
	 * @return converted byte array.
	 */
	public byte[] toByteArray()
	{
		return bos.toByteArray();
	}
}
