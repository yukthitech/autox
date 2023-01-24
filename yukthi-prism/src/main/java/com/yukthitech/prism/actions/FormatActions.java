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
package com.yukthitech.prism.actions;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rtextarea.RTextArea;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.editor.FileEditor;
import com.yukthitech.prism.editor.FileEditorTabbedPane;
import com.yukthitech.prism.format.XmlFormatter;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionHolder;

@ActionHolder
public class FormatActions
{
	private static Logger logger = LogManager.getLogger(FormatActions.class);
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;

	@Action
	public void formatCode()
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "No active file editor found for formatting.");
			return;
		}

		RTextArea textArea = fileEditor.getTextArea();
		String currentText = textArea.getText();
		currentText = formatFile(fileEditor.getFile(), currentText);
		
		if(currentText == null)
		{
			return;
		}
		
		int caretPos = textArea.getCaretPosition(); 
		fileEditor.getTextArea().replaceRange(currentText, 0, textArea.getText().length());
		
		try
		{
			textArea.setCaretPosition(caretPos);
		}catch(Exception ex)
		{
			logger.info("Failed to set caret position post formatting. Ignoring the error: " + ex);
		}
	}
	
	private String formatFile(File file, String content)
	{
		if(file.getName().toLowerCase().endsWith(".xml"))
		{
			return XmlFormatter.formatXml(content);
		}
		
		return content;
	}
	
}
