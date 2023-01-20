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
package com.yukthitech.autox.ide.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.FreeMarkerMethodDocInfo;
import com.yukthitech.autox.doc.PluginInfo;
import com.yukthitech.autox.doc.PrefixExpressionDoc;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.UiLocatorDoc;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.IViewPanel;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.services.GlobalStateManager;
import com.yukthitech.autox.ide.services.ResourceCache;
import com.yukthitech.utils.exceptions.InvalidStateException;

@Component
public class HelpPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(HelpPanel.class);
	
	private static Pattern URL_PATTERN = Pattern.compile("\\[\\[URL\\]\\s*(.*?)\\s*\\]");
	
	@Autowired
	private GlobalStateManager globalStateManager;

	private JTabbedPane parentTabbedPane;

	private JSplitPane splitPane;
	private JScrollPane scrollPane;
	private JTree tree;
	private JScrollPane editorScrollPane;
	private JEditorPane editorPane;
	private JPanel panel;
	private JTextField searchField;

	private String documentTemplate = null;

	private String fmMethodDocTemplate = null;

	private String exprDocTemplate = null;

	private String currentSearchText = "";

	private final JLabel lblSearch = new JLabel("Search: ");

	private HelpNodeData rootNode;

	private Directory indexDirectory;

	private StandardAnalyzer indexAnalyzer = new StandardAnalyzer();

	private IndexSearcher indexSearcher;
	private final JLabel errLabel = new JLabel("");

	/**
	 * Create the panel.
	 */
	public HelpPanel()
	{
		setLayout(new BorderLayout(0, 0));
		add(getPanel(), BorderLayout.NORTH);
		add(getSplitPane());

		try
		{
			documentTemplate = IOUtils.toString(HelpPanel.class.getResourceAsStream("/help/templates/documentation.html"), Charset.defaultCharset());
			fmMethodDocTemplate = IOUtils.toString(HelpPanel.class.getResourceAsStream("/help/templates/fm-method-doc.html"), Charset.defaultCharset());
			exprDocTemplate = IOUtils.toString(HelpPanel.class.getResourceAsStream("/help/templates/expr-doc.html"), Charset.defaultCharset());
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occured while loading documentation template", ex);
		}
	}

	private void initIndex()
	{
		File folder = new File("./autox-work/help-index");

		try
		{
			if(folder.exists())
			{
				AutomationUtils.deleteFolder(folder);
			}

			FileUtils.forceMkdir(folder);
			indexDirectory = FSDirectory.open(folder.toPath());
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating index folder: " + folder.getPath(), ex);
		}
	}

	@PostConstruct
	private void init()
	{
		initIndex();

		try
		{
			DocInformation docInformation = globalStateManager.getDocInformation();
			
			Map<String, Object> context = new HashMap<>();

			rootNode = new HelpNodeData("root", "root", "", null);

			HelpNodeData stepRootNode = new HelpNodeData("steps", "Steps", "", null);
			rootNode.addHelpNode(stepRootNode);

			for(StepInfo step : docInformation.getSteps())
			{
				context.put("type", "step");
				context.put("node", step);
				
				String stepKey = "step:" + step.getName();
				stepRootNode.addHelpNode(new HelpNodeData(stepKey, step.getName(), buildDoc(stepKey, documentTemplate, context), step));
			}

			HelpNodeData validationNode = new HelpNodeData("validations", "Validations", "", null);
			rootNode.addHelpNode(validationNode);

			for(ValidationInfo step : docInformation.getValidations())
			{
				context.put("type", "step");
				context.put("node", step);
				
				String valKey = "validation:" + step.getName();
				validationNode.addHelpNode(new HelpNodeData(valKey, step.getName(), buildDoc(valKey, documentTemplate, context), step));
			}

			HelpNodeData methodNode = new HelpNodeData("methods", "Free Marker Methods", "", null);
			rootNode.addHelpNode(methodNode);

			for(FreeMarkerMethodDocInfo method : docInformation.getFreeMarkerMethods())
			{
				context.put("type", "method");
				context.put("node", method);
				
				String metKey = "method:" + method.getName();
				methodNode.addHelpNode(new HelpNodeData(metKey, method.getName(), buildDoc(metKey, fmMethodDocTemplate, context), method));
			}

			HelpNodeData expressionNode = new HelpNodeData("expressions", "Prefix Expressions", "", null);
			rootNode.addHelpNode(expressionNode);

			for(PrefixExpressionDoc prefixExpr : docInformation.getPrefixExpressions())
			{
				context.put("type", "expression");
				context.put("node", prefixExpr);
				
				String exprKey = "expression:" + prefixExpr.getName();
				expressionNode.addHelpNode(new HelpNodeData(exprKey, prefixExpr.getName(), buildDoc(exprKey, exprDocTemplate, context), prefixExpr));
			}

			HelpNodeData uiLocatorNode = new HelpNodeData("uiLocators", "UI Locators", "", null);
			rootNode.addHelpNode(uiLocatorNode);

			for(UiLocatorDoc method : docInformation.getUiLocators())
			{
				context.put("type", "locator");
				context.put("node", method);
				
				String uiLocKey = "uiloc:" + method.getName();
				uiLocatorNode.addHelpNode(new HelpNodeData(uiLocKey, method.getName(), buildDoc(uiLocKey, exprDocTemplate, context), method));
			}
			
			HelpNodeData pluginsNode = new HelpNodeData("plugins", "Plugins", "", null);
			rootNode.addHelpNode(pluginsNode);

			for(PluginInfo plugin : docInformation.getPlugins())
			{
				context.put("type", "plugin");
				context.put("node", plugin);
				
				String pluginKey = "plugin:" + plugin.getName();
				pluginsNode.addHelpNode(new HelpNodeData(pluginKey, plugin.getName(), buildDoc(pluginKey, documentTemplate, context), plugin));
			}

			loadStaticDocs(rootNode);

			// create and open index
			IndexWriterConfig config = new IndexWriterConfig(indexAnalyzer);
			IndexWriter writer = new IndexWriter(indexDirectory, config);
			rootNode.index(writer);
			writer.close();

			IndexReader indexReader = DirectoryReader.open(indexDirectory);
			indexSearcher = new IndexSearcher(indexReader);

			// create final tree
			HelpTreeModel model = new HelpTreeModel(rootNode);
			tree.setModel(model);
		} catch(Exception ex)
		{
			logger.error("An error occurred while initializing help panel", ex);
			throw new InvalidStateException("An error occurred while initializing help panel", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadHelpDocNodes(HelpNodeData rootNode, List<Map<String, Object>> docLst) throws Exception
	{
		StringBuffer buffer = new StringBuffer();
		
		for(Map<String, Object> docEntry : docLst)
		{
			String label = (String) docEntry.get("label");
			String file = (String) docEntry.get("file");
			String htmlContent = IOUtils.resourceToString(file, Charset.defaultCharset());
			htmlContent = htmlContent.replace("\t", "  ");
			
			logger.debug("Loading static-doc with [Label: {}, File: {}]", label, file);
			
			//replace image urls
			buffer.setLength(0);
			Matcher matcher = URL_PATTERN.matcher(htmlContent);
			
			while(matcher.find())
			{
				String imgPath = matcher.group(1);
				logger.debug("For static-help '{}' computing classpath url for resource: {}", label, imgPath);
				
				String url = HelpPanel.class.getResource(imgPath).toString();
				matcher.appendReplacement(buffer, url);
			}
			
			matcher.appendTail(buffer);
			
			//create and add tree node
			HelpNodeData docNode = new HelpNodeData("doc:" + label, label, buffer.toString(), null);
			rootNode.addHelpNode(docNode);
			
			List<Map<String, Object>> subdocLst = (List<Map<String, Object>>) docEntry.get("subnodes");
			
			if(CollectionUtils.isNotEmpty(subdocLst))
			{
				loadHelpDocNodes(docNode, subdocLst);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadStaticDocs(HelpNodeData rootNode)
	{
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			String docLstJson = IOUtils.resourceToString("/help/help-doc-list.json", Charset.defaultCharset());
			
			List<Map<String, Object>> docLst = (List<Map<String, Object>>) objectMapper.readValue(docLstJson, Object.class);
			loadHelpDocNodes(rootNode, docLst);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading static docs", ex);
		}
	}

	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
		this.parentTabbedPane = parentTabPane;
	}

	private JSplitPane getSplitPane()
	{
		if(splitPane == null)
		{
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getScrollPane());
			splitPane.setRightComponent(getScrollPane_1());
		}
		return splitPane;
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTree());
		}
		return scrollPane;
	}

	private JTree getTree()
	{
		if(tree == null)
		{
			tree = new JTree();
			tree.setRootVisible(false);
			tree.setShowsRootHandles(true);

			tree.addTreeSelectionListener(new TreeSelectionListener()
			{
				@Override
				public void valueChanged(TreeSelectionEvent e)
				{
					displayNodeContent();
				}
			});
			tree.setCellRenderer(new DefaultTreeCellRenderer()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
				{
					JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					if(!searchField.getText().isEmpty() && label.getText().contains(searchField.getText()))

					{
						label.setForeground(Color.GRAY);

						return label;
					}
					return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				}
			});
		}
		return tree;
	}

	private JScrollPane getScrollPane_1()
	{
		if(editorScrollPane == null)
		{
			editorScrollPane = new JScrollPane();
			editorScrollPane.setViewportView(getEditorPane());
		}
		return editorScrollPane;
	}

	private JEditorPane getEditorPane()
	{
		if(editorPane == null)
		{
			editorPane = new JEditorPane("text/html", "");
			editorPane.setEditable(false);
		}
		return editorPane;
	}

	private JPanel getPanel()
	{
		if(panel == null)
		{
			panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 2, 5));
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0 };
			gbl_panel.rowHeights = new int[] { 23, 0, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0 };
			gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);

			GridBagConstraints gbc_lblSearch = new GridBagConstraints();
			gbc_lblSearch.insets = new Insets(0, 0, 0, 5);
			gbc_lblSearch.anchor = GridBagConstraints.EAST;
			gbc_lblSearch.gridx = 0;
			gbc_lblSearch.gridy = 0;
			lblSearch.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.add(lblSearch, gbc_lblSearch);
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 5, 0);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = 1;
			gbc_textField.gridy = 0;
			panel.add(getTextField(), gbc_textField);
			
			GridBagConstraints gbc_errLabel = new GridBagConstraints();
			gbc_errLabel.anchor = GridBagConstraints.EAST;
			gbc_errLabel.gridx = 1;
			gbc_errLabel.gridy = 1;
			errLabel.setBorder(new EmptyBorder(0, 0, 0, 5));
			errLabel.setForeground(Color.RED);
			errLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 10));
			panel.add(errLabel, gbc_errLabel);
		}
		return panel;
	}

	private JTextField getTextField()
	{
		if(searchField == null)
		{
			searchField = new JTextField();
			searchField.addKeyListener(new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent e)
				{
					if(e.getKeyCode() == KeyEvent.VK_UP)
					{
						tree.requestFocus();
						transferFocus();
					}
					if(e.getKeyCode() == KeyEvent.VK_DOWN)
					{
						tree.requestFocus();
						transferFocus();
					}
				}
			});
			searchField.setColumns(10);

			final Runnable applyFilterRunnable = new Runnable()
			{
				@Override
				public void run()
				{
					applyFilter();
				}
			};

			searchField.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void removeUpdate(DocumentEvent e)
				{
					IdeUtils.executeConsolidatedJob("applyFilter", applyFilterRunnable, 1000);
				}

				@Override
				public void insertUpdate(DocumentEvent e)
				{
					IdeUtils.executeConsolidatedJob("applyFilter", applyFilterRunnable, 1000);
				}

				@Override
				public void changedUpdate(DocumentEvent e)
				{
					IdeUtils.executeConsolidatedJob("applyFilter", applyFilterRunnable, 1000);
				}
			});
		}

		return searchField;
	}

	private Set<String> performSearch(String query, Set<String> filteredIds)
	{
		if(StringUtils.isBlank(query))
		{
			return filteredIds;
		}

		String finalQuery = transformQuery(query);

		try
		{
			Query q = null;
			
			try
			{
				q = new QueryParser("doc", indexAnalyzer).parse(finalQuery);
			}catch(Exception ex)
			{
				errLabel.setText("Invalid lucene query specified: " + query);
				return null;
			}

			TopScoreDocCollector collector = TopScoreDocCollector.create(1000);
			indexSearcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			Set<String> filteredDocIds = new LinkedHashSet<>();

			if(hits != null)
			{
				for(ScoreDoc doc : hits)
				{
					Document filteredDoc = indexSearcher.doc(doc.doc);
					filteredDocIds.add(filteredDoc.get("id"));
				}
			}

			if(filteredIds == null)
			{
				return filteredDocIds;
			}

			filteredIds.retainAll(filteredDocIds);
			return filteredIds;
		} catch(Exception ex)
		{
			logger.error("An error occurred while performing search operation with string: {}", query, ex);
			JOptionPane.showMessageDialog(this, "An error occurred while performing search operation. Search string: " + query + "\nError: " + ex);
			return null;
		}
	}
	
	private String transformQuery(String query)
	{
		// if single word is provided divide them into subquery using camel case
		// syntax
		String queryWords[] = query.split("\\s+");
		
		if(queryWords.length > 1)
		{
			return query;
		}
		
		String nonHyphenString = query.replaceAll("-(\\w)", "$1");
		
		if(nonHyphenString.toLowerCase().equals(query.toLowerCase()))
		{
			nonHyphenString = null;
		}

		String subwordStr = queryWords[0].replaceAll("([A-Z])", " $1");
		subwordStr = subwordStr.replace("-", " ");
		
		//if there are no sub words
		if(query.equals(subwordStr.trim()))
		{
			return query;
		}

		String finalQuery = query + "^2 " + subwordStr;
		
		if(nonHyphenString != null)
		{
			finalQuery += " " + nonHyphenString + "^3";
		}
		
		logger.debug("Using effective final query as: {}", subwordStr);
		return finalQuery;
	}

	private void applyFilter()
	{
		String newText = searchField.getText().trim();

		if(newText.equals(currentSearchText))
		{
			return;
		}
		
		errLabel.setText("");

		//do the lucene search
		Set<String> filteredDocIds = null;
		
		if(StringUtils.isNotBlank(newText))
		{
			String searchQueries[] = newText.split("\\|");
			
			for(String query : searchQueries)
			{
				if(StringUtils.isBlank(query))
				{
					errLabel.setText("Empty filter query specified.");
					return;
				}
				
				filteredDocIds = performSearch(query, filteredDocIds);
				
				//when error occurs
				if(filteredDocIds == null)
				{
					return;
				}
				
				//if empty results are encountered
				if(filteredDocIds.isEmpty())
				{
					break;
				}
			}

			if(filteredDocIds == null)
			{
				filteredDocIds = Collections.emptySet();
			}
		}
		
		//filter the tree in the ui
		currentSearchText = newText;
		rootNode.filter(filteredDocIds);
		
		HelpTreeModel model = new HelpTreeModel(rootNode);
		tree.setModel(model);

		if(!filteredDocIds.isEmpty())
		{
			HelpTreeNode selectedNode = model.getNode(filteredDocIds.iterator().next());
			selectNode(selectedNode);
		}
		else
		{
			selectNode((HelpTreeNode) model.getRoot());
		}
	}

	private void selectNode(HelpTreeNode select)
	{
		if(select.getChildCount() > 0)
		{
			select = (HelpTreeNode) select.getFirstChild();
			selectNode(select);
		}
		else
		{
			TreeNode[] treeNodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot((TreeNode) select);
			TreePath path = new TreePath(treeNodes);
			tree.scrollPathToVisible(path);
			tree.setSelectionPath(path);
		}
	}

	private String buildDoc(String name, String template, Map<String, Object> context)
	{
		return ResourceCache.getInstance().getFromCache(() -> 
		{
			return IIdeConstants.FREE_MARKER_ENGINE.processTemplate("documentation.ftl", template, context);
		}, "helpPanel." + name);
		
	}

	private void displayNodeContent()
	{
		Object treeNode = tree.getLastSelectedPathComponent();

		if(!(treeNode instanceof HelpTreeNode))
		{
			return;
		}

		HelpTreeNode node = (HelpTreeNode) tree.getLastSelectedPathComponent();

		if(node == null)
		{
			return;
		}

		HelpNodeData nodeValue = node.getHelpNodeData();
		editorPane.setText(nodeValue.getDocumentation());

		IdeUtils.executeUiTask(() -> {
			editorScrollPane.getVerticalScrollBar().setValue(0);
		});
	}

	public void activatePanel(String defaultSearchString)
	{
		if(defaultSearchString != null)
		{
			searchField.setText(defaultSearchString.trim());
			applyFilter();
		}

		parentTabbedPane.setSelectedComponent(this);
		searchField.requestFocus();

		if(searchField.getText().length() > 0)
		{
			searchField.select(0, searchField.getText().length());
		}
	}
}
