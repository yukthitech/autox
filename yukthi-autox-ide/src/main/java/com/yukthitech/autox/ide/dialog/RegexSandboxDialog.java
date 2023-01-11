package com.yukthitech.autox.ide.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.state.PersistableState;
import com.yukthitech.swing.EscapableDialog;
import javax.swing.JCheckBox;

@PersistableState(directState = true, fields = true)
@Component
public class RegexSandboxDialog extends EscapableDialog
{
	private static final long serialVersionUID = 1L;
	
	private static final Pattern GRP_NAME_PATTERN = Pattern.compile("\\?\\<(\\w+)\\>");
	
	private final JPanel pnl1 = new JPanel();
	private final JLabel lblNewLabel = new JLabel("Regular Exp:");
	
	@PersistableState
	private final JTextField fldRegex = new JTextField();
	private final JLabel lblNewLabel_1 = new JLabel("Test Content:");
	private final JScrollPane scrollPane = new JScrollPane();
	
	@PersistableState
	private final JTextArea fldTestContent = new JTextArea();
	private final JPanel panel = new JPanel();
	private final JButton btnFind = new JButton("Find All");
	
	private final JButton btnMatch = new JButton("Check for Match");
	private final JLabel lblNewLabel_2 = new JLabel("Output:");
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JTextArea fldOutput = new JTextArea();
	private final JButton replaceBut = new JButton("Replace Matches");
	private final JLabel lblNewLabel_3 = new JLabel("Replace With:");
	private final JScrollPane scrollPane_2 = new JScrollPane();

	@PersistableState
	private final JTextArea replaceWithFld = new JTextArea();
	private final JPanel panel_1 = new JPanel();
	
	@PersistableState
	private final JCheckBox caseSensitiveCbox = new JCheckBox("Case Sensitive");
	
	@PersistableState
	private final JCheckBox multiLineCbox = new JCheckBox("Match Multi Line");

	/**
	 * Create the dialog.
	 */
	public RegexSandboxDialog()
	{
		setTitle("Regular Expression Sandbox");
		fldRegex.setFont(new Font("Tahoma", Font.BOLD, 12));
		fldRegex.setMargin(new Insets(5, 5, 5, 5));
		fldRegex.setColumns(10);
		setBounds(100, 100, 749, 448);
		getContentPane().setLayout(new BorderLayout());
		pnl1.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pnl1, BorderLayout.CENTER);
		GridBagLayout gbl_pnl1 = new GridBagLayout();
		gbl_pnl1.columnWidths = new int[]{0, 0, 0};
		gbl_pnl1.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pnl1.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_pnl1.rowWeights = new double[]{0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		pnl1.setLayout(gbl_pnl1);
		
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		pnl1.add(lblNewLabel, gbc_lblNewLabel);
		
		GridBagConstraints gbc_fldRegex = new GridBagConstraints();
		gbc_fldRegex.insets = new Insets(0, 0, 5, 0);
		gbc_fldRegex.fill = GridBagConstraints.HORIZONTAL;
		gbc_fldRegex.gridx = 1;
		gbc_fldRegex.gridy = 0;
		pnl1.add(fldRegex, gbc_fldRegex);
		
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.EAST;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.VERTICAL;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 1;
		pnl1.add(panel_1, gbc_panel_1);
		
		panel_1.add(caseSensitiveCbox);
		
		panel_1.add(multiLineCbox);
		
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		pnl1.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 0.5;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 2;
		pnl1.add(scrollPane, gbc_scrollPane);
		fldTestContent.setTabSize(3);
		fldTestContent.setRows(4);
		
		scrollPane.setViewportView(fldTestContent);
		
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		pnl1.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.weighty = 0.5;
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 1;
		gbc_scrollPane_2.gridy = 3;
		pnl1.add(scrollPane_2, gbc_scrollPane_2);
		replaceWithFld.setTabSize(3);
		replaceWithFld.setRows(2);
		
		scrollPane_2.setViewportView(replaceWithFld);
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.anchor = GridBagConstraints.EAST;
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 4;
		pnl1.add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		btnFind.setToolTipText("Finds all matches of regex in test content");
		
		btnFind.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				execute(true, false);
			}
		});
		
		panel.add(btnFind);
		btnMatch.setToolTipText("Checks if test content fully matches with regex");
		
		btnMatch.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				execute(false, false);
			}
		});
		
		panel.add(btnMatch);
		replaceBut.setToolTipText("Replaces regex in test-content with replace-with content.");
		
		panel.add(replaceBut);
		
		replaceBut.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				execute(false, true);
			}
		});
		
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 5;
		pnl1.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.weighty = 1;
		gbc_scrollPane_1.gridwidth = 2;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 6;
		pnl1.add(scrollPane_1, gbc_scrollPane_1);
		fldOutput.setBackground(UIManager.getColor("Button.light"));
		fldOutput.setEditable(false);
		
		scrollPane_1.setViewportView(fldOutput);
	}
	
	private void addOutput(String patternStr, Matcher matcher, StringBuilder output)
	{
		output.append("Match [Index: " + matcher.start() + "]");
		output.append("\n\tMatched Content: " + matcher.group(0));
		
		int count = matcher.groupCount();
		
		for(int i = 1; i <= count; i++)
		{
			output.append("\n\tGroup-" + i + ": " + matcher.group(i));
		}
		
		Matcher grpNameMatcher = GRP_NAME_PATTERN.matcher(patternStr);
		
		while(grpNameMatcher.find())
		{
			String grpName = grpNameMatcher.group(1);
			
			output.append("\n\tGroup [" + grpName + "]: " + matcher.group(grpName));
		}

		output.append("\n\n");
	}

	private void execute(boolean find, boolean replaceMatches)
	{
		fldOutput.setText("");
		
		String patternStr = fldRegex.getText();
		String content = fldTestContent.getText();
		
		if(StringUtils.isEmpty(patternStr))
		{
			JOptionPane.showMessageDialog(this, "Please provide regex and then try!");
			return;
		}
		
		if(StringUtils.isEmpty(content))
		{
			JOptionPane.showMessageDialog(this, "Please provide content and then try!");
			return;
		}
		
		int mod = 0;
		mod = caseSensitiveCbox.isSelected() ? mod : (mod | Pattern.CASE_INSENSITIVE);
		mod = multiLineCbox.isSelected() ? (mod | Pattern.DOTALL) : mod;

		Pattern pattern = null;
		
		try
		{
			pattern = Pattern.compile(patternStr, mod);
		}catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "Failed to parse regex. Error:\n" + ex);
			return;
		}
		
		Matcher matcher = pattern.matcher(content);
		StringBuilder output = new StringBuilder();

		if(find)
		{
			while(matcher.find())
			{
				addOutput(patternStr, matcher, output);
			}
			
			if(output.length() == 0)
			{
				output.append("Specified pattern is not found in specified content.");
			}
		}
		else if(replaceMatches)
		{
			StringBuffer buffer = new StringBuffer();
			String repStr = replaceWithFld.getText();
			
			try
			{
				while(matcher.find())
				{
					matcher.appendReplacement(buffer, repStr);
				}
				
				matcher.appendTail(buffer);
				output.append(buffer.toString());
			}catch(Exception ex)
			{
				output.append("An error occurred while replacing matches.\nError: " + ex);
			}
		}
		else
		{
			if(matcher.matches())
			{
				addOutput(patternStr, matcher, output);
			}
			else
			{
				output.append("Specified content does not match with specified regex");
			}
		}
		
		fldOutput.setText(output.toString());
	}
	
	public void display()
	{
		super.setVisible(true);
	}
}
