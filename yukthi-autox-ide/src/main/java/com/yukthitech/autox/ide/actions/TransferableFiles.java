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
import java.util.List;

/**
 * Trasferable wrapper for fils which is used to copy file paths to clipboard.
 * @author akiran
 */
public class TransferableFiles implements Transferable
{
	private List<File> listOfFiles;

	public TransferableFiles(List<File> listOfFiles)
	{
		this.listOfFiles = listOfFiles;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { DataFlavor.javaFileListFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return DataFlavor.javaFileListFlavor.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		return listOfFiles;
	}

}
