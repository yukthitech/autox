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
package com.yukthitech.autox.ide.actions;

import java.io.File;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.debug.common.ClientMssgExecuteSteps;
import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironmentManager;
import com.yukthitech.autox.ide.exeenv.ExecutionManager;
import com.yukthitech.autox.ide.exeenv.ExecutionType;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;

@ActionHolder
public class RunActions
{
	public static final String NODE_TEST_SUITE = "testsuite";
	
	public static final String NODE_TEST_CASE = "testcase";
	
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
		
		executionManager.execute(ExecutionType.TEST_SUITE, project, testSuite, debug);
	}
	
	private void runTestCase(boolean debug)
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}
		
		Project project = fileEditor.getProject();
		String testCase = fileEditor.getCurrentElementName(NODE_TEST_CASE);
		
		if(testCase == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-case found at cursor position.");
			return;
		}
		
		executionManager.execute(ExecutionType.TEST_CASE, project, testCase, debug);
	}

	@Action
	public void runTestSuite()
	{
		runTestSuite(false);
	}
	
	@Action
	public void runTestCase()
	{
		runTestCase(false);
	}
	
	@Action
	public void debugTestSuite()
	{
		runTestSuite(true);
	}
	
	@Action
	public void debugTestCase()
	{
		runTestCase(true);
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
		
		/*
		if(activeEnv == null)
		{
			final String mssg = "Starting interactive environment for project: " + project.getName();
			inProgressDialog=InProgressDialog.getInstance();
			inProgressDialog.display(mssg, new Runnable()
			{
				@Override
				public void run()
				{
					logger.debug("Starting interactive environment for project: {}", project.getName());
					
					ExecutionEnvironment newInteractiveEnv = executionManager.execute(ExecutionType.INTERACTIVE, project, null);
					inProgressDialog.setSubmessage("Waiting for environment to get started...");
					
					while(!newInteractiveEnv.isReadyToInteract() && !newInteractiveEnv.isTerminated())
					{
						try
						{
							Thread.sleep(100);
						}catch(Exception ex)
						{}
					}
					
					if(!newInteractiveEnv.isTerminated())
					{
						if(code != null)
						{
							newInteractiveEnv.sendDataToServer(new ClientMssgExecuteSteps(code));
						}
						
						if(envCallback != null)
						{
							if(callbackMssg != null)
							{
								inProgressDialog.setSubmessage(callbackMssg);
							}
							
							envCallback.accept(newInteractiveEnv);
						}
					}
				}
			});
		}
		else
		{
			activeEnv.sendDataToServer(new ClientMssgExecuteSteps(code));
		}
		*/
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
	public synchronized void runToCurrentStep()
	{
		JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "This functionality is not yet available");
		/*
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}
		
		Project project = fileEditor.getProject();
		ExecutionEnvironment interactiveEnv = executionManager.getInteractiveEnvironment(project);
		
		if(interactiveEnv != null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "An interactive environment is already active.");
			return;
		}

		String testCaseName = fileEditor.getCurrentElementName(NODE_TEST_CASE);
		int stepLineNo = testCaseName != null ? fileEditor.getCurrentElementLineNo(IIdeConstants.ELEMENT_TYPE_STEP) : -1;
		
		if(testCaseName == null || stepLineNo <= 0)
		{
			int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), "At current position found no test-case or executable step.\n"
					+ "Would you like to execute global setup only?", "Run To Position..", JOptionPane.YES_NO_OPTION);
			
			if(res == JOptionPane.NO_OPTION)
			{
				return;
			}
		}
		
		executeStepCode(null, project, env -> 
		{
			logger.debug("Sending command to execute test case '{}' till line number: {}", testCaseName, stepLineNo);
			ObjectWrapper<Boolean> testCaseExecuted = new ObjectWrapper<>(false);
			ObjectWrapper<Boolean> isTerminated = new ObjectWrapper<>(false);
			
			/*
			env.sendDataToServer(new InteractiveTestCaseExecDetails(testCaseName, fileEditor.getFile().getPath(), stepLineNo), new IMessageCallback()
			{
				@Override
				public void onProcess(MessageConfirmationServerMssg confirmation)
				{
					logger.debug("Interactive environment testcase execution is completed. Environment is ready to use...");
					testCaseExecuted.setValue(true);
				}
				
				@Override
				public void terminated()
				{
					logger.debug("Interactive environment terminated abruptly");
					testCaseExecuted.setValue(true);
					isTerminated.setValue(true);
				}
			});
			* /
			
			logger.debug("Interactive environment is started. Waiting for test case execution to completed to come to current point...");
			
			while(!testCaseExecuted.getValue())
			{
				try
				{
					Thread.sleep(100);
				}catch(Exception ex)
				{}
			}
			
			if(isTerminated.getValue())
			{
				logger.debug("Test case execution completed. Environment is ready to interact...");
			}
		}, String.format("Executing test case '%s' till line number: %s", testCaseName, stepLineNo));
		*/
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

		executionManager.execute(executionType, project, activeFolder.getPath(), debug);
	}
	
	private synchronized void executeProject(boolean debug) 
	{
		Project project = projectExplorer.getSelectedProject();
		executionManager.execute(ExecutionType.PROJECT, project, null, debug);
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
