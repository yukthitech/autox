package com.yukthitech.autox.ide.search;

import java.util.List;

import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;

/**
 * Abstraction of search operation.
 * @author akiran
 */
public interface ISearchOperation
{

	void reset();

	boolean isReplaceOperation();

	int getMatchedFileCount();

	List<? extends SearchResult> findAll();

	void replace(FileEditorTabbedPane fileEditorTabbedPane, List<SearchResult> matches);

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