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
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.proj.ProjectManager;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;
import com.yukthitech.autox.ide.search.FileSearchQuery.QueryType;
import com.yukthitech.autox.ide.search.FileSearchQuery.Scope;
import com.yukthitech.autox.ide.views.search.SearchResultPanel;

@Service
public class FileSearchService
{
	@Autowired
	private ProjectExplorer projectExplorer;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private SearchResultPanel searchResultPanel;
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	private List<File> getSearchFolders(FileSearchQuery query)
	{
		List<File> searchFolders = new ArrayList<>();
		
		if(query.getScope() == Scope.ALL_PROJECTS)
		{
			searchFolders.addAll(projectManager.getAllProjectFolders().values());
		}
		else
		{
			searchFolders.addAll(projectExplorer.getSelectedFiles());
		}

		return searchFolders;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void repeatSearch(ISearchOperation operation)
	{
		operation.reset();
		
		List<SearchResult> results = (List) operation.findAll();
		searchResultPanel.setSearchResults(operation, results);
	}
	
	public void replaceMatches(ISearchOperation operation, List<SearchResult> matches)
	{
		operation.replace(fileEditorTabbedPane, matches);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void findAll(FileSearchQuery query, boolean replaceOp)
	{
		List<File> searchFolders = getSearchFolders(query);
		
		ISearchOperation op = query.getQueryType() == QueryType.TEXT ?
				new TextSearchOperation(query, searchFolders, replaceOp) :
				new XpathSearchOperation(query, searchFolders, replaceOp);
		
		List<SearchResult> results = (List) op.findAll();
		
		if(results == null)
		{
			return;
		}
		
		searchResultPanel.setSearchResults(op, results);
	}
	
	public int replaceAll(FileSearchQuery query)
	{
		List<File> searchFolders = getSearchFolders(query);
		
		ISearchOperation op = query.getQueryType() == QueryType.TEXT ?
				new TextSearchOperation(query, searchFolders, true) :
				new XpathSearchOperation(query, searchFolders, true);

		return op.replaceAll(fileEditorTabbedPane);
	}
}
