package com.yukthitech.autox.ide.views.debug;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.dialog.XpathSandboxDialog;
import com.yukthitech.swing.IconButton;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

@Component
public class ContextAttributesPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private static final String PARAM_PREFIX = "(param) ";

	private static ImageIcon XPATH_SANDBOX_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/xpath-sandbox.svg", 18);
	private static ImageIcon DEBUG_SANDBOX_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/debug-sandbox.svg", 18);

	@Autowired
	private XpathSandboxDialog xpathSandboxDialog;

	private ContextAttributeValueDlg ctxValDlg;

	private JScrollPane scrollPane;

	private JTable table;

	private ContextAttributeTableModel model;

	private final JPanel panel = new JPanel();
	private final IconButton xpathSandboxButton = new IconButton();
	private final IconButton debugSandboxButton = new IconButton();
	
	private DebugPanel debugPanel;
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem xpathMnuItem = new JMenuItem("xPath Sandbox");
	private final JMenuItem sboxMnuItem = new JMenuItem("Debug Sandbox");

	public ContextAttributesPanel()
	{
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane());
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(1);
		flowLayout.setHgap(1);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		
		add(panel, BorderLayout.NORTH);
		xpathSandboxButton.setToolTipText("xPath Sandbox");
		
		panel.add(xpathSandboxButton);
		xpathSandboxButton.setIcon(XPATH_SANDBOX_ICON);
		xpathSandboxButton.addActionListener(this::onXpathSandbox);
		xpathSandboxButton.setEnabled(false);
		
		debugSandboxButton.setToolTipText("Debug Sandbox");
		debugSandboxButton.setHorizontalAlignment(SwingConstants.RIGHT);
		debugSandboxButton.setEnabled(false);
		
		panel.add(debugSandboxButton);
		debugSandboxButton.setIcon(DEBUG_SANDBOX_ICON);
		debugSandboxButton.addActionListener(this::onDebugSandbox);

		popupMenu.add(xpathMnuItem);
		xpathMnuItem.addActionListener(this::onXpathSandbox);
		
		popupMenu.add(sboxMnuItem);
		sboxMnuItem.addActionListener(this::onDebugSandbox);
	}
	
	void setDebugPanel(DebugPanel debugPanel)
	{
		this.debugPanel = debugPanel;
	}
	
	public void setContextAttributes(Map<String, byte[]> attributes)
	{
		model.setContextAttributes(attributes);
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane(getTable());
		}
		return scrollPane;
	}

	private JTable getTable()
	{
		if(table == null)
		{
			table = new JTable();
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && table.getSelectedColumn() == 1)
					{
						if(ctxValDlg == null)
						{
							ctxValDlg = new ContextAttributeValueDlg(IdeUtils.getCurrentWindow());
						}
						
						ctxValDlg.display(
							model.getValueAt(table.getSelectedRow(), 0).toString(),
							model.getValueAt(table.getSelectedRow(), table.getSelectedColumn()).toString()
						);
					}
					else if(SwingUtilities.isRightMouseButton(e))
					{
						popupMenu.show(table, e.getX(), e.getY());
					}
				}
			});
			
			model = new ContextAttributeTableModel();
			table.setModel(model);
			
			table.getSelectionModel().addListSelectionListener(this::onSelectionChange);
		}
		
		return table;
	}
	
	private void onSelectionChange(ListSelectionEvent e)
	{
		boolean selected = (table.getSelectedRowCount() > 0);
		
		xpathSandboxButton.setEnabled(selected);
		debugSandboxButton.setEnabled(selected);
		
		xpathMnuItem.setEnabled(selected);
		sboxMnuItem.setEnabled(selected);
	}
	
	private void onXpathSandbox(ActionEvent e)
	{
		int selIdx = table.getSelectedRow();
		
		if(selIdx < 0)
		{
			return;
		}
		
		String row[] = model.getRow(selIdx);
		processKey(row[0], (prefix, name) -> xpathSandboxDialog.display(prefix, name, row[1]));
	}
	
	private void processKey(String attrName, BiConsumer<String, String> consumer)
	{
		if(attrName.startsWith(PARAM_PREFIX))
		{
			attrName = attrName.substring(PARAM_PREFIX.length());
			consumer.accept("param", attrName);
		}
		else
		{
			consumer.accept("attr", attrName);
		}

	}

	private void onDebugSandbox(ActionEvent e)
	{
		int selIdx = table.getSelectedRow();
		
		if(selIdx < 0)
		{
			return;
		}
		
		String row[] = model.getRow(selIdx);
		processKey(row[0], (prefix, name) -> debugPanel.openSandboxTab(prefix, name));
	}
}
