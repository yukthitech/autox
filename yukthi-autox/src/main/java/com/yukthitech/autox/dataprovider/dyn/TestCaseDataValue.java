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
package com.yukthitech.autox.dataprovider.dyn;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Ordered value instructions for a dynamic test-case-data entry.
 */
public class TestCaseDataValue implements Validateable
{
	private List<IDataValueInstruction> instructions = new ArrayList<>();
	
	public void addProperty(DataPropertyInstruction property)
	{
		this.instructions.add(property);
	}
	
	public void addClone(DataCloneInstruction clone)
	{
		this.instructions.add(clone);
	}
	
	public List<IDataValueInstruction> getInstructions()
	{
		return instructions;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(CollectionUtils.isEmpty(instructions))
		{
			throw new ValidateException("No instructions specified in test-case-data value.");
		}
	}
}
