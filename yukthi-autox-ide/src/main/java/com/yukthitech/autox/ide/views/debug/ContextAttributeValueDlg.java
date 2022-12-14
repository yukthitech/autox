package com.yukthitech.autox.ide.views.debug;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.yukthitech.autox.ide.IdeUtils;

public class ContextAttributeValueDlg extends JDialog
{
	private static final long serialVersionUID = 1L;
	private RSyntaxTextArea textArea;
	private final JPanel panel = new JPanel();
	private final JLabel lblName = new JLabel("Name: ");
	private final JTextField nameFld = new JTextField();

	public ContextAttributeValueDlg(Window window)
	{
		super(window, ModalityType.DOCUMENT_MODAL);
		nameFld.setFont(new Font("Tahoma", Font.BOLD, 14));
		nameFld.setEditable(false);
		nameFld.setBorder(null);
		nameFld.setColumns(10);
		
		super.setTitle("Context Attribute Value");
		
		super.addWindowFocusListener(new WindowAdapter()
		{
			@Override
			public void windowDeactivated(WindowEvent e)
			{
				setVisible(false);
			}
		});
		
		setBounds(100, 100, 450, 300);
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane);
			scrollPane.setViewportView(getTextArea());
		}
		
		KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = super.getRootPane();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		rootPane.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel.add(lblName, BorderLayout.WEST);
		
		panel.add(nameFld, BorderLayout.CENTER);
		rootPane.registerKeyboardAction(e -> dispose(), k, JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public void display(String name, String string)
	{
		nameFld.setText(name);
		textArea.setText(string);
		IdeUtils.centerOnScreen(this);
		super.setVisible(true);
	}

	private RSyntaxTextArea getTextArea()
	{
		if(textArea == null)
		{
			textArea = new RSyntaxTextArea();
			textArea.setEditable(false);
		}
		return textArea;
	}
}
