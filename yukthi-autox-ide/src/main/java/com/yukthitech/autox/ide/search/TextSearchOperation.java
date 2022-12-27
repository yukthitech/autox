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
package com.yukthitech.autox.ide.search;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;

/**
 * Represents file search operation using text/regex.
 * @author akranthikiran
 */
public class TextSearchOperation extends AbstractSearchOperation
{
	private static Logger logger = LogManager.getLogger(TextSearchOperation.class);
	
	public TextSearchOperation(FileSearchQuery fileSearchQuery, List<File> searchFiles, boolean replaceOp)
	{
		super(fileSearchQuery, searchFiles, replaceOp);
	}
	
	private Pattern buildSearchPattern()
	{
		String searchStr = fileSearchQuery.getSearchString();
		
		if(!fileSearchQuery.isRegularExpression())
		{
			searchStr = Pattern.quote(searchStr);
		}
		
		int mod = 0;
		mod = fileSearchQuery.isCaseSensitive() ? mod : (mod | Pattern.CASE_INSENSITIVE);
		mod = fileSearchQuery.isRegularExpression() && fileSearchQuery.isSpanMultipleLines() ? (mod | Pattern.DOTALL) : mod;
		
		return Pattern.compile(searchStr, mod);
	}
	
	@Override
	public List<SearchResult> findAll()
	{
		List<SearchResult> res = new LinkedList<>();
		File file = null;
		Pattern searchPattern = buildSearchPattern();
		
		while((file = nextFile()) != null)
		{
			String content = null;
			
			try
			{
				content = FileUtils.readFileToString(file, Charset.defaultCharset());
			}catch(Exception ex)
			{
				logger.error("An error occurred while loading content of file: {}", file.getPath(), ex);
				
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
						String.format("An error occurred while loading content of file: %s\nError: %s", file.getPath(), ex.getMessage()));
				return null;
			}
			
			Matcher matcher = searchPattern.matcher(content);
			TreeMap<Integer, FileLine> lineMaping = null;
			
			while(matcher.find())
			{
				if(lineMaping == null)
				{
					lineMaping = loadLineIndexMapping(content);
				}
				
				Map.Entry<Integer, FileLine> startLineEntry = lineMaping.floorEntry(matcher.start());
				FileLine startLine = startLineEntry.getValue();
				int matchSt = matcher.start() - startLineEntry.getKey();
				
				Map.Entry<Integer, FileLine> endLineEntry = lineMaping.floorEntry(matcher.end());
				int matchEnd = startLine.content.length();
				
				if(startLineEntry.getKey().equals(endLineEntry.getKey()))
				{
					matchEnd = matcher.end() - startLineEntry.getKey();
				}
				
				StringBuilder builder = new StringBuilder("<html><body style=\"white-space: nowrap;\">");
				
				
				builder.append("<b>").append(file.getName()).append(":").append(startLine.lineNo).append("</b> - ");
				builder.append(escapeHtml(startLine.content.substring(0, matchSt)));
				builder.append("<span style=\"background-color: yellow;\">")
					.append(escapeHtml(startLine.content.substring(matchSt, matchEnd)))
					.append("</span>");
				builder.append((matchEnd < startLine.content.length() ? escapeHtml(startLine.content.substring(matchEnd)) : "") );
				builder.append("</body></html>");
				
				res.add(new SearchResult(file, matcher.start(), matcher.end(), startLine.lineNo, builder.toString()));
			}
		}
		
		return res;
	}
	
	@Override
	public void replace(FileEditorTabbedPane fileEditorTabbedPane, List<SearchResult> matches)
	{
		//Group the results by file, in descending order
		Map<File, TreeSet<SearchResult>> fileResults = groupResults(matches);
		
		//replace the results with replacement string
		Pattern searchPattern = buildSearchPattern();
		
		for(Map.Entry<File, TreeSet<SearchResult>> entry : fileResults.entrySet())
		{
			File file = entry.getKey();
			String content = readContent(fileEditorTabbedPane, file);
			
			if(content == null)
			{
				return;
			}
			
			for(SearchResult res : entry.getValue())
			{
				String matchedContent = content.substring(res.getStart(), res.getEnd());
				Matcher matcher = searchPattern.matcher(matchedContent);
				matchedContent = matcher.replaceFirst(fileSearchQuery.getReplaceWith());
				
				content = content.substring(0, res.getStart()) + matchedContent + content.substring(res.getEnd());
			}
			
			if(!writeContent(fileEditorTabbedPane, file, content))
			{
				return;
			}
		}
	}

	
	@Override
	public void replaceAll(FileEditorTabbedPane fileEditorTabbedPane)
	{
		File file = null;
		Pattern searchPattern = buildSearchPattern();
		
		while((file = nextFile()) != null)
		{
			String content = readContent(fileEditorTabbedPane, file);
			
			if(content == null)
			{
				return;
			}
			
			Matcher matcher = searchPattern.matcher(content);
			StringBuffer buffer = new StringBuffer();
			boolean matchFound = false;
			
			while(matcher.find())
			{
				matcher.appendReplacement(buffer, fileSearchQuery.getReplaceWith());
				matchFound = true;
			}
			
			if(matchFound)
			{
				matcher.appendTail(buffer);
	
				if(!writeContent(fileEditorTabbedPane, file, buffer.toString()))
				{
					return;
				}
			}
		}
	}
}
