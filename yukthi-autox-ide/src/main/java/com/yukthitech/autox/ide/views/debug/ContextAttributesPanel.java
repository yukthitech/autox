package com.yukthitech.autox.ide.views.debug;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.UiLayout;

@Component
public class ContextAttributesPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	@Autowired
	private UiLayout uiLayout;

	@Autowired
	private ActionCollection actionCollection;

	private ContextAttributeValueDlg ctxValDlg;

	private JScrollPane scrollPane;

	private JTable table;

	private ContextAttributeTableModel model;

	private JPopupMenu ctxAttributePopup;

	public ContextAttributesPanel()
	{
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane());
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
						ctxAttributePopup = uiLayout.getPopupMenu("contextAttributePopup").toPopupMenu(actionCollection);
						table.setComponentPopupMenu(ctxAttributePopup);
					}

				}
			});
			
			model = new ContextAttributeTableModel();
			table.setModel(model);
		}
		return table;
	}
}
