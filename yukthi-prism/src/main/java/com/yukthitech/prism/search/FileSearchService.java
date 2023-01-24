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
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.prism.editor.FileEditorTabbedPane;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.proj.ProjectManager;
import com.yukthitech.prism.projexplorer.ProjectExplorer;
import com.yukthitech.prism.search.FileSearchQuery.QueryType;
import com.yukthitech.prism.search.FileSearchQuery.Scope;
import com.yukthitech.prism.ui.InProgressDialog;
import com.yukthitech.prism.views.search.SearchResultPanel;

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
		if(query.getScope() == Scope.PROJECT)
		{
			searchFolders.add(query.getProject().getBaseFolder());
		}
		else
		{
			searchFolders.addAll(projectExplorer.getSelectedFiles());
		}

		return searchFolders;
	}
	
	private List<File> getIgnoredFolders(FileSearchQuery query)
	{
		List<File> ignoredFolders = new ArrayList<>();
		
		if(query.getScope() == Scope.ALL_PROJECTS)
		{
			projectManager.getAllProjects().forEach(proj -> 
			{
				File baseFolder = proj.getBaseFolder();
				
				proj.getIgnoreFoldersList().forEach(str -> 
				{
					ignoredFolders.add(new File(baseFolder, str));
				});
			});
		}
		if(query.getScope() == Scope.PROJECT)
		{
			Project proj = query.getProject();
			File baseFolder = proj.getBaseFolder();
			
			proj.getIgnoreFoldersList().forEach(str -> 
			{
				ignoredFolders.add(new File(baseFolder, str));
			});
		}

		return ignoredFolders;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void repeatSearch(ISearchOperation operation)
	{
		operation.reset();
		
		List<SearchResult> results = (List) operation.findAll();
		searchResultPanel.setSearchResults(operation, results);
	}
	
	public void replaceMatches(ISearchOperation operation, List<SearchResult> matches, String dynReplaceText)
	{
		InProgressDialog.getInstance().display("Replace operation in progress", () -> 
		{
			operation.replace(fileEditorTabbedPane, matches, dynReplaceText);
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void findAll(FileSearchQuery query, boolean replaceOp)
	{
		List<File> searchFolders = getSearchFolders(query);
		List<File> ignoredFolders = getIgnoredFolders(query);
		
		ISearchOperation op = query.getQueryType() == QueryType.TEXT ?
				new TextSearchOperation(query, searchFolders, ignoredFolders, replaceOp) :
				new XpathSearchOperation(query, searchFolders, ignoredFolders, replaceOp);
		
		InProgressDialog.getInstance().display("Search in progress", () -> 
		{
			List<SearchResult> results = (List) op.findAll();
			
			if(results == null)
			{
				return;
			}
			
			searchResultPanel.setSearchResults(op, results);
		});
	}
	
	public int replaceAll(FileSearchQuery query)
	{
		List<File> searchFolders = getSearchFolders(query);
		List<File> ignoredFolders = getIgnoredFolders(query);
		
		ISearchOperation op = query.getQueryType() == QueryType.TEXT ?
				new TextSearchOperation(query, searchFolders, ignoredFolders, true) :
				new XpathSearchOperation(query, searchFolders, ignoredFolders, true);

		Integer res = InProgressDialog.getInstance().display("Replace operation in progress", () -> 
		{
			return op.replaceAll(fileEditorTabbedPane);
		}, null);
		
		return (res == null) ? -1 : res;
	}
}
