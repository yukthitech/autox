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
package com.yukthitech.prism.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.AutoCompletionEvent;
import org.fife.ui.autocomplete.AutoCompletionEvent.Type;
import org.fife.ui.autocomplete.AutoCompletionListener;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.yukthitech.autox.common.IndexRange;
import com.yukthitech.prism.IIdeConstants;
import com.yukthitech.prism.IIdeFileManager;
import com.yukthitech.prism.IdeFileManagerFactory;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.actions.FileActions;
import com.yukthitech.prism.common.CodeSnippet;
import com.yukthitech.prism.editor.FileEditorIconGroup.IconType;
import com.yukthitech.prism.events.ActiveFileChangedEvent;
import com.yukthitech.prism.events.FileSavedEvent;
import com.yukthitech.prism.index.FileIndex;
import com.yukthitech.prism.index.FileParseCollector;
import com.yukthitech.prism.index.ProjectIndex;
import com.yukthitech.prism.index.ReferableElement;
import com.yukthitech.prism.index.ReferenceElement;
import com.yukthitech.prism.model.Project;
import com.yukthitech.prism.proj.ProjectManager;
import com.yukthitech.prism.services.GlobalStateManager;
import com.yukthitech.prism.services.IdeEventManager;
import com.yukthitech.prism.services.IdeSettingsManager;
import com.yukthitech.prism.xmlfile.MessageType;
import com.yukthitech.prism.xmlfile.XmlFileLocation;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FileEditor extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(FileEditor.class);
	
	private static ImageIcon ERROR_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/bookmark-error.svg", 16);
	
	private static ImageIcon WARN_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/bookmark-warn.svg", 16);
	
	private static Pattern STARTING_WORD = Pattern.compile("^(\\w+)");
	
	private static GutterPopup gutterPopup = new GutterPopup();
	
	private class LinkeGeneratorResultImpl implements LinkGeneratorResult
	{
		private ReferenceElement ref;
		
		public LinkeGeneratorResultImpl(ReferenceElement ref)
		{
			this.ref = ref;
		}

		@Override
		public HyperlinkEvent execute()
		{
			ProjectIndex projectIndex = projectManager.getProjectIndex(project.getName());
			ReferableElement element = projectIndex.getReferableElement(ref.getType(), ref.getName(), ref.getScopes());
			
			if(element == null)
			{
				return null;
			}
			
			//push the current hyper link position on history
			navigationHistoryManager.push(project, file, new IndexRange(ref.getStartPostion(), ref.getEndPosition()));
			//then push the target position to history, which would act as current location
			navigationHistoryManager.push(project, element.getFile(), element.getSelectionRange());
			
			fileActions.gotoFilePath(project, element.getFile().getPath(), -1, element.getSelectionRange());
			return null;
		}

		@Override
		public int getSourceOffset()
		{
			return ref.getStartPostion();
		}
	}
	
	private RTextScrollPane scrollPane;

	private Project project;

	private File file;

	private RSyntaxTextArea syntaxTextArea;
	
	private RSyntaxTextAreaHighlighter highlighter = new RSyntaxTextAreaHighlighter();

	private IconRowHeader iconArea;
	
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private IdeFileManagerFactory ideFileManagerFactory;
	
	@Autowired
	private IdeEventManager eventManager;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private FileActions fileActions;
	
	@Autowired
	private NavigationHistoryManager navigationHistoryManager;
	
	/**
	 * File manager for current file.
	 */
	private IIdeFileManager currentFileManager;
	
	private List<FileParseMessage> currentHighlights = new ArrayList<>();
	
	/**
	 * Content parsed in last iteration.
	 */
	private Object parsedFileContent;
	
	private ErrorHighLigherPanel errorHighLigherPanel;
	
	private FileEditorIconManager iconManager;
	
	private Object debugHighlightTag;
	
	private boolean contentChanged = false;
	
	private FileEditorTab fileEditorTab;
	
	/**
	 * Maintains list of references in this file (clickable elements).
	 */
	private FileIndex fileIndex = new FileIndex();
	
	/**
	 * Used to updates focus state of the editor.
	 */
	@Autowired
	private GlobalStateManager globalStateManager;
	
	private AutoCompletion autoCompletion;
	
	private boolean autoCompltetionTriggered = false;
	
	@Autowired
	private IdeSettingsManager ideSettingsManager;
	
	public FileEditor(Project project, File file)
	{
		scrollPane = new RTextScrollPane(new RSyntaxTextArea());
		scrollPane.getGutter().setBookmarkingEnabled(true);

		errorHighLigherPanel = new ErrorHighLigherPanel(this);
		
		super.setLayout(new BorderLayout());
		super.add(scrollPane, BorderLayout.CENTER);
		super.add(errorHighLigherPanel, BorderLayout.EAST);

		try
		{
			Field iconAreaFld = scrollPane.getGutter().getClass().getDeclaredField("iconArea");
			iconAreaFld.setAccessible(true);
			iconArea = (IconRowHeader) iconAreaFld.get(scrollPane.getGutter());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching icon area", ex);
		}
		
		iconArea.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(!iconManager.isExecutionSupported())
				{
					return;
				}
				
				if(SwingUtilities.isRightMouseButton(e))
				{
					gutterPopup.setActiveEditor(FileEditor.this, 
							e.getPoint(), 
							iconManager.getLineNo(e.getPoint()),
							iconManager.getDebugPoint(e.getPoint()) != null);
					
					iconArea.add(gutterPopup);
					gutterPopup.show(e.getComponent(), e.getX(), e.getY());
				}
				else if(e.getClickCount() > 1)
				{
					iconManager.toggleBreakPoint(e.getPoint());
				}
			}
		});

		this.project = project;
		this.syntaxTextArea = (RSyntaxTextArea) scrollPane.getViewport().getView();
		this.syntaxTextArea.setHighlighter(highlighter);
		this.syntaxTextArea.setToolTipSupplier(this::getToolTip);
		this.syntaxTextArea.setCurrentLineHighlightColor(new Color(255, 255, 172, 128));
		
		scrollPane.setLineNumbersEnabled(true);

		this.file = file;

		try
		{
			String loadedText = FileUtils.readFileToString(file, Charset.defaultCharset());
			loadedText = IdeUtils.removeCarriageReturns(loadedText);
			
			syntaxTextArea.setText(loadedText);
			syntaxTextArea.discardAllEdits();
			syntaxTextArea.setCaretPosition(0);
		} catch(Exception ex)
		{
			ex.printStackTrace();
		}

		syntaxTextArea.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				fileContentChanged(true);
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				fileContentChanged(true);
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				fileContentChanged(true);
			}
		});

		//during key events also fire content changed, so that content is not restyled
			// when typing is going on, even when content is not changed (like arrow keys)
		syntaxTextArea.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_HOME)
				{
					handleHomeKey(e);
					return;
				}
				
				fileContentChanged(false);
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				//if auto completion was triggered but closed because of typing
				if(autoCompltetionTriggered && !autoCompletion.isPopupVisible())
				{
					char ch = e.getKeyChar();
					int code = e.getKeyCode();
				
					//chek if char can be continue auto-trigger, if so display auto complete again
					if(Character.isJavaIdentifierStart(ch) || ch == '-' || code == KeyEvent.VK_BACK_SPACE)
					{
						autoCompletion.doCompletion();
					}

					//if non-word chars are typed closed auto trigger
					
					//Also when word chars are used close trigger part, so that if 
					// auto completions are displayed with current change, then only the redisplay gets enabled
					autoCompltetionTriggered = false;
				}
		
				fileContentChanged(false);
			}
		});
		
		syntaxTextArea.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				globalStateManager.focusGained(syntaxTextArea);
				onFocusGained(e);
			}
		});

		syntaxTextArea.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				int amount = e.getWheelRotation();
				
				if(!e.isControlDown())
				{
					JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
					
					if(scrollBar != null)
					{
						int lineHeight = syntaxTextArea.getFontMetrics(syntaxTextArea.getFont()).getHeight();
						int val = scrollBar.getValue() + lineHeight * 2 * amount;
						scrollBar.setValue(val);
					}
					
					return;
				}
				
				IdeUtils.executeConsolidatedJob("editor.changeFontSize", () -> 
				{
					ideSettingsManager.changeFontSize(amount < 0);	
				}, 10);
				
				e.consume();
			}
		});

		syntaxTextArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl ENTER"), "dummy");
		syntaxTextArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl F1"), "dummy help");
		//syntaxTextArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl shift R"), "dummy");
		
		syntaxTextArea.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5");
		syntaxTextArea.getInputMap().put(KeyStroke.getKeyStroke("F6"), "F6");
		syntaxTextArea.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8");
		
		syntaxTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.VK_CONTROL | KeyEvent.VK_SHIFT), "dummy");
	}
	
	@PostConstruct
	private void init()
	{
		this.currentFileManager = ideFileManagerFactory.getFileManager(project, file);
		boolean executionSupported = this.currentFileManager.isExecutionSupported();
		
		iconManager = new FileEditorIconManager(this, executionSupported, iconArea, scrollPane.getGutter());
		
		if(this.currentFileManager != null)
		{
			IIdeCompletionProvider provider = currentFileManager.getCompletionProvider(this);
			
			if(provider != null)
			{
				autoCompletion = new AutoCompletion(provider) 
				{
					{
						setHideOnNoText(true);
					}
					
					@Override
					protected void insertCompletion(Completion c, boolean typedParamListStartChar)
					{
						super.insertCompletion(c, typedParamListStartChar);
						provider.onAutoCompleteInsert(c);
					}
				};
				
				// show documentation dialog box
				autoCompletion.setShowDescWindow(true);
				autoCompletion.install(syntaxTextArea);
				
				autoCompletion.addAutoCompletionListener(new AutoCompletionListener()
				{
					@Override
					public void autoCompleteUpdate(AutoCompletionEvent e)
					{
						if(e.getEventType() == Type.POPUP_SHOWN)
						{
							autoCompltetionTriggered = true;
						}
					}
				});
				
				//logger.debug("For file {} installing auto-complete from provider: {}", file.getPath(), provider);
			}
			
			syntaxTextArea.setHyperlinksEnabled(true);
			syntaxTextArea.setLinkGenerator(this::generateLink);
		}

		setSyntaxStyle();
		syntaxTextArea.setCodeFoldingEnabled(true);

		IdeUtils.autowireBean(applicationContext, iconManager);
		
		if(executionSupported)
		{
			iconManager.loadDebugPoints();
		}
	}
	
	void editorClosed()
	{
		globalStateManager.componentRemoved(syntaxTextArea);
	}
	
	void setFileEditorTab(FileEditorTab tab)
	{
		this.fileEditorTab = tab;
		
		// as tab is set post init of editor and tab
		//   parse the content post setting the tab
		IdeUtils.executeConsolidatedJob("FileEditor.parseFileContent." + file.getName(), this::parseFileContent, IIdeConstants.FILE_EDITOR_PARSE_DELAY);
	}
	
	private LinkGeneratorResult generateLink(RSyntaxTextArea textArea, int offs)
	{
		ReferenceElement element = fileIndex.getReference(offs);
		
		if(element == null)
		{
			return null;
		}
		
		return new LinkeGeneratorResultImpl(element);
	}
	
	public boolean isContentChanged()
	{
		return contentChanged;
	}
	
	private void onFocusGained(FocusEvent e)
	{
		eventManager.raiseAsyncEvent(new ActiveFileChangedEvent(project, file, this));
	}
	
	private void handleHomeKey(KeyEvent e)
	{
		try
		{
			int curPos = syntaxTextArea.getCaretPosition();
			int curLine = syntaxTextArea.getCaretLineNumber();
			int stOffset = syntaxTextArea.getLineStartOffset(curLine);
			
			int endOffset = syntaxTextArea.getLineEndOffset(curLine);
			char lineText[] = syntaxTextArea.getText().substring(stOffset, endOffset).toCharArray();
			
			int extraOffset = 0;
			
			for(char ch : lineText)
			{
				if(Character.isWhitespace(ch))
				{
					extraOffset ++;
					continue;
				}
				
				break;
			}
			
			e.consume();
			
			int textStartOffset = stOffset + extraOffset; 
			int finalPos = (textStartOffset != curPos) ? textStartOffset : stOffset;
			
			if(e.isShiftDown())
			{
				//select the text from current position to final position
				syntaxTextArea.moveCaretPosition(finalPos);
			}
			else
			{
				syntaxTextArea.setCaretPosition(finalPos);
			}
		}catch(Exception ex)
		{
			//ignore
		}
	}
	
	public synchronized void changeSettings(Font font, Boolean wrap)
	{
		if(font != null)
		{
			syntaxTextArea.setFont(font);
		}
		
		if(wrap != null)
		{
			syntaxTextArea.setLineWrap(wrap);
			syntaxTextArea.setWrapStyleWord(wrap);
		}
	}
	
	public RSyntaxTextArea getTextArea()
	{
		return syntaxTextArea;
	}
	
	public FileEditorIconManager getIconManager()
	{
		return iconManager;
	}
	
	public void insertStepCode(String code)
	{
		int pos = syntaxTextArea.getCaretPosition();
		syntaxTextArea.insert(code, pos);
	}
	
	public XmlFileLocation getXmlFileLocation()
	{
		return currentFileManager.getXmlFileLocation(this);
	}
	
	public boolean isStepInsertablePosition()
	{
		return currentFileManager.isStepInsertablePosition(this);
	}
	
	public List<FileParseMessage> getCurrentHighlights()
	{
		return currentHighlights;
	}

	private void setSyntaxStyle()
	{
		int extIdx = file.getName().lastIndexOf(".");
		String extension = null;

		if(extIdx > 0 && extIdx < file.getName().length() - 1)
		{
			extension = file.getName().substring(extIdx + 1).toLowerCase();
		}
		
		((RSyntaxDocument) syntaxTextArea.getDocument()).setTokenMakerFactory(new FileEditorTokenFactory(currentFileManager));

		String style = currentFileManager.getSyntaxEditingStyle(extension);
		syntaxTextArea.setSyntaxEditingStyle(style);
	}

	public void setFile(File file)
	{
		this.file = file;
		setSyntaxStyle();
	}

	public Project getProject()
	{
		return project;
	}

	public void saveFile()
	{
		try
		{
			if(!file.canWrite())
			{
				int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), "Current file '" + file.getName() + "' is read-only file. Do you still want to overwite the file?", "Save File", JOptionPane.YES_NO_OPTION);

				if(res == JOptionPane.NO_OPTION)
				{
					return;
				}

				file.setWritable(true);
			}

			FileUtils.write(file, syntaxTextArea.getText(), Charset.defaultCharset());
			
			markAsSaved();
			
			//update all existing break points with update line numbers
			iconManager.resetDebugPoints();
		} catch(Exception ex)
		{
			logger.debug("An error occurred while saving file: " + file.getPath(), ex);
			JOptionPane.showMessageDialog(this, "An error occurred while saving file: " + file.getName() + "\nError: " + ex.getMessage());
		}
	}
	
	public void markAsSaved()
	{
		fileEditorTab.fileContentSaved();
		eventManager.raiseAsyncEvent(new FileSavedEvent(project, file));
		contentChanged = false;
	}

	private void fileContentChanged(boolean contentChangedEvent)
	{
		//ignore method call, which occurs during init of file editor
		if(fileEditorTab == null)
		{
			return;
		}
		
		if(contentChangedEvent)
		{
			fileEditorTab.fileContentChanged();
			contentChanged = true;
		}
		
		if(contentChangedEvent)
		{
			//from last change time, try to parse the content and highlight regions if any
			IdeUtils.executeConsolidatedJob("FileEditor.parseFileContent." + file.getName(), this::parseFileContent, IIdeConstants.FILE_EDITOR_PARSE_DELAY);
		}
		//if content is not changed, if job is already submitted, simply reshedule it (so that job is not present yet, this call be ignored)
		else
		{
			IdeUtils.rescheduleConsolidatedJob("FileEditor.parseFileContent." + file.getName(), this::parseFileContent, IIdeConstants.FILE_EDITOR_PARSE_DELAY);
		}
	}
	
	private String getToolTip(RTextArea textArea, MouseEvent e)
	{
		int offset = textArea.viewToModel(e.getPoint());
		
		if(offset < 0)
		{
			return null;
		}
		
		for(FileParseMessage mssg : this.currentHighlights)
		{
			if(!mssg.hasValidOffsets())
			{
				continue;
			}
			
			if(offset >= mssg.getStartOffset() && offset <= mssg.getEndOffset())
			{
				return mssg.getMessage();
			}
		}
		
		return currentFileManager.getToolTip(this, parsedFileContent, offset);
	}
	
	public synchronized void highlightDebugLine(int lineNo, boolean errorPoint)
	{
		if(debugHighlightTag != null)
		{
			syntaxTextArea.removeLineHighlight(debugHighlightTag);
		}
		
		int actLineNo = lineNo;
		
		try
		{
			//convert to zero index line
			lineNo = lineNo - 1;
			
			logger.debug("Highlighting debug point line {}:{}", file.getName(), lineNo);

			//work around for highlight bug - reduce line number by 1
			debugHighlightTag = syntaxTextArea.addLineHighlight(lineNo - 1, 
					errorPoint ?  IIdeConstants.DEBUG_ERR_BG_COLOR : IIdeConstants.DEBUG_BG_COLOR);
		}catch(BadLocationException ex)
		{
			logger.error("Wrong line number specified for debug highlight: {}", lineNo, ex);
		}
		
		IdeUtils.executeUiTask(() -> makeLineVisible(actLineNo));
	}
	
	public void makeLineVisible(int lineNo)
	{
		try
		{
			int minYPos = syntaxTextArea.yForLine(lineNo - 3);
			minYPos = (minYPos < 0) ? 0 : minYPos;
			
			JScrollBar verScroll = scrollPane.getVerticalScrollBar();
			int maxYPos = syntaxTextArea.yForLine(lineNo);
			maxYPos = (maxYPos > verScroll.getMaximum()) ? verScroll.getMaximum() : maxYPos;
			
			Rectangle view = scrollPane.getViewport().getViewRect();
			
			if(minYPos >= view.y && maxYPos < (view.y + view.height))
			{
				return;
			}
			
			int finalPos = 0;
			
			//if required line is below current view
			if(minYPos >= view.y)
			{
				//align the view bottom line to max y
				finalPos = maxYPos - view.height;
			}
			//if required line is above current view
			else
			{
				//align the top line to min y
				finalPos = minYPos;
			}
			
			verScroll.setValue(finalPos);
			scrollPane.invalidate();
		}catch(BadLocationException ex)
		{
			logger.error("Wrong line number specified for debug highlight: {}", lineNo, ex);
		}
	}
	
	public void clearHighlightDebugLine()
	{
		if(debugHighlightTag != null)
		{
			syntaxTextArea.removeLineHighlight(debugHighlightTag);
		}
	}

	private void addMessage(FileParseMessage message)
	{
		try
		{
			if(message.getMessageType() == MessageType.ERROR)
			{
				iconManager.addIcon(message.getLineNo() - 1, ERROR_ICON, message.getMessage(), IconType.ERROR);
				
				if(message.hasValidOffsets())
				{
					highlighter.addHighlight(message.getStartOffset(), message.getEndOffset(), new SquiggleUnderlineHighlightPainter(Color.red));
				}
			}
			else
			{
				iconManager.addIcon(message.getLineNo() - 1, WARN_ICON, message.getMessage(), IconType.WARNING);
				
				if(message.hasValidOffsets())
				{
					highlighter.addHighlight(message.getStartOffset(), message.getEndOffset(), 
							new RectangleHighlighter(ErrorHighLigherPanel.WARNING_BORDER));
				}
			}
			
			this.currentHighlights.add(message);
			this.errorHighLigherPanel.addMessage(message);
			
		}catch(BadLocationException ex)
		{
			throw new InvalidStateException("An error occurred while adding xml file message", ex);
		}
	}
	
	private void clearAllMessages()
	{
		//remove all non debug points
		iconManager.clearIcons(false);

		//remove high lights
		highlighter.removeAllHighlights();
		
		this.currentHighlights.clear();
		this.errorHighLigherPanel.clear();
	}
	
	public FileParseCollector parseFileContent()
	{
		int selSt = syntaxTextArea.getSelectionStart();
		int selEnd = syntaxTextArea.getSelectionEnd();
		
		if(!this.currentHighlights.isEmpty())
		{
			clearAllMessages();
		}
		
		FileParseCollector collector = new FileParseCollector(project, file);
		parsedFileContent = currentFileManager.parseContent(project, file, syntaxTextArea.getText(), collector);
		
		//update indexes
		fileIndex.setReferences(collector.getReferences());
		projectManager.getProjectIndex(project.getName()).addReferableElements(file, collector.getReferableElements());
		
		fileEditorTab.setParseResults(collector.getErrorCount() > 0, collector.getWarningCount() > 0);
		
		collector.getMessages().stream().forEach(mssg -> this.addMessage(mssg));
		
		//this will ensure selection is not lost when resetting the messages
		if(selSt > 0 && selEnd > 0 && selEnd > selSt)
		{
			syntaxTextArea.select(selSt, selEnd);
		}
		
		return collector;
	}

	public int getCaretPosition()
	{
		return syntaxTextArea.getCaretPosition();
	}

	public void setCaretPosition(int position)
	{
		syntaxTextArea.setCaretPosition(position);
		
		Point viewPoint = syntaxTextArea.getCaret().getMagicCaretPosition();
		
		if(viewPoint != null)
		{
			syntaxTextArea.scrollRectToVisible(new Rectangle(viewPoint, new Dimension(1, 1)));
		}
	}
	
	public String getContent()
	{
		return syntaxTextArea.getText();
	}
	
	public void setContent(String content)
	{
		int carPos = syntaxTextArea.getCaretPosition();
		
		syntaxTextArea.beginAtomicEdit();
		
		try
		{
			syntaxTextArea.setText(content);
		}finally
		{
			syntaxTextArea.endAtomicEdit();
		}
		
		if(carPos >= content.length())
		{
			carPos = content.length() - 1;
		}
		
		syntaxTextArea.setCaretPosition(carPos);
		IdeUtils.executeConsolidatedJob("FileEditor.parseFileContent." + file.getName(), this::parseFileContent, IIdeConstants.FILE_EDITOR_PARSE_DELAY);
	}
	
	public int getCurrentLineNumber()
	{
		return syntaxTextArea.getCaretLineNumber();
	}
	
	public int getLineCount()
	{
		return syntaxTextArea.getLineCount();
	}
	
	public void scrollToCaretPosition()
	{
		int pos = syntaxTextArea.getCaretPosition();
		
		// move cursor to a different position and back to same position, this will ensure
		//  scroll bars are moved approp
		if(pos > 0)
		{
			syntaxTextArea.setCaretPosition(pos - 1);
		}
		else
		{
			try
			{
				syntaxTextArea.setCaretPosition(pos + 1);
			}catch(Exception ex)
			{
				//ignore if moving to this temp position failed
			}
		}
		
		syntaxTextArea.setCaretPosition(pos);
	}
	
	public void gotoLine(int line)
	{
		if(line < 1 || line > getLineCount())
		{
			return;
		}
		
		line = line - 1;
		
		try
		{
			syntaxTextArea.requestFocus();
			
			int pos = syntaxTextArea.getLineStartOffset(line);
			
			//if current position is same as new position
			if(syntaxTextArea.getCaretPosition() == pos)
			{
				scrollToCaretPosition();
			}
			else
			{
				syntaxTextArea.setCaretPosition(pos);
			}
		}catch(BadLocationException ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error occurred while moving cursor to specified location. Error: " + ex);
		}
	}

	public String getCurrentElementName(String nodeType)
	{
		return currentFileManager.getActiveElement(this, nodeType);
	}

	public int getCurrentElementLineNo(String nodeType)
	{
		return currentFileManager.getActiveElementLineNumber(this, nodeType);
	}

	public CodeSnippet getCurrentElementText(String nodeType)
	{
		return currentFileManager.getActiveElementText(this, nodeType);
	}
	
	public IndexRange getSelectedRange()
	{
		int st = syntaxTextArea.getSelectionStart();
		int end = syntaxTextArea.getSelectionEnd();
		
		return new IndexRange(st, end);
	}

	public String getSelectedText()
	{
		String selectedText = syntaxTextArea.getSelectedText();

		if(StringUtils.isNotBlank(selectedText))
		{
			return selectedText;
		}

		return null;
	}
	
	public CodeSnippet getSelectedCodeSnippet()
	{
		String selectedText = syntaxTextArea.getSelectedText();

		if(StringUtils.isNotBlank(selectedText))
		{
			int lineNo = -1;
			
			try
			{
				lineNo = syntaxTextArea.getLineOfOffset(syntaxTextArea.getSelectionStart());
			}catch(BadLocationException ex)
			{
				throw new InvalidStateException("An error occurred while fetching line number of selected text", ex);
			}
			
			return new CodeSnippet(selectedText, file, lineNo);
		}

		return null;
	}

	public void selectText(int start, int end)
	{
		syntaxTextArea.select(start, end);
	}
	
	private boolean isWordChar(char ch)
	{
		if(Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_')
		{
			return true;
		}
		
		return (ch == '-');
	}
	
	/**
	 * Fetches the current word at the cursor.
	 * @return
	 */
	public String getCursorWord()
	{
		String selectedText = syntaxTextArea.getSelectedText();
		
		if(selectedText != null)
		{
			Matcher matcher = STARTING_WORD.matcher(selectedText);
			
			if(!matcher.find())
			{
				return null;
			}
			
			return matcher.group(1);
		}
		
		try
		{
			int line = syntaxTextArea.getCaretLineNumber();
			int lineStart = syntaxTextArea.getLineStartOffset(line);
			int lineEnd = syntaxTextArea.getLineEndOffset(line);
	
			//if current line is not having any text
			if(lineStart >= lineEnd)
			{
				return null;
			}
			
			String lineText = syntaxTextArea.getText(lineStart, (lineEnd - lineStart) + 1);
			int pos = syntaxTextArea.getCaretOffsetFromLineStart();
			
			char ch[] = lineText.toCharArray();
			
			int st = -1;
			
			for(int i = pos; i >= 0 && isWordChar(ch[i]); i--)
			{
				st = i;
			}
			
			if(st < 0)
			{
				return null;
			}
			
			int end = pos;
			
			for(int i = pos; i < ch.length && isWordChar(ch[i]); i++)
			{
				end = i;
			}
			
			if(st >= end)
			{
				return null;
			}
			
			return new String(ch, st, (end - st + 1));
		}catch(Exception ex)
		{
			logger.error("An error occurred while fetching current word", ex);
			return null;
		}
	}

	public File getFile()
	{
		return file;
	}
	
	public void changeCase(boolean toUpperCase)
	{
		int st = syntaxTextArea.getSelectionStart();
		int end = syntaxTextArea.getSelectionEnd();
		
		if(st >= end)
		{
			return;
		}
		
		String selectedText = syntaxTextArea.getSelectedText();
		selectedText = toUpperCase ? selectedText.toUpperCase() : selectedText.toLowerCase();
		
		syntaxTextArea.replaceRange(selectedText, st, end);
	}
	
}
