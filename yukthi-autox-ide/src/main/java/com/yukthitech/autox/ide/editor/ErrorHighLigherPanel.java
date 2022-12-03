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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.yukthitech.autox.ide.xmlfile.MessageType;

public class ErrorHighLigherPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Color WARNING_BG = new Color(252, 241, 202);
	public static Color WARNING_BORDER = new Color(0, 193, 0);
	
	private static Color ERROR_BG = new Color(249, 186, 218);
	private static Color ERROR_BORDER = new Color(253, 49, 152);
	
	private static Dimension HIGLIGHTER_SIZE = new Dimension(10, 5);
	
	private static Cursor HIGHLIGHTER_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	private class Highlighter extends JLabel
	{
		private static final long serialVersionUID = 1L;
		
		private FileParseMessage message;

		public Highlighter(FileParseMessage message)
		{
			super.setOpaque(true);
			super.setCursor(HIGHLIGHTER_CURSOR);
			
			this.message = message;
			
			if(message.getMessageType() == MessageType.ERROR)
			{
				super.setBackground(ERROR_BG);
				super.setBorder(new LineBorder(ERROR_BORDER, 1));
			}
			else
			{
				super.setBackground(WARNING_BG);
				super.setBorder(new LineBorder(WARNING_BORDER, 1));
			}
			
			super.setToolTipText(message.getMessage());
		}
		
		public Rectangle getBounds(double factor)
		{
			int y = (int) (message.getLineNo() * factor);
			Rectangle rect = new Rectangle(2, y, HIGLIGHTER_SIZE.width, HIGLIGHTER_SIZE.height);
			return rect;
		}
	}

	private FileEditor fileEditor;
	
	private MouseListener highlightListener = new MouseAdapter()
	{
		public void mousePressed(java.awt.event.MouseEvent e) 
		{
			Highlighter highlighter = (Highlighter) e.getSource();
			fileEditor.gotoLine(highlighter.message.getLineNo());
		}
	};
	
	public ErrorHighLigherPanel(FileEditor editor)
	{
		this.fileEditor = editor;
		super.setLayout(null);
	}
	
	public void addMessage(FileParseMessage mssg)
	{
		double lineCount = fileEditor.getLineCount();
		double verPosFactor = super.getHeight() / lineCount;

		Highlighter highlighter = new Highlighter(mssg);
		super.add(highlighter);
		highlighter.setBounds(highlighter.getBounds(verPosFactor));
		
		highlighter.addMouseListener(highlightListener);

		super.repaint();		
		super.revalidate();
	}
	
	public void clear()
	{
		super.removeAll();
		
		super.repaint();		
		super.revalidate();
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(14, 10);
	}
	
	@Override
	public void doLayout()
	{
		double lineCount = fileEditor.getLineCount();
		double verPosFactor = super.getHeight() / lineCount;
		
		for(Component comp : super.getComponents())
		{
			Highlighter highlighter = (Highlighter) comp;
			comp.setBounds(highlighter.getBounds(verPosFactor));
		}
	}
}
