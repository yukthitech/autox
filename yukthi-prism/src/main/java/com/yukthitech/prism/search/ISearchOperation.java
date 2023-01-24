package com.yukthitech.prism.search;

import java.util.List;

import com.yukthitech.prism.editor.FileEditorTabbedPane;

/**
 * Abstraction of search operation.
 * @author akiran
 */
public interface ISearchOperation
{

	void reset();

	boolean isReplaceOperation();
	
	default boolean isDynamicReplaceSupported()
	{
		return false;
	}
	
	default String getDynamicReplaceDefault()
	{
		return null;
	}

	int getMatchedFileCount();

	List<? extends SearchResult> findAll();

	void replace(FileEditorTabbedPane fileEditorTabbedPane, List<SearchResult> matches, String dynReplaceText);

	/**
	 * Should return number of replacements done. On error this should return current number of replacements + 1 as negative value.
	 * 
	 * So in case of no replacements value should be -1.
	 * 
	 * @param fileEditorTabbedPane
	 * @return
	 */
	int replaceAll(FileEditorTabbedPane fileEditorTabbedPane);

}