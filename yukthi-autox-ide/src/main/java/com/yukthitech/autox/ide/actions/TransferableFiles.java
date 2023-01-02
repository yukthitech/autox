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
package com.yukthitech.autox.ide.actions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Trasferable wrapper for fils which is used to copy file paths to clipboard.
 * @author akiran
 */
public class TransferableFiles implements Transferable
{
	public static DataFlavor SERIALIZED_DATA;
	
	static
	{
		try
		{
			SERIALIZED_DATA = new DataFlavor(DataFlavor.javaSerializedObjectMimeType)
			{
				@Override
				public String getParameter(String paramName)
				{
					if("class".equals(paramName))
					{
						return FileData.class.getName();
					}
					
					return super.getParameter(paramName);
				}
			};
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to create data flavour", ex);
		}
	}
	
	public static class FileData implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private List<File> listOfFiles;
		private boolean moveOperation;
		
		public FileData(List<File> listOfFiles, boolean moveOperation)
		{
			if(CollectionUtils.isEmpty(listOfFiles))
			{
				throw new NullPointerException("File list cannot be empty.");
			}
			
			this.listOfFiles = listOfFiles;
			this.moveOperation = moveOperation;
		}

		public List<File> getListOfFiles()
		{
			return listOfFiles;
		}
		
		public boolean isMoveOperation()
		{
			return moveOperation;
		}
	}

	private FileData fileData;

	public TransferableFiles(List<File> listOfFiles, boolean moveOperation)
	{
		this.fileData = new FileData(listOfFiles, moveOperation);
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { DataFlavor.javaFileListFlavor, SERIALIZED_DATA };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return DataFlavor.javaFileListFlavor.equals(flavor) || SERIALIZED_DATA.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if(DataFlavor.javaFileListFlavor.equals(flavor))
		{
			return fileData.listOfFiles;
		}
		
		return fileData;
	}
}
