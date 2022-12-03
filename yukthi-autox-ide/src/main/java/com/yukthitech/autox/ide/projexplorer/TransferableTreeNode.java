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
package com.yukthitech.autox.ide.projexplorer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.TreePath;

public class TransferableTreeNode implements Transferable
{

	public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,"Tree Path");
	
	DataFlavor flavors[] = {TREE_PATH_FLAVOR };
	
	TreePath path;
	public TransferableTreeNode(TreePath path)
	{
		this.path=path;
		
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		// TODO Auto-generated method stub
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		// TODO Auto-generated method stub
		return (flavor.getRepresentationClass()==TreePath.class);
	}

	@Override
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if(isDataFlavorSupported(flavor)){
			return (Object)path;
		}
		else{
		// TODO Auto-generated method stub
			throw new UnsupportedFlavorException(flavor);
		}
	}

}
