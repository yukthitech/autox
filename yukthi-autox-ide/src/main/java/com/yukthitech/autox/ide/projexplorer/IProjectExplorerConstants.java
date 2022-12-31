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

public interface IProjectExplorerConstants
{
	String ID_PREFIX_PROJECT = ":a-project::";
	
	String ID_PREFIX_TEST_SUITE_FOLDER = ":a-testSuite::";

	String ID_PREFIX_APP_CONFIG = ":b-appConfig::";

	String ID_PREFIX_APP_PROP = ":c-appProp::";

	String ID_PREFIX_FOLDER = ":i-folder::";
	
	public static String extractIdPrefix(String id)
	{
		int idx = id.indexOf("::");
		
		if(idx <= 0)
		{
			return null;
		}
		
		return id.substring(0, idx + 2);
	}
	
	public static boolean isSpecialNode(String id)
	{
		if(id.startsWith(ID_PREFIX_PROJECT) 
				|| id.startsWith(ID_PREFIX_TEST_SUITE_FOLDER)
				|| id.startsWith(ID_PREFIX_APP_CONFIG)
				|| id.startsWith(ID_PREFIX_APP_PROP)
			)
		{
			return true;
		}
		
		return false;
	}

	public static boolean isDragableNode(String id)
	{
		return !isSpecialNode(id);
	}
}
