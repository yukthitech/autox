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
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;

/**
 * Represents file search operation.
 * @author akranthikiran
 */
public class FileSearchOperation
{
	private static Logger logger = LogManager.getLogger(FileSearchOperation.class);
	
	/**
	 * Represents a line in a file.
	 * @author akranthikiran
	 */
	private static class FileLine
	{
		private int lineNo;
		
		private String content;

		public FileLine(int lineNo, String content)
		{
			this.lineNo = lineNo;
			this.content = content;
		}
	}
	
	private LinkedList<File> fileStack = new LinkedList<>();
	
	private List<File> searchFiles;
	
	private TreeSet<File> currentFiles = new TreeSet<>();
	
	private int matchedFileCount;
	
	private FileSearchQuery fileSearchQuery;
	
	private List<Pattern> fileNamePatterns = new ArrayList<>();
	
	private boolean replaceOperation;

	public FileSearchOperation(FileSearchQuery fileSearchQuery, List<File> searchFiles, boolean replaceOp)
	{
		this.fileSearchQuery = fileSearchQuery;
		this.searchFiles = new ArrayList<>(searchFiles);
		
		fileStack.addAll(new TreeSet<>(searchFiles));
		this.replaceOperation = replaceOp;
		
		fileSearchQuery.getFileNamePatterns().forEach(str -> 
		{
			this.fileNamePatterns.add(Pattern.compile(buildPattern(str), Pattern.CASE_INSENSITIVE));
		});
	}
	
	public void reset()
	{
		currentFiles.clear();
		fileStack.clear();
		matchedFileCount = 0;
		
		fileStack.addAll(searchFiles);
	}
	
	public boolean isReplaceOperation()
	{
		return replaceOperation;
	}

	private static String buildPattern(String str)
	{
		str = str.replaceAll("[^\\*\\?\\w]", "\\\\$0");
		str = str.replace("*", ".*");
		str = str.replace("?", ".");
		
		return str;
	}
	
	public int getMatchedFileCount()
	{
		return matchedFileCount;
	}
	
	private boolean moveToNextFolder()
	{
		if(fileStack.isEmpty())
		{
			return false;
		}
		
		//clear current files
		currentFiles.clear();
		
		File nextFile = fileStack.removeFirst();
		
		//if next file is not direct, set it as current file for processing
		if(!nextFile.isDirectory())
		{
			currentFiles.add(nextFile);
			return true;
		}
		
		TreeSet<File> newFolders = new TreeSet<>();
		
		nextFile.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				if(file.isDirectory())
				{
					newFolders.add(file);
					return false;
				}
				
				boolean matched = false;
				
				for(Pattern ptrn : fileNamePatterns)
				{
					if(ptrn.matcher(file.getName()).matches())
					{
						matched = true;
						break;
					}
				}
				
				if(matched)
				{
					currentFiles.add(file);
				}
				
				return false;
			}
		});
		
		this.fileStack.addAll(newFolders);
		return true;
	}
	
	private File nextFile()
	{
		//till files are found loop through the folders
		while(currentFiles.isEmpty())
		{
			if(!moveToNextFolder())
			{
				break;
			}
		}
		
		if(currentFiles.isEmpty())
		{
			return null;
		}
		
		File first = currentFiles.first();
		currentFiles.remove(first);
		matchedFileCount++;
		
		return first;
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
	
	private TreeMap<Integer, FileLine> loadLineMapping(String content)
	{
		TreeMap<Integer, FileLine> res = new TreeMap<>();
		
		int idx = 0, prevIdx = 0;
		int lineNo = 1;
		
		while(prevIdx < content.length() && (idx = content.indexOf('\n', prevIdx)) >= 0)
		{
			String lineContent = idx > prevIdx ? content.substring(prevIdx, idx) : "";
			res.put(prevIdx, new FileLine(lineNo, lineContent));
			
			//move post current \n char
			prevIdx = idx + 1;
			//increment line number
			lineNo++;
		}
		
		if(prevIdx < content.length())
		{
			String lineContent = content.substring(prevIdx);
			res.put(prevIdx, new FileLine(lineNo, lineContent));
		}
		
		return res;
	}
	
	private String escapeHtml(String content)
	{
		//replace white space chars with single space
		content = content.replaceAll("\\s+", " ");
		return StringEscapeUtils.escapeHtml4(content);
	}
	
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
					lineMaping = loadLineMapping(content);
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
				
				StringBuilder builder = new StringBuilder("<html><body>");
				
				
				builder.append("<b>").append(file.getName()).append(":").append(startLine.lineNo).append("</b> - ");
				builder.append(escapeHtml(startLine.content.substring(0, matchSt)));
				builder.append("<span style=\"background-color: yellow;\">")
					.append(escapeHtml(startLine.content.substring(matchSt, matchEnd)))
					.append("</span>");
				builder.append((matchEnd < startLine.content.length() ? escapeHtml(startLine.content.substring(matchEnd)) : "") );
				builder.append("</body></html>");
				
				res.add(new SearchResult(file, matchSt, matchEnd, startLine.lineNo, builder.toString()));
			}
		}
		
		return res;
	}

	public boolean replaceAll(FileEditorTabbedPane fileEditorTabbedPane)
	{
		File file = null;
		Pattern searchPattern = buildSearchPattern();
		
		while((file = nextFile()) != null)
		{
			FileEditor openEditor = fileEditorTabbedPane.getFileEditor(file);
			String content = null;
			
			if(openEditor != null && openEditor.isContentChanged())
			{
				content = openEditor.getContent();
			}
			else
			{
				try
				{
					content = FileUtils.readFileToString(file, Charset.defaultCharset());
				}catch(Exception ex)
				{
					logger.error("An error occurred while loading content of file: {}", file.getPath(), ex);
	
					JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
							String.format("An error occurred while loading content of file: %s\nError: %s", file.getPath(), ex.getMessage()));
					return false;
				}
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
				
				if(openEditor != null && openEditor.isContentChanged())
				{
					openEditor.setContent(buffer.toString());
				}
				else
				{
					writeToFile(file, buffer);
				}
			}
		}
		
		return true;
	}
	
	private boolean writeToFile(File file, StringBuffer buffer)
	{
		try
		{
			FileUtils.write(file, buffer, Charset.defaultCharset());
		}catch(Exception ex)
		{
			logger.error("An error occurred while replacing content of file: {}", file.getPath(), ex);

			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
					String.format("An error occurred while replacing content of file: %s\nError: %s", file.getPath(), ex.getMessage()));
			return false;
		}
		
		return true;
	}
}
