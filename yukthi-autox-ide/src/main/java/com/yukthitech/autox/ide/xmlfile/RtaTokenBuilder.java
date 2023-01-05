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
package com.yukthitech.autox.ide.xmlfile;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.rsyntaxtextarea.TokenImpl;

public class RtaTokenBuilder
{
	private char text[];
	
	private int offset;
	private int endOffset;
	
	private int docOffset;
	
	private TokenImpl source;
	
	private int lastHeadOffset;
	
	private List<TokenImpl> resTokens = new ArrayList<>();
	
	public RtaTokenBuilder(TokenImpl source)
	{
		this.source = source;
		this.text = source.text;
		
		int len = source.getEndOffset() - source.getOffset() + 1;
		
		this.docOffset = source.getOffset();
		
		this.offset = this.lastHeadOffset = source.getTextOffset();
		this.endOffset = this.offset + len - 1;
	}
	
	public void addSubtoken(int startOffset, int endOffset)
	{
		int gap = 0;
		
		//add remaining header
		if(startOffset > lastHeadOffset)
		{
			gap = lastHeadOffset - this.offset;
			resTokens.add(new TokenImpl(text, this.lastHeadOffset, startOffset - 1, docOffset + gap, source.getType(), source.getLanguageIndex()));
		}
		
		//add sub token
		gap = startOffset - this.offset;
		
		TokenImpl subtoken = new TokenImpl(text, startOffset, endOffset - 1, docOffset + gap, source.getType(), source.getLanguageIndex());
		resTokens.add(subtoken);
		
		//update the last offsets
		lastHeadOffset = endOffset;
	}
	
	public void appendTailToken()
	{
		if(endOffset >= lastHeadOffset)
		{
			int gap = lastHeadOffset - this.offset;
			
			TokenImpl tailToken = new TokenImpl(text, this.lastHeadOffset, endOffset - 1, docOffset + gap, source.getType(), source.getLanguageIndex());
			resTokens.add(tailToken);
			
			tailToken.setNextToken(source.getNextToken());
		}
	}
	
	public boolean hasSubtokens()
	{
		return !resTokens.isEmpty();
	}
	
	public TokenImpl toToken()
	{
		TokenImpl head = null;
		TokenImpl cur = null;
		
		for(TokenImpl token : this.resTokens)
		{
			if(head == null)
			{
				head = cur = token;
				continue;
			}
			
			cur.setNextToken(token);
			cur = token;
		}
		
		return head;
	}
}
