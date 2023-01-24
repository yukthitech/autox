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
import java.awt.Graphics;
import java.awt.Rectangle;

import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;

public class RectangleHighlighter extends SquiggleUnderlineHighlightPainter
{
	private static final long serialVersionUID = 1L;

	private Color fillColor;
	
	public RectangleHighlighter(Color color)
	{
		super(color);
		this.fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
	}
	
	@Override
	protected void paintSquiggle(Graphics g, Rectangle r)
	{
		g.setColor(fillColor);
		g.fillRect(r.x, r.y, r.width, r.height);
		
		super.paintSquiggle(g, r);
	}
}
