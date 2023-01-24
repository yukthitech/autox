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
package com.yukthitech.prism.swing;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import com.yukthitech.utils.event.EventListenerManager;

/**
 * Pane to display html with hyperlink action handling.
 * 
 * @author akiran
 */
public class YukthiHtmlPane extends JTextPane
{
	private static final long serialVersionUID = 1L;

	private final class HyperlinkMouseListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getButton() != MouseEvent.BUTTON1)
			{
				return;
			}
			
			Element h = getHyperlinkElement(e);
			
			if(h == null)
			{
				return;
			}
			
			Object attribute = h.getAttributes().getAttribute(HTML.Tag.A);

			if(!(attribute instanceof AttributeSet))
			{
				return;
			}

			AttributeSet set = (AttributeSet) attribute;
			String href = (String) set.getAttribute(HTML.Attribute.HREF);
			
			if(href == null)
			{
				return;
			}

			//invoke href listeners
			listenerManager.get().onClick(new HyperLinkEvent(href));
		}

		private Element getHyperlinkElement(MouseEvent event)
		{
			JEditorPane editor = (JEditorPane) event.getSource();
			int pos = editor.getUI().viewToModel(editor, event.getPoint());
			
			if(pos >= 0 && editor.getDocument() instanceof HTMLDocument)
			{
				HTMLDocument hdoc = (HTMLDocument) editor.getDocument();
				Element elem = hdoc.getCharacterElement(pos);
			
				if(elem.getAttributes().getAttribute(HTML.Tag.A) != null)
				{
					return elem;
				}
			}
			
			return null;
		}
	}
	
	private EventListenerManager<IHperLinkListener> listenerManager = EventListenerManager.newEventListenerManager(IHperLinkListener.class, false);

	public YukthiHtmlPane()
	{
		super.setContentType("text/html");
		super.setEditable(false);
		super.addMouseListener(new HyperlinkMouseListener());
		
		super.setCursor(new Cursor(Cursor.TEXT_CURSOR));
	}

	public void addHyperLinkListener(IHperLinkListener listener)
	{
		listenerManager.addListener(listener);
	}
	
	public void removeHyperLinkListener(IHperLinkListener listener)
	{
		listenerManager.removeListener(listener);
	}
}
