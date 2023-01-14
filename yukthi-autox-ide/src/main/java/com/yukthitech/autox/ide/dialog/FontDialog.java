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
package com.yukthitech.autox.ide.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.state.PersistableState;
import com.yukthitech.autox.ide.swing.IdeDialogPanel;
import com.yukthitech.autox.ide.swing.SearchableList;
import com.yukthitech.utils.CaseInsensitiveComparator;

@Service
@PersistableState
public class FontDialog extends IdeDialogPanel
{
	private static final long serialVersionUID = 1L;

	private static class FontCellRenderer extends JLabel implements ListCellRenderer<FontWrapper>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<? extends FontWrapper> list, FontWrapper value, int index, boolean isSelected, boolean cellHasFocus)
		{
			super.setFont(value.font);
			super.setText(value.font.getName());
			super.setOpaque(true);
			
			if(isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			return this;
		}
	}
	
	private static class FontWrapper
	{
		private Font font;

		public FontWrapper(Font font)
		{
			this.font = font;
		}
		
		public String getName()
		{
			return font.getName();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
			{
				return true;
			}

			if(!(obj instanceof FontDialog.FontWrapper))
			{
				return false;
			}

			FontDialog.FontWrapper other = (FontDialog.FontWrapper) obj;
			return font.getName().equals(other.font.getName());
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashcode()
		 */
		@Override
		public int hashCode()
		{
			return font.getName().hashCode();
		}
		
	}

	private final JPanel contentPanel = new JPanel();
	private final JLabel lblFont = new JLabel("Font: ");
	private final JLabel lblSize = new JLabel("Size:");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JList<FontWrapper> fldFontLst = new SearchableList<FontWrapper>(this::searchFont);
	private DefaultListModel<FontWrapper> fontListModel = new DefaultListModel<>();

	private final JPanel panel = new JPanel();
	private final JCheckBox chckbxBold = new JCheckBox("Bold");
	private final JCheckBox chckbxItalic = new JCheckBox("Italic");
	private final JTextField fldSize = new JTextField();
	private final JLabel lblSample = new JLabel("");

	private Font resFont = null;
	
	private TreeMap<String, FontWrapper> fontMap = new TreeMap<String, FontWrapper>(new CaseInsensitiveComparator());

	/**
	 * Create the dialog.
	 */
	public FontDialog()
	{
		super.setModalityType(ModalityType.APPLICATION_MODAL);

		super.setTitle("Font Dialog");
		super.setDialogBounds(100, 100, 450, 490);

		fldSize.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				fontChanged(false);
			}
		});
		
		fldSize.setColumns(10);
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		GridBagConstraints gbc_lblFont = new GridBagConstraints();
		gbc_lblFont.insets = new Insets(0, 0, 5, 5);
		gbc_lblFont.gridx = 0;
		gbc_lblFont.gridy = 0;
		contentPanel.add(lblFont, gbc_lblFont);

		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		contentPanel.add(scrollPane, gbc_scrollPane);
		fldFontLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fldFontLst.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				fontChanged(false);
			}
		});
		fldFontLst.setVisibleRowCount(10);

		scrollPane.setViewportView(fldFontLst);

		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPanel.add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		chckbxBold.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				fontChanged(false);
			}
		});

		panel.add(chckbxBold);
		chckbxItalic.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				fontChanged(false);
			}
		});

		panel.add(chckbxItalic);

		GridBagConstraints gbc_lblSize = new GridBagConstraints();
		gbc_lblSize.anchor = GridBagConstraints.EAST;
		gbc_lblSize.insets = new Insets(0, 0, 5, 5);
		gbc_lblSize.gridx = 0;
		gbc_lblSize.gridy = 2;
		contentPanel.add(lblSize, gbc_lblSize);

		GridBagConstraints gbc_fldSize = new GridBagConstraints();
		gbc_fldSize.insets = new Insets(0, 0, 5, 0);
		gbc_fldSize.fill = GridBagConstraints.HORIZONTAL;
		gbc_fldSize.gridx = 1;
		gbc_fldSize.gridy = 2;
		contentPanel.add(fldSize, gbc_fldSize);

		GridBagConstraints gbc_lblSample = new GridBagConstraints();
		gbc_lblSample.gridwidth = 2;
		gbc_lblSample.fill = GridBagConstraints.BOTH;
		gbc_lblSample.gridx = 0;
		gbc_lblSample.gridy = 3;
		lblSample.setHorizontalAlignment(SwingConstants.CENTER);
		lblSample.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Sample", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPanel.add(lblSample, gbc_lblSample);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						resFont = fontChanged(true);

						if(resFont == null)
						{
							return;
						}

						FontDialog.this.closeDialog();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						FontDialog.this.closeDialog();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		loadFonts();
		fldFontLst.setCellRenderer(new FontCellRenderer());
		fldFontLst.setModel(fontListModel);
	}

	private void loadFonts()
	{
		String fontNames[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		List<FontWrapper> fonts = new ArrayList<>();
		Font font = null;
		
		for(String name : fontNames)
		{
			font = new Font(name, Font.PLAIN, 16);
			FontWrapper wrapper = new FontWrapper(font);
			
			fontMap.put(name, wrapper);
			fonts.add(wrapper);
		}

		Collections.sort(fonts, new Comparator<FontWrapper>()
		{
			@Override
			public int compare(FontWrapper o1, FontWrapper o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		fonts.forEach(fontObj -> fontListModel.addElement(fontObj));
	}

	private Font fontChanged(boolean showErr)
	{
		FontWrapper font = fldFontLst.getSelectedValue();

		if(font == null)
		{
			if(showErr)
			{
				JOptionPane.showMessageDialog(this, "Please select a font anf then try!");
			}
			return null;
		}

		String fontName = font.getName();
		int flags = Font.PLAIN;

		if(chckbxBold.isSelected())
		{
			flags |= Font.BOLD;
		}

		if(chckbxItalic.isSelected())
		{
			flags |= Font.ITALIC;
		}

		int size = 0;

		try
		{
			size = Integer.parseInt(fldSize.getText());

			if(size <= 0)
			{
				if(showErr)
				{
					JOptionPane.showMessageDialog(this, "Please provide valid size and then try: " + size);
				}
				return null;
			}
		} catch(Exception ex)
		{
			if(showErr)
			{
				JOptionPane.showMessageDialog(this, "Please provide valid size and then try!");
			}
			return null;
		}

		Font newFont = new Font(fontName, flags, size);
		lblSample.setFont(newFont);
		lblSample.setText("AbcZ.. abcz... 0123");

		return newFont;
	}

	public Font display(Font font)
	{
		if(font == null)
		{
			//set default font used for RSyntaxTextarea
			font = IIdeConstants.DEFAULT_FONT;
		}
		
		FontWrapper cachedFont = fontMap.get(font.getName());
		
		if(cachedFont != null)
		{
			fldFontLst.setSelectedValue(cachedFont, true);
		}
		
		chckbxBold.setSelected(font.isBold());
		chckbxItalic.setSelected(font.isItalic());
		
		fldSize.setText("" + font.getSize());
		
		super.displayInDialog();

		return resFont;
	}
	
	private FontWrapper searchFont(String name)
	{
		Entry<String, FontWrapper> entry = fontMap.ceilingEntry(name);
		
		if(entry == null)
		{
			return null;
		}
		
		return entry.getValue();
	}
}
