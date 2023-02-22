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
package com.yukthitech.prism.views.debug;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.debug.common.ClientMssgEvalExpression;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.common.ServerMssgEvalExprResult;
import com.yukthitech.prism.IdeUtils;
import com.yukthitech.prism.exeenv.ExecutionEnvironment;
import com.yukthitech.prism.swing.HyperLinkEvent;
import com.yukthitech.prism.swing.YukthiHtmlPane;
import com.yukthitech.swing.IconButton;
import com.yukthitech.utils.CommonUtils;

public class DebugSandboxPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(DebugSandboxPanel.class);
	
	private static final String NEW_ROW_TEMPLATE = IdeUtils.loadResource("/templates/debug/sandbox-new-row.html");
	
	private static final String UPDATE_VAL_TEMPLATE = IdeUtils.loadResource("/templates/debug/sandbox-value-update.html");
	
	private static class ExprDetails
	{
		private String id;
		
		private String expression;
		
		private String value;

		public ExprDetails(String id, String expression)
		{
			this.id = id;
			this.expression = expression;
		}
	}
	
	private final JPanel panel = new JPanel();
	private final IconButton btnClear = new IconButton();
	private final JSplitPane splitPane = new JSplitPane();
	private final JScrollPane exprHistScrollPane = new JScrollPane();
	private final YukthiHtmlPane exprHistoryPane = new YukthiHtmlPane();
	
	private ContextAttributeValueDlg ctxValDlg;
	
	private final JPanel panel_1 = new JPanel();
	private final JPanel panel_2 = new JPanel();
	private final JButton btnExecute = new JButton("<html><body>\r\n<div style=\"text-align: center;\">\r\n   "
			+ "<span style=\"font-size: 10px;\">Execute</span><br/>\r\n   <span style=\"font-size: 7px; color: gray;\">(CTRL + ENTER)</span>\r\n</div>\r\n</body></html>");
	private final JScrollPane exprScrollPane = new JScrollPane();
	private final JTextArea exprFld = new JTextArea();
	
	private ExecutionEnvironment activeEnvironment;
	
	private String activeExecutionId;
	
	private Map<String, ExprDetails> expressionMap = new HashMap<>();
	
	private DebugPanel debugPanel;
	
	/**
	 * Create the panel.
	 */
	public DebugSandboxPanel()
	{
		setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(2);
		flowLayout.setHgap(2);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		add(panel, BorderLayout.NORTH);
		
		btnClear.setIcon(IdeUtils.loadIconWithoutBorder("/ui/icons/clear-console.svg", 16));
		panel.add(btnClear);
		btnClear.addActionListener(this::resetHistory);
		
		splitPane.setResizeWeight(0.75);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		add(splitPane, BorderLayout.CENTER);
		
		splitPane.setLeftComponent(exprHistScrollPane);
		
		exprHistScrollPane.setViewportView(exprHistoryPane);
		
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		panel_1.add(panel_2, BorderLayout.EAST);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] {105};
		gbl_panel_2.rowHeights = new int[] {35};
		gbl_panel_2.columnWeights = new double[]{0.0};
		gbl_panel_2.rowWeights = new double[]{0.0};
		panel_2.setLayout(gbl_panel_2);
		
		GridBagConstraints gbc_btnExecute = new GridBagConstraints();
		gbc_btnExecute.insets = new Insets(0, 3, 0, 3);
		gbc_btnExecute.gridx = 0;
		gbc_btnExecute.gridy = 0;
		panel_2.add(btnExecute, gbc_btnExecute);
		btnExecute.addActionListener(this::onExecute);
		
		panel_1.add(exprScrollPane, BorderLayout.CENTER);
		
		exprScrollPane.setViewportView(exprFld);
		exprFld.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					onExecute(null);
				}
			}
		});
		
		exprHistoryPane.addHyperLinkListener(this::onHyperLinkClick);

		exprFld.setEnabled(false);
		btnExecute.setEnabled(false);
		
		resetHistory(null);
	}
	
	void setDebugPanel(DebugPanel debugPanel)
	{
		this.debugPanel = debugPanel;
	}
	
	private void resetHistory(ActionEvent e)
	{
		exprHistoryPane.setText("<html><body id=\"body\"></body></html>");
	}

	public void setActiveEnvironment(ExecutionEnvironment activeEnvironment, String activThreadId)
	{
		this.activeEnvironment = activeEnvironment;
		this.activeExecutionId = activThreadId;
		
		boolean active = (activeEnvironment != null && activThreadId != null);
		exprFld.setEnabled(active);
		btnExecute.setEnabled(active);
	}
	
	private void onExecute(ActionEvent e)
	{
		String expression = exprFld.getText().trim();
		
		if(expression.length() == 0)
		{
			return;
		}
		
		exprFld.setText("");
		
		ClientMssgEvalExpression mssg = new ClientMssgEvalExpression(activeExecutionId, expression);
		ExprDetails expDetails = new ExprDetails(mssg.getRequestId(), expression);
		
		synchronized(expressionMap)
		{
			expressionMap.put(mssg.getRequestId(), expDetails);
			
			String neRow = FreeMarkerMethodManager.replaceExpressions("newRow", 
					CommonUtils.toMap(
						"expression", expression,
						"id", mssg.getRequestId()
					), 
					NEW_ROW_TEMPLATE);
			
			appendNewContent(neRow);
		}
		
		activeEnvironment.sendDataToServer(mssg, this::onExprEvaluation);
		debugPanel.activateTab();
	}
	
	private void onExprEvaluation(ServerMssgConfirmation confirmation)
	{
		ServerMssgEvalExprResult resMssg = (ServerMssgEvalExprResult) confirmation;
		Object value = null;
		
		try
		{
			value = SerializationUtils.deserialize(resMssg.getResult());
		}catch(Exception ex)
		{
			value = "<non-deserializable>";
		}
		
		ExprDetails expDetails = null;
		
		synchronized(expressionMap)
		{
			expDetails = expressionMap.get(resMssg.getRequestId());
			
			if(expDetails == null)
			{
				return;
			}
			
			if(value != null)
			{
				expDetails.value = AutomationUtils.toFormattedJson(value);
			}
		}
		
		String strVal = expDetails.value;
		
		if(strVal != null && strVal.length() > 100)
		{
			strVal = strVal.substring(0, 100) + "...";
		}
		
		String valueContent = FreeMarkerMethodManager.replaceExpressions("updateVal", 
				CommonUtils.toMap(
					"successful", resMssg.isSuccessful(),
					"message", resMssg.getError(),
					"successful", resMssg.isSuccessful(),
					"value", strVal,
					"id", resMssg.getRequestId()
				), 
				UPDATE_VAL_TEMPLATE);
		
		updateValue(expDetails.id, valueContent);
	}

	private void moveToEnd()
	{
		EventQueue.invokeLater(() -> 
		{
			// move scroll pane to the end
			JScrollBar vertical = exprHistScrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
			
			JScrollBar horizontal = exprHistScrollPane.getHorizontalScrollBar();
			horizontal.setValue(0);
		});
	}
	
	private void updateValue(String id, String content)
	{
		HTMLDocument htmlDoc = (HTMLDocument) exprHistoryPane.getDocument();
		Element element = htmlDoc.getElement("value_" + id);
		
		if(element == null)
		{
			return;
		}

		try
		{
			htmlDoc.setInnerHTML(element, content);
		}catch(Exception ex)
		{
			logger.error("An error occurred while updating value", ex);
		}
		
		debugPanel.activateTab();
	}

	private void appendNewContent(String content)
	{
		HTMLDocument htmlDoc = (HTMLDocument) exprHistoryPane.getDocument();
		Element element = htmlDoc.getElement("body");

		try
		{
			htmlDoc.insertBeforeEnd(element, content);

			moveToEnd();
		} catch(Exception ex)
		{
			logger.error("An error occurred while adding html content", ex);
		}
	}
	
	private synchronized void displayValue(String id)
	{
		if(ctxValDlg == null)
		{
			ctxValDlg = new ContextAttributeValueDlg(IdeUtils.getCurrentWindow());
		}
		
		ExprDetails exprDet = expressionMap.get(id);
		
		if(exprDet == null)
		{
			return;
		}
		
		ctxValDlg.display(
			exprDet.expression,
			exprDet.value
		);
	}

	private void onHyperLinkClick(HyperLinkEvent event)
	{
		String exprId = event.getHref();
		displayValue(exprId);
	}
	
	public void setExpression(String expression)
	{
		this.exprFld.setText(expression);
		this.exprFld.requestFocus();
	}
}
