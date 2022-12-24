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
package com.yukthitech.autox.ide.editor;

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
import javax.swing.text.BadLocationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.LinkGenerator;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.IIdeFileManager;
import com.yukthitech.autox.ide.IdeFileManagerFactory;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.dialog.FindCommand;
import com.yukthitech.autox.ide.dialog.FindOperation;
import com.yukthitech.autox.ide.editor.FileEditorIconGroup.IconType;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.MessageType;
import com.yukthitech.autox.ide.xmlfile.XmlFileLocation;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FileEditor extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(FileEditor.class);
	
	private static ImageIcon ERROR_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/bookmark-error.svg", 16);
	
	private static ImageIcon WARN_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/bookmark-warn.svg", 16);
	
	private static GutterPopup gutterPopup = new GutterPopup();
	
	private RTextScrollPane scrollPane;

	private Project project;

	private File file;

	private RSyntaxTextArea syntaxTextArea;
	
	private RSyntaxTextAreaHighlighter highlighter = new RSyntaxTextAreaHighlighter();

	private IconRowHeader iconArea;
	
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private IdeFileManagerFactory ideFileManagerFactory;
	
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
		
		scrollPane.setLineNumbersEnabled(true);

		this.file = file;

		try
		{
			String loadedText = FileUtils.readFileToString(file, Charset.defaultCharset());
			loadedText = IdeUtils.removeCarriageReturns(loadedText);
			
			syntaxTextArea.setText(loadedText);
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
				
				fileContentChanged(false);
			}
		});
		
		syntaxTextArea.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				onFocusGained(e);
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
				AutoCompletion ac = new AutoCompletion(provider) 
				{
					@Override
					protected void insertCompletion(Completion c, boolean typedParamListStartChar)
					{
						super.insertCompletion(c, typedParamListStartChar);
						provider.onAutoCompleteInsert(c);
					}
				};
				
				// show documentation dialog box
				ac.setShowDescWindow(true);
				ac.install(syntaxTextArea);
				
				//logger.debug("For file {} installing auto-complete from provider: {}", file.getPath(), provider);
			}
			
			syntaxTextArea.setHyperlinksEnabled(true);
			syntaxTextArea.setLinkGenerator(new LinkGenerator(){

				@Override
				public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea, int offs)
				{
					// TODO Auto-generated method stub
					return null;
				}
				
			});
		}

		setSyntaxStyle();
		syntaxTextArea.setCodeFoldingEnabled(true);

		fileContentChanged(false);
		
		IdeUtils.autowireBean(applicationContext, iconManager);
		
		if(executionSupported)
		{
			iconManager.loadDebugPoints();
		}
	}
	
	public boolean isContentChanged()
	{
		return contentChanged;
	}
	
	private void onFocusGained(FocusEvent e)
	{
		ideContext.setActiveDetails(project, file);
		ideContext.getProxy().activeFileChanged(file, this);
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
	
	public void setEditorFont(Font font)
	{
		syntaxTextArea.setFont(font);
	}
	
	public void setEnableTextWrapping(boolean wrap)
	{
		syntaxTextArea.setLineWrap(wrap);
		syntaxTextArea.setWrapStyleWord(wrap);
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
			ideContext.getProxy().fileSaved(file);
			contentChanged = false;
			
			//update all existing break points with update line numbers
			iconManager.resetDebugPoints();
		} catch(Exception ex)
		{
			logger.debug("An error occurred while saving file: " + file.getPath(), ex);
			JOptionPane.showMessageDialog(this, "An error occurred while saving file: " + file.getName() + "\nError: " + ex.getMessage());
		}
	}

	private void fileContentChanged(boolean contentChangedEvent)
	{
		if(contentChangedEvent)
		{
			ideContext.getProxy().fileChanged(file);
			contentChanged = true;
		}
		
		if(contentChangedEvent)
		{
			//from last change time, try to parse the content and highlight regions if any
			IdeUtils.executeConsolidatedJob("FileEditor.parseFileContent." + file.getName(), this::parseFileContent, IIdeConstants.FILE_EDITOR_PARSE_DELAY);
		}
		//if content is not changed, if job is already submitted, simply reshedule it
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
	
	public synchronized void highlightDebugLine(int lineNo)
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
			
			//work around for highlight bug - reduce line number by 1
			debugHighlightTag = syntaxTextArea.addLineHighlight(lineNo - 1, IIdeConstants.DEBUG_BG_COLOR);
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
	
	private void parseFileContent()
	{
		int selSt = syntaxTextArea.getSelectionStart();
		int selEnd = syntaxTextArea.getSelectionEnd();
		
		if(!this.currentHighlights.isEmpty())
		{
			clearAllMessages();
		}
		
		FileParseCollector collector = new FileParseCollector(project, file);
		parsedFileContent = currentFileManager.parseContent(project, file.getName(), syntaxTextArea.getText(), collector);
		
		collector.getMessages().stream().forEach(mssg -> this.addMessage(mssg));
		
		//this will ensure selection is not lost when resetting the messages
		if(selSt > 0 && selEnd > 0 && selEnd > selSt)
		{
			syntaxTextArea.select(selSt, selEnd);
		}
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
		
		syntaxTextArea.setText(content);
		
		if(carPos >= content.length())
		{
			carPos = content.length() - 1;
		}
		
		syntaxTextArea.setCaretPosition(carPos);
	}
	
	public int getCurrentLineNumber()
	{
		return syntaxTextArea.getCaretLineNumber();
	}
	
	public int getLineCount()
	{
		return syntaxTextArea.getLineCount();
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
			}
			
			syntaxTextArea.setCaretPosition(pos);
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

	public String getCurrentElementText(String nodeType)
	{
		return currentFileManager.getActiveElementText(this, nodeType);
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
	
	private boolean isWordChar(char ch)
	{
		if(Character.isAlphabetic(ch))
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
	
	private int getCaretPositionForFind(FindCommand command)
	{
		int curPos = syntaxTextArea.getCaretPosition();
		
		if(syntaxTextArea.getSelectedText() != null && syntaxTextArea.getSelectedText().length() > 0)
		{
			curPos = command.isReverseDirection() ? syntaxTextArea.getSelectionStart() : syntaxTextArea.getSelectionEnd();
		}
		
		return curPos;
	}
	
	private int[] find(FindCommand command, int startPos, int curPos, boolean wrapped, String fullText)
	{
		int idx = 0;
		
		if(command.isRegularExpression())
		{
			Pattern pattern = (command.getRegexOptions() == 0) ? Pattern.compile(command.getSearchString()) :
				Pattern.compile(command.getSearchString(), command.getRegexOptions());
			
			Matcher matcher = pattern.matcher(fullText);
			
			if(command.isReverseDirection())
			{
				int region[] = null;
				
				while(matcher.find())
				{
					if(matcher.end() > curPos)
					{
						break;
					}
					
					region = new int[] {matcher.start(), matcher.end()};
				}
				
				if(wrapped && region[0] < startPos)
				{
					return null;
				}
				
				return region;
			}
			else
			{
				if(!matcher.find(curPos))
				{
					return null;
				}
				
				if(wrapped && matcher.end() > startPos)
				{
					return null;
				}
				
				return new int[] {matcher.start(), matcher.end()};
			}
		}
		
		fullText = command.isCaseSensitive() ? fullText : fullText.toLowerCase();
		
		String searchStr = command.getSearchString();
		searchStr = command.isCaseSensitive() ? searchStr : searchStr.toLowerCase();
		
		if(command.isReverseDirection())
		{
			if(curPos > 0)
			{
				curPos = curPos - 1;
				idx = fullText.lastIndexOf(searchStr, curPos);
			}
			else
			{
				idx = -1;
			}
		}
		else
		{
			idx = fullText.indexOf(searchStr, curPos);
		}

		if(idx < 0)
		{
			if(command.isWrapSearch())
			{
				if(wrapped)
				{
					return null;
				}
				
				int resetPos = command.isReverseDirection() ? fullText.length() : 0;
				return find(command, startPos, resetPos, true, fullText);
			}
			
			return null;
		}

		return new int[] {idx, idx + command.getSearchString().length()};
	}
	
	/**
	 * Finds the string that needs to be used as replacement string.
	 * @param command command in use
	 * @param content current content
	 * @param range range being replaced
	 * @return string to be used for replacement.
	 */
	private String findReplaceString(FindCommand command, String content, int range[])
	{
		if(!command.isRegularExpression())
		{
			return command.getReplaceWith();
		}
		
		//extract the string that needs to be replaced
		String targetStr = content.substring(range[0], range[1]);
		
		//in obtained string replace the pattern, this will ensure $ expressions of regex is respected
		Pattern pattern = (command.getRegexOptions() == 0) ? Pattern.compile(command.getSearchString()) :
			Pattern.compile(command.getSearchString(), command.getRegexOptions());
		Matcher matcher = pattern.matcher(targetStr);
		StringBuffer buff = new StringBuffer();
		
		while(matcher.find())
		{
			matcher.appendReplacement(buff, command.getReplaceWith());
		}
		
		matcher.appendTail(buff);
		return buff.toString();
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
	
	public synchronized String executeFindOperation(FindCommand command, FindOperation op)
	{
		String fullText = syntaxTextArea.getText();
		int startPos = getCaretPositionForFind(command);
		
		switch(op)
		{
			case FIND:
			{
				int range[] = find(command, startPos, getCaretPositionForFind(command), false, fullText);
				
				if(range == null)
				{
					return "Search string not found.";
				}
				
				syntaxTextArea.select(range[0], range[1]);
				break;
			}
			case REPLACE:
			{
				if(syntaxTextArea.getSelectedText().length() <= 0)
				{
					return "No text is selected to replace";
				}
				
				int range[] = new int[] {syntaxTextArea.getSelectionStart(), syntaxTextArea.getSelectionEnd()};
				
				//If pattern find replace string
				
				syntaxTextArea.replaceRange(
						findReplaceString(command, fullText, range), 
						range[0], range[1]);
				syntaxTextArea.setCaretPosition(range[0] + command.getReplaceWith().length());
				break;
			}
			case REPLACE_AND_FIND:
			{
				if(syntaxTextArea.getSelectedText() == null || syntaxTextArea.getSelectedText().length() <= 0)
				{
					return "No text is selected to replace";
				}
				
				int range[] = new int[] {syntaxTextArea.getSelectionStart(), syntaxTextArea.getSelectionEnd()};
				
				String repString = findReplaceString(command, fullText, range);
				
				syntaxTextArea.replaceRange(repString, range[0], range[1]);
				syntaxTextArea.setCaretPosition(range[0] + repString.length());
				
				fullText = syntaxTextArea.getText();
				
				range = find(command, startPos, getCaretPositionForFind(command), false, fullText);
				
				if(range == null)
				{
					return "Search string not found.";
				}
				
				syntaxTextArea.select(range[0], range[1]);
				break;
			}
			default:
			{
				int count = 0;
				
				while(true)
				{
					int range[] = find(command, startPos, getCaretPositionForFind(command), false, fullText);
					
					if(range == null)
					{
						break;
					}
					
					count++;
					syntaxTextArea.replaceRange(command.getReplaceWith(), range[0], range[1]);
					syntaxTextArea.setCaretPosition(range[0] + command.getReplaceWith().length());
					
					fullText = syntaxTextArea.getText();
				}
				
				return count + " occurrences are replaced.";
			}
		}
		
		return "";
	}
}
