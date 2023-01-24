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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.rtextarea.GutterIconInfo;

import com.yukthitech.prism.exeenv.debug.IdeDebugPoint;

/**
 * Represents file editor icon on left side - error, warning, debug etc.
 * @author akranthikiran
 */
public class FileEditorIconGroup implements Icon
{
	public static enum IconType
	{
		ERROR,
		WARNING,
		DEBUG
	}
	
	public static class FileEditorIcon
	{
		/**
		 * Type of icon added.
		 */
		private IconType iconType;
		
		private Icon icon;
		
		private IdeDebugPoint debugPoint;
		
		private String message;

		public FileEditorIcon(IconType iconType, Icon icon, String message)
		{
			this.iconType = iconType;
			this.icon = icon;
			this.message = message;
		}

		public IdeDebugPoint getDebugPoint()
		{
			return debugPoint;
		}

		public void setDebugPoint(IdeDebugPoint debugPoint)
		{
			this.debugPoint = debugPoint;
		}

		public IconType getIconType()
		{
			return iconType;
		}

		public Icon getIcon()
		{
			return icon;
		}

		public String getMessage()
		{
			return message;
		}
	}
	
	/**
	 * Icon info obtained from syntax text area.
	 */
	private GutterIconInfo iconInfo;
	
	private Icon icon;
	
	private String message;
	
	private List<FileEditorIcon> editorIcons = new ArrayList<>();
	
	private FileEditorIcon debugIcon = null;
	
	public void addIcon(FileEditorIcon editorIcon)
	{
		//if new icon is debug icon, remove existing one, if present
		if(editorIcon.getIconType() == IconType.DEBUG)
		{
			if(debugIcon != null)
			{
				removeIcon(debugIcon);
			}
			
			debugIcon = editorIcon;
		}

		this.icon = mergeIcons(icon, editorIcon.icon);
		this.message = mergeMessages(message, editorIcon.message);
		
		editorIcons.add(editorIcon);
	}
	
	private String mergeMessages(String curMssg, String newMssg)
	{
		if(curMssg == null)
		{
			return newMssg;
		}
		
		return curMssg + "\n" + newMssg;
	}
	
	private Icon mergeIcons(Icon curIcon, Icon newIcon)
	{
		if(curIcon == null)
		{
			return newIcon;
		}
		
		if(curIcon == newIcon)
		{
			return curIcon;
		}
		
		int maxWid = curIcon.getIconWidth() > newIcon.getIconWidth() ? curIcon.getIconWidth() : newIcon.getIconWidth();
		int maxHei = curIcon.getIconHeight() > newIcon.getIconHeight() ? curIcon.getIconHeight() : newIcon.getIconHeight();
		
		BufferedImage mergedImg = new BufferedImage(maxWid, maxWid, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = mergedImg.getGraphics();
		
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, maxWid, maxHei);
		
		curIcon.paintIcon(null, g, 0, 0);
		newIcon.paintIcon(null, g, 0, 0);
		
		return new ImageIcon(mergedImg);
	}
	
	private void updateIconMssg()
	{
		this.icon = null;
		this.message = null;
		
		this.editorIcons.forEach(icon -> 
		{
			this.icon = mergeIcons(FileEditorIconGroup.this.icon, icon.icon);
			this.message = mergeMessages(FileEditorIconGroup.this.message, icon.message);
		});
	}
	
	void clearNonDebugIcons()
	{
		List<FileEditorIcon> iconsToRemove = new ArrayList<>();
		
		for(FileEditorIcon icon : this.editorIcons)
		{
			if(icon.getIconType() == IconType.DEBUG)
			{
				continue;
			}
			
			iconsToRemove.add(icon);
		}
		
		this.editorIcons.removeAll(iconsToRemove);
		
		//update icon and message
		updateIconMssg();
	}
	
	void clearDebugIcons()
	{
		List<FileEditorIcon> iconsToRemove = new ArrayList<>();
		
		for(FileEditorIcon icon : this.editorIcons)
		{
			if(icon.getIconType() != IconType.DEBUG)
			{
				continue;
			}
			
			iconsToRemove.add(icon);
		}
		
		this.editorIcons.removeAll(iconsToRemove);
		
		//update icon and message
		updateIconMssg();
	}

	boolean removeIcon(FileEditorIcon icon)
	{
		if(this.editorIcons.remove(icon))
		{
			updateIconMssg();
			return true;
		}
		
		return false;
	}
	
	FileEditorIcon getDebugIcon()
	{
		return debugIcon;
	}

	boolean isEmpty()
	{
		return this.editorIcons.isEmpty();
	}
	
	public String getMessage()
	{
		if(message == null)
		{
			return null;
		}
		
		return "<html><body>" + message.replace("\n", "<br/>") + "</body></html>";
	}
	
	public void setIconInfo(GutterIconInfo iconInfo)
	{
		this.iconInfo = iconInfo;
	}
	
	public GutterIconInfo getIconInfo()
	{
		return iconInfo;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		if(icon == null)
		{
			return;
		}
		
		icon.paintIcon(c, g, x, y);
	}

	@Override
	public int getIconWidth()
	{
		if(icon == null)
		{
			return 0;
		}
		
		return icon.getIconWidth();
	}

	@Override
	public int getIconHeight()
	{
		if(icon == null)
		{
			return 0;
		}
		
		return icon.getIconHeight();
	}
}
