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
package com.yukthitech.prism.search;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.prism.IIdeConstants;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.editor.FileEditor;
import com.yukthitech.prism.editor.FileEditorTabbedPane;

/**
 * Base class for search operations.
 * @author akranthikiran
 */
public abstract class AbstractSearchOperation implements ISearchOperation
{
	private static Logger logger = LogManager.getLogger(AbstractSearchOperation.class);
	
	/**
	 * Represents a line in a file.
	 * @author akranthikiran
	 */
	protected static class FileLine
	{
		int index;
		
		int lineNo;
		
		String content;

		public FileLine(int lineNo, int index, String content)
		{
			this.lineNo = lineNo;
			this.index = index;
			this.content = content;
		}
	}
	
	private LinkedList<File> fileStack = new LinkedList<>();
	
	private List<File> searchFiles;
	
	private List<File> ignoredFolders;
	
	private TreeSet<File> currentFiles = new TreeSet<>();
	
	private int matchedFileCount;
	
	protected FileSearchQuery fileSearchQuery;
	
	private List<Pattern> fileNamePatterns = new ArrayList<>();
	
	private boolean replaceOperation;

	public AbstractSearchOperation(FileSearchQuery fileSearchQuery, List<File> searchFiles, List<File> ignoredFolders, boolean replaceOp)
	{
		this.fileSearchQuery = fileSearchQuery;
		this.searchFiles = new ArrayList<>(searchFiles);
		this.ignoredFolders = new ArrayList<>(ignoredFolders);
		
		fileStack.addAll(new TreeSet<>(searchFiles));
		this.replaceOperation = replaceOp;
		
		fileSearchQuery.getFileNamePatterns().forEach(str -> 
		{
			this.fileNamePatterns.add(Pattern.compile(buildNamePattern(str), Pattern.CASE_INSENSITIVE));
		});
	}
	
	@Override
	public void reset()
	{
		currentFiles.clear();
		fileStack.clear();
		matchedFileCount = 0;
		
		fileStack.addAll(searchFiles);
	}
	
	@Override
	public boolean isReplaceOperation()
	{
		return replaceOperation;
	}
	
	private static String buildNamePattern(String str)
	{
		str = str.replaceAll("[^\\*\\?\\w]", "\\\\$0");
		str = str.replace("*", ".*");
		str = str.replace("?", ".");
		
		return str;
	}
	
	@Override
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
					if(!ignoredFolders.contains(file))
					{
						newFolders.add(file);
					}
					
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
	
	protected File nextFile()
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
	
	protected TreeMap<Integer, FileLine> loadLineIndexMapping(String content)
	{
		TreeMap<Integer, FileLine> res = new TreeMap<>();
		
		int idx = 0, prevIdx = 0;
		int lineNo = 1;
		
		while(prevIdx < content.length() && (idx = content.indexOf('\n', prevIdx)) >= 0)
		{
			String lineContent = idx > prevIdx ? content.substring(prevIdx, idx) : "";
			res.put(prevIdx, new FileLine(lineNo, prevIdx, lineContent));
			
			//move post current \n char
			prevIdx = idx + 1;
			//increment line number
			lineNo++;
		}
		
		if(prevIdx < content.length())
		{
			String lineContent = content.substring(prevIdx);
			res.put(prevIdx, new FileLine(lineNo, prevIdx, lineContent));
		}
		
		return res;
	}
	
	protected TreeMap<Integer, FileLine> loadLineNumberMapping(String content)
	{
		TreeMap<Integer, FileLine> res = new TreeMap<>();
		
		int idx = 0, prevIdx = 0;
		int lineNo = 1;
		
		while(prevIdx < content.length() && (idx = content.indexOf('\n', prevIdx)) >= 0)
		{
			String lineContent = idx > prevIdx ? content.substring(prevIdx, idx) : "";
			res.put(lineNo, new FileLine(lineNo, prevIdx, lineContent));
			
			//move post current \n char
			prevIdx = idx + 1;
			//increment line number
			lineNo++;
		}
		
		if(prevIdx < content.length())
		{
			String lineContent = content.substring(prevIdx);
			res.put(lineNo, new FileLine(lineNo, prevIdx, lineContent));
		}
		
		return res;
	}

	protected String escapeHtml(String content)
	{
		//replace white space chars with single space
		content = content.replaceAll("\\s+", " ");
		return StringEscapeUtils.escapeHtml4(content);
	}
	
	protected Map<File, TreeSet<SearchResult>> groupResults(List<SearchResult> matches)
	{
		Map<File, TreeSet<SearchResult>> fileResults = new HashMap<>();
		Comparator<SearchResult> resComparator = new Comparator<SearchResult>()
		{
			@Override
			public int compare(SearchResult o1, SearchResult o2)
			{
				return o2.getStart() - o1.getStart();
			}
		};
		
		for(SearchResult res : matches)
		{
			TreeSet<SearchResult> resSet = fileResults.get(res.getFile());
			
			if(resSet == null)
			{
				resSet = new TreeSet<SearchResult>(resComparator);
				fileResults.put(res.getFile(), resSet);
			}
			
			resSet.add(res);
		}
		
		return fileResults;
	}

	protected String readContent(FileEditorTabbedPane fileEditorTabbedPane, File file)
	{
		FileEditor openEditor = fileEditorTabbedPane.getFileEditor(file);
		String content = null;
		
		if(openEditor != null)
		{
			if(openEditor.isContentChanged())
			{
				int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), 
						String.format("File '%s' is open and modified. Would you like to save file before proceeding?", file.getName()),
						"Search & Replace", JOptionPane.OK_CANCEL_OPTION);
				
				if(res != JOptionPane.OK_OPTION)
				{
					return null;
				}
			}
			
			openEditor.saveFile();
		}
		
		try
		{
			content = FileUtils.readFileToString(file, Charset.defaultCharset());
		}catch(Exception ex)
		{
			logger.error("An error occurred while loading content of file: {}", file.getPath(), ex);

			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
					String.format("An error occurred while loading content of file: %s\nError: %s", file.getPath(), IIdeConstants.wrap(ex.getMessage())));
			return null;
		}
		
		return content;
	}
	
	protected boolean writeContent(FileEditorTabbedPane fileEditorTabbedPane, File file, String content)
	{
		FileEditor openEditor = fileEditorTabbedPane.getFileEditor(file);

		try
		{
			FileUtils.write(file, content, Charset.defaultCharset());
		}catch(Exception ex)
		{
			logger.error("An error occurred while replacing content of file: {}", file.getPath(), ex);

			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
					String.format("An error occurred while replacing content of file: %s\nError: %s", file.getPath(), IIdeConstants.wrap(ex.getMessage())));
			return false;
		}
		
		if(openEditor != null)
		{
			openEditor.setContent(content);
			openEditor.markAsSaved();
		}
		
		return true;
	}
}
