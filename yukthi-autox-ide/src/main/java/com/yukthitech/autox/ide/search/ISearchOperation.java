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

	void replaceAll(FileEditorTabbedPane fileEditorTabbedPane);

}