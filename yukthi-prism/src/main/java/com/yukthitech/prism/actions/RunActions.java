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
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.debug.common.ClientMssgExecuteSteps;
import com.yukthitech.prism.IIdeConstants;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.editor.FileEditor;
import com.yukthitech.prism.editor.FileEditorTabbedPane;
import com.yukthitech.prism.exeenv.ExecuteCommand;
import com.yukthitech.prism.exeenv.ExecutionEnvironment;
import com.yukthitech.prism.exeenv.ExecutionEnvironmentManager;
import com.yukthitech.prism.exeenv.ExecutionManager;
import com.yukthitech.prism.exeenv.ExecutionType;
import com.yukthitech.prism.layout.Action;
import com.yukthitech.prism.layout.ActionHolder;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.projexplorer.ProjectExplorer;

@ActionHolder
public class RunActions
{
	public static final String NODE_TEST_SUITE = "testsuite";
	
	public static final String NODE_TEST_CASE = "testcase";
	
	public static final String NODE_FUNCTION = "function";
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	@Autowired
	private ExecutionManager executionManager;

	@Autowired
	private ExecutionEnvironmentManager executionEnvManager;

	@Autowired
	private ProjectExplorer projectExplorer;
	
	private void runTestSuite(boolean debug)
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}

		Project project = fileEditor.getProject();
		String testSuite = fileEditor.getCurrentElementName(NODE_TEST_SUITE);
		
		if(testSuite == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite found at cusrsor position.");
			return;
		}
		
		File testSuiteFolder = projectExplorer.getTestSuiteFolder(fileEditor.getFile());
		
		executionManager.execute(new ExecuteCommand(ExecutionType.TEST_SUITE, project, testSuiteFolder, testSuite, debug));
	}
	
	private void runExecutable(boolean debug, boolean runToCurLine)
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "No file is active for execution.");
			return;
		}
		
		Project project = fileEditor.getProject();
		String testCaseName = fileEditor.getCurrentElementName(NODE_TEST_CASE);
		String funcName = fileEditor.getCurrentElementName(NODE_FUNCTION);
		
		if(testCaseName == null && funcName == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-case/function found at cursor position.");
			return;
		}
		
		File testSuiteFolder = projectExplorer.getTestSuiteFolder(fileEditor.getFile());
		ExecuteCommand exeCommand = new ExecuteCommand(
				(funcName == null) ? ExecutionType.TEST_CASE : ExecutionType.FUNCTION, 
				project, 
				testSuiteFolder, 
				(funcName == null) ? testCaseName : funcName, 
				debug);
		
		if(runToCurLine)
		{
			int lineNo = fileEditor.getCurrentLineNumber();
			exeCommand.setRunToLinePoint(fileEditor.getFile(), lineNo);
		}
		
		executionManager.execute(exeCommand);
	}

	@Action
	public void runTestSuite()
	{
		runTestSuite(false);
	}
	
	@Action
	public void runTestCase()
	{
		runExecutable(false, false);
	}
	
	@Action
	public void debugTestSuite()
	{
		runTestSuite(true);
	}
	
	@Action
	public void debugTestCase()
	{
		runExecutable(true, false);
	}

	public void executeStepCode(String code, Project project, Consumer<ExecutionEnvironment> envCallback)
	{
		executeStepCode(code, project, envCallback, null);
	}
	
	private void executeStepCode(String code, Project project, Consumer<ExecutionEnvironment> envCallback, String callbackMssg)
	{
		ExecutionEnvironment activeEnv = executionEnvManager.getActiveEnvironment();
		
		if(activeEnv == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "No active enviroment is selected.");
			return;
		}
		
		if(activeEnv.getProject() != project)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Current file is not part of active execution.");
			return;
		}
		
		if(!activeEnv.isDebugEnv())
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Active execution is not in debug/interactive mode.");
			return;
		}

		String currentTestSuiteName = null;
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor != null)
		{
			currentTestSuiteName = fileEditor.getCurrentElementName(NODE_TEST_SUITE);
		}
		
		activeEnv.sendDataToServer(new ClientMssgExecuteSteps(activeEnv.getActiveThreadId(), code, currentTestSuiteName));
	}
	
	@Action
	public synchronized void runSelectedSteps()
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}
		
		Project project = fileEditor.getProject();
		String selectedText = fileEditor.getSelectedText();
		
		if(selectedText == null)
		{
			selectedText = fileEditor.getCurrentElementText(IIdeConstants.ELEMENT_TYPE_STEP);
			
			if(selectedText == null)
			{
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no selected text for execution nor current location is part of any step.");
				return;
			}
		}

		executeStepCode(selectedText, project, null);
	}
	
	@Action
	public void runToCurrentStep()
	{
		runExecutable(true, true);
	}
	
	private synchronized void executeTestSuiteFolder(boolean debug) 
	{
		File activeFolder = projectExplorer.getSelectedFile();
		
		if(activeFolder == null || !activeFolder.isDirectory())
		{
			return;
		}
		
		Project project = projectExplorer.getSelectedProject();
		ExecutionType executionType = project.isTestSuiteFolder(activeFolder) ? ExecutionType.SOURCE_FOLDER : ExecutionType.FOLDER;

		File testSuiteFolder = project.isTestSuiteFolder(activeFolder) ?  activeFolder : projectExplorer.getTestSuiteFolder(activeFolder);
		executionManager.execute(new ExecuteCommand(executionType, project, testSuiteFolder, activeFolder.getPath(), debug));
	}
	
	private synchronized void executeProject(boolean debug) 
	{
		Project project = projectExplorer.getSelectedProject();
		executionManager.execute(new ExecuteCommand(ExecutionType.PROJECT, project, null, project.getName(), debug));
	}

	@Action
	public void executeTestSuiteFolder() 
	{
		executeTestSuiteFolder(false);
	}
	
	@Action
	public void executeProject() 
	{
		executeProject(false);
	}
}
