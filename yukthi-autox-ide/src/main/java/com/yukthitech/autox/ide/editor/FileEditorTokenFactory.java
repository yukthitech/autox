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
package com.yukthitech.autox.ide.editor;

import java.util.Set;

import javax.swing.Action;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.OccurrenceMarker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.modes.AbstractMarkupTokenMaker;

import com.yukthitech.autox.ide.IIdeFileManager;

public class FileEditorTokenFactory extends TokenMakerFactory
{
	private class TokenMakerWrapper extends AbstractMarkupTokenMaker
	{
		private TokenMaker actualTokenMaker;
		
		public TokenMakerWrapper(TokenMaker actualTokenMaker)
		{
			this.actualTokenMaker = actualTokenMaker;
		}

		@Override
		public boolean getCompleteCloseTags()
		{
			return ((AbstractMarkupTokenMaker) actualTokenMaker).getCompleteCloseTags();
		}

		@Override
		public void yybegin(int newState)
		{
			((AbstractMarkupTokenMaker) actualTokenMaker).yybegin(newState);
		}

		public void addNullToken()
		{
			actualTokenMaker.addNullToken();
		}

		public void addToken(char[] array, int start, int end, int tokenType, int startOffset)
		{
			actualTokenMaker.addToken(array, start, end, tokenType, startOffset);
		}

		public int getClosestStandardTokenTypeForInternalType(int type)
		{
			return actualTokenMaker.getClosestStandardTokenTypeForInternalType(type);
		}

		public boolean getCurlyBracesDenoteCodeBlocks(int languageIndex)
		{
			return actualTokenMaker.getCurlyBracesDenoteCodeBlocks(languageIndex);
		}

		public int getLastTokenTypeOnLine(Segment text, int initialTokenType)
		{
			return actualTokenMaker.getLastTokenTypeOnLine(text, initialTokenType);
		}

		public String[] getLineCommentStartAndEnd(int languageIndex)
		{
			return actualTokenMaker.getLineCommentStartAndEnd(languageIndex);
		}

		public Action getInsertBreakAction()
		{
			return actualTokenMaker.getInsertBreakAction();
		}

		public boolean getMarkOccurrencesOfTokenType(int type)
		{
			return actualTokenMaker.getMarkOccurrencesOfTokenType(type);
		}

		public OccurrenceMarker getOccurrenceMarker()
		{
			return actualTokenMaker.getOccurrenceMarker();
		}

		public boolean getShouldIndentNextLineAfter(Token token)
		{
			return actualTokenMaker.getShouldIndentNextLineAfter(token);
		}

		public Token getTokenList(Segment text, int initialTokenType, int startOffset)
		{
			Token res = actualTokenMaker.getTokenList(text, initialTokenType, startOffset);
			res = fileManager.subtokenize(res);
			
			return res;
		}

		public boolean isIdentifierChar(int languageIndex, char ch)
		{
			return actualTokenMaker.isIdentifierChar(languageIndex, ch);
		}
	}
	
	
	private TokenMakerFactory actualFactory = TokenMakerFactory.getDefaultInstance();
	
	private IIdeFileManager fileManager;
	
	public FileEditorTokenFactory(IIdeFileManager fileManager)
	{
		this.fileManager = fileManager;
	}

	@Override
	protected TokenMaker getTokenMakerImpl(String key)
	{
		return new TokenMakerWrapper(actualFactory.getTokenMaker(key));
	}

	@Override
	public Set<String> keySet()
	{
		return actualFactory.keySet();
	}

}
