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
package com.yukthitech.prism.xmlfile;

import java.io.File;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.prism.AbstractIdeFileManager;
import com.yukthitech.prism.IdeNotificationPanel;
import com.yukthitech.prism.common.CodeSnippet;
import com.yukthitech.prism.context.IdeContext;
import com.yukthitech.prism.editor.FileEditor;
import com.yukthitech.prism.editor.FileParseMessage;
import com.yukthitech.prism.editor.IIdeCompletionProvider;
import com.yukthitech.prism.index.FileParseCollector;
import com.yukthitech.prism.model.Project;
import com.yukthitech.autox.prefix.PrefixExpressionFactory;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ObjectWrapper;

/**
 * Ide file manager for test-data files.
 * @author akiran
 */
@Service
public class TestDataIdeFileManager extends AbstractIdeFileManager
{
	private static Logger logger = LogManager.getLogger(TestDataIdeFileManager.class);
	
	private static final int RSA_XML_TOKEN_TYPE_ATTR_VAL = 28;
	
	private static final int RSA_XML_TOKEN_TYPE_ELEM_TEXT = 20;
	
	private static final int RSA_XML_TOKEN_TYPE_CDATA_TEXT = 33;
	
	private static Set<Integer> TEXT_TOKEN_TYPES = CommonUtils.toSet(RSA_XML_TOKEN_TYPE_ATTR_VAL, RSA_XML_TOKEN_TYPE_ELEM_TEXT, RSA_XML_TOKEN_TYPE_CDATA_TEXT);
	
	@Autowired
	private IdeNotificationPanel ideNotificationPanel;
	
	@Autowired
	private IdeContext ideContext;
	
	@PostConstruct
	private void init()
	{
		PrefixExpressionFactory.init(null, CommonUtils.toSet("com.yukthitech"));
	}
	
	@Override
	public boolean isExecutionSupported()
	{
		return true;
	}
	
	@Override
	public boolean isSuppored(Project project, File file)
	{
		if(!file.getName().toLowerCase().endsWith(".xml"))
		{
			return false;
		}

		if(project.isTestSuiteFolderFile(file))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public IIdeCompletionProvider getCompletionProvider(FileEditor fileEditor)
	{
		XmlCompletionProvider xmlCompletionProvider = new XmlCompletionProvider(fileEditor.getProject(), fileEditor, ideContext);
		return xmlCompletionProvider;
	}
	
	@Override
	public Object parseContent(Project project, File file, String content, FileParseCollector collector)
	{
		XmlFile xmlFile = null;
		
		try
		{
			xmlFile = XmlFile.parse(content, -1, collector);
		}catch(XmlParseException ex)
		{
			xmlFile = ex.getXmlFile();
			collector.addMessage(new FileParseMessage(MessageType.ERROR, ex.getMessage(), ex.getLineNumber(), ex.getOffset(), ex.getEndOffset()));
		}catch(Exception ex)
		{
			logger.debug("Failed to parse xml file: " + file.getName(), ex);
			collector.addMessage(new FileParseMessage(MessageType.ERROR, "Failed to parse xml file with error: " + ex, 1));
		}
		
		if(xmlFile != null && xmlFile.getRootElement() != null)
		{
			xmlFile.getRootElement().populateTestFileTypes(project, collector);
		}
		
		return xmlFile;
	}

	@Override
	public String getSyntaxEditingStyle(String extension)
	{
		return RSyntaxTextArea.SYNTAX_STYLE_XML;
	}
	
	@Override
	public String getToolTip(FileEditor fileEditor, Object parsedFile, int offset)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getActiveElement(FileEditor fileEditor, String nodeType)
	{
		//TODO: Content reparsing is required only when content is changed
		XmlFile xmlFile = getXmlFile(fileEditor.getProject(), fileEditor.getFile(), fileEditor.getContent());

		if(xmlFile == null)
		{
			return null;
		}
		
		int curLineNo = fileEditor.getCurrentLineNumber();
		Element curElement = xmlFile.getElement(nodeType, curLineNo);

		if(curElement == null)
		{
			return null;
		}

		Attribute attr = curElement.getAttribute("name");

		if(attr == null || StringUtils.isBlank(attr.getValue()))
		{
			return null;
		}

		return attr.getValue();
	}
	
	@Override
	public int getActiveElementLineNumber(FileEditor fileEditor, String nodeType)
	{
		XmlFile xmlFile = getXmlFile(fileEditor.getProject(), fileEditor.getFile(), fileEditor.getContent());

		if(xmlFile == null)
		{
			return -1;
		}
		
		xmlFile.getRootElement().populateTestFileTypes(fileEditor.getProject(), new FileParseCollector(fileEditor.getProject(), fileEditor.getFile()));
		
		int curLineNo = fileEditor.getCurrentLineNumber() + 1;
		Element curElement = xmlFile.getElement(nodeType, curLineNo);

		if(curElement == null)
		{
			return -1;
		}
		
		return curElement.getStartLocation().getStartLineNumber();
	}
	
	@Override
	public CodeSnippet getActiveElementText(FileEditor fileEditor, String nodeType)
	{
		String content = fileEditor.getContent();
		
		XmlFile xmlFile = getXmlFile(fileEditor.getProject(), fileEditor.getFile(), fileEditor.getContent());

		if(xmlFile == null)
		{
			return null;
		}
		
		//TODO: Content reparsing is required only when content is changed
		xmlFile.getRootElement().populateTestFileTypes(fileEditor.getProject(), new FileParseCollector(fileEditor.getProject(), fileEditor.getFile()));

		int curLineNo = fileEditor.getCurrentLineNumber() + 1;
		Element curElement = xmlFile.getElement(nodeType, curLineNo);

		if(curElement == null)
		{
			return null;
		}
		
		String elemText = content.substring(curElement.getStartLocation().getStartOffset(), curElement.getEndLocation().getEndOffset() + 1);
		return new CodeSnippet(elemText, fileEditor.getFile(), curElement.getStartLocation().getStartLineNumber());
	}

	private XmlFile getXmlFile(Project project, File file, String content)
	{
		if(!file.getName().toLowerCase().endsWith(".xml"))
		{
			return null;
		}

		try
		{
			XmlFile xmlFile = XmlFile.parse(content, -1, new FileParseCollector(project, file));
			return xmlFile;
		} catch(Exception ex)
		{
			logger.trace("Failed to parse xml file: " + file.getName() + " Error: " + ex);
			return null;
		}
	}
	
	@Override
	public boolean isStepInsertablePosition(FileEditor fileEditor)
	{
		XmlFileLocation loc = getXmlFileLocation(fileEditor);
		
		if(loc == null)
		{
			return false;
		}
		
		return (loc.getType() == XmlLocationType.CHILD_ELEMENT);
	}

	@Override
	public XmlFileLocation getXmlFileLocation(FileEditor fileEditor)
	{
		try
		{
			XmlFileLocation loc = XmlLoctionAnalyzer.getLocation(fileEditor.getContent(), fileEditor.getCaretPosition());
			return loc;
		}catch(Exception ex)
		{
			ideNotificationPanel.displayWarning("Failed to parse xml till current location. Error: " + ex.getMessage());
			return null;
		}
	}
	
	private Token subtokenizeText(Token token, ObjectWrapper<Token> tailToken)
	{
		try
		{
			TokenImpl tokenImpl = (TokenImpl) token;
			String content = token.getLexeme();
			
			RtaTokenBuilder tokenBuilder = new RtaTokenBuilder(tokenImpl);
			int tokenStart = token.getTextOffset();
			
			TestDataFileTokenizer.parse(content, tokenStart, xmlToken -> 
			{
				tokenBuilder.addSubtoken(xmlToken.startOffset, xmlToken.endOffset);
			});
			
			if(!tokenBuilder.hasSubtokens())
			{
				return token;
			}
			
			tokenBuilder.appendTailToken();
			return tokenBuilder.toToken(tailToken);
		}catch(RuntimeException ex)
		{
			throw ex;
		}
	}
	
	public static void printTokenTree(Token token)
	{
		String indent = "";
		
		while(token != null)
		{
			StringBuilder builder = new StringBuilder(indent);
			
			if(token.getLexeme() != null)
			{
				builder.append("[").append(token.getLexeme().replace("\n", "\\n").replace("\t", "\\t")).append("] ");
				builder.append(" St Offset: ").append(token.getOffset());
				builder.append(", End Offset: ").append(token.getEndOffset());
				builder.append(", Txt Offset: ").append(token.getTextOffset());
			}
			else
			{
				builder.append("[null]");
			}
				
			System.out.println(builder);
			indent += "  ";
			
			token = token.getNextToken();
		}
	}
	
	@Override
	public Token subtokenize(Token tokenLst)
	{
		Token head = tokenLst;
		Token curToken = tokenLst;
		Token prevToken = null;
		
		while(curToken != null)
		{
			//if null token is encountered, stop
			if(curToken.getType() == 0)
			{
				break;
			}
			
			if(TEXT_TOKEN_TYPES.contains(curToken.getType()))
			{
				ObjectWrapper<Token> tailToken = new ObjectWrapper<>();
				Token newToken = subtokenizeText(curToken, tailToken);
				
				//if no change in cur token
				if(newToken == curToken)
				{
					prevToken = curToken;
					curToken = curToken.getNextToken();
					continue;
				}
				
				//if first token is getting modified
				if(prevToken == null)
				{
					head = newToken;
				}
				//if mid token got modified
				else
				{
					((TokenImpl) prevToken).setNextToken(newToken);
				}
				
				//move to the end of newly created sub token list
				prevToken = tailToken.getValue();
				curToken = prevToken.getNextToken();
				continue;
			}
			
			prevToken = curToken;
			curToken = curToken.getNextToken();
		}
		
		return head;
	}
}
