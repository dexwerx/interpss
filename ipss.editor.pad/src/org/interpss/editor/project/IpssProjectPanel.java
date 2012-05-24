package org.interpss.editor.project;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.interpss.editor.coreframework.GPBarFactory;
import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.doc.IpssDocument;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.doc.IpssProjectItem;
import org.interpss.editor.doc.IpssProjectItemCollector;
import org.interpss.editor.util.Utilities;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.spring.EditorSpringFactory;
import org.interpss.spring.EditorPluginSpringFactory;

import com.interpss.common.util.IpssLogger;

public class IpssProjectPanel extends JPanel {

	/**
	 * A reference to the top level component
	 */
	protected GPGraphpad graphpad;

	private IconNode rootNode = null;

	private JTree tree = null;

	private DefaultTreeModel model;

	private JPopupMenu popupProject;

	private JPopupMenu popupRoot;

	private JPopupMenu popupItem;

	private ArrayList<IpssProjectItem> init_OpenItems = new ArrayList<IpssProjectItem>();

	private IpssProjectItem init_ActiveItem;

	/**
	 * Constructor for StatusBar.
	 * 
	 */
	public IpssProjectPanel(GPGraphpad gp) {
		super();
		this.graphpad = gp;
		rootNode = new IconNode(new IpssProjectRootItem());
		initComponents();

		initListeners();
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}

	public JTree getTree() {
		return tree;
	}

	private Hashtable makeIcons() {

		Hashtable icons = new Hashtable();
		icons.put("ipss", IpssIconFactory.ICON_GRAPH);
		icons.put("xml", IpssIconFactory.ICON_XML);
		icons.put("txt", IpssIconFactory.ICON_TEXT);
		icons.put("ipssrpt", IpssIconFactory.ICON_REPORT);

		List adapterList = EditorPluginSpringFactory.getCustomFileAdapterList();
		for (int i = 0; i < adapterList.size(); i++) {
			IpssFileAdapter adapter = (IpssFileAdapter) adapterList.get(i);
			icons.put(adapter.getExtension(), IpssIconFactory.ICON_CUS);
		}

		return icons;
	}

	private void initComponents() {

		setLayout(new BorderLayout());

		// rootItem.setIcon(MetalIconFactory.getFileChooserHomeFolderIcon());
		rootNode.setIcon(IpssIconFactory.ICON_ROOT);

		model = new DefaultTreeModel(rootNode);

		setTree(new JTree(model) {
			public String getToolTipText(MouseEvent evt) {
				if (getRowForLocation(evt.getX(), evt.getY()) == -1)
					return null;
				TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
				Object userObject = ((IconNode) curPath.getLastPathComponent())
						.getUserObject();
				if (userObject instanceof IpssProject) {
					return ((IpssProject) userObject).getProjectPath();
				} else if (userObject instanceof IpssProjectItem) {
					return ((IpssProjectItem) userObject).getName();
				} else {
					return userObject.toString();
				}
				// return null;
			}

		});
		getTree().putClientProperty("JTree.icons", makeIcons());
		getTree().setCellRenderer(new IconNodeRenderer());

		getTree().setToolTipText("");
		JScrollPane sp = new JScrollPane(getTree());
		add(sp, BorderLayout.CENTER);

		popupProject = GPBarFactory.getInstance().createProjectPopupMenu(
				graphpad);

		popupRoot = GPBarFactory.getInstance().createRootPopupMenu(graphpad);

		popupItem = GPBarFactory.getInstance().createProjectItemPopupMenu(
				graphpad);

		showExistTree();

		// tree.setBackground(Color.red);
	}

	private void initListeners() {
		getTree().addMouseListener(new PopupTrigger());

		getTree().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				graphpad.update();
			}
		});

	}

	// public Action getCommand(String key) {
	// return GPPluginInvoker.getCommand(key, getActionMap(), this);
	// }
	//    
	// public void initCommand(Action action) {
	// graphpad.initCommand(action);
	// }

	public void addNewAllProject(IpssProject project) {

		IconNode projectNode = new IconNode(project);
		projectNode.setIcon(IpssIconFactory.ICON_PROJECT);

		model.insertNodeInto(projectNode, rootNode, rootNode.getChildCount());

		addNewAllItems(project, projectNode);
	}

	public void addNewAllItems(IpssProjectItemCollector itemCollector,
			IconNode parentNode) {
		// add items
		IpssProjectItem[] items = itemCollector.getAllProjectItems();

		if (items != null)
			for (int j = 0; j < items.length; j++) {
				IconNode node = new IconNode(items[j]);
				model.insertNodeInto(node, parentNode, parentNode
						.getChildCount());
				if (items[j].getItemCount() > 0) {
					addNewAllItems(items[j], node);
				}

				// if (items[j].isOpen() && !items[j].isLoaded())
				// graphpad.OpenProjectItem(items[j]);
				if (items[j].isOpen() && !items[j].isLoaded()) {
					init_OpenItems.add(items[j]);
					if (items[j].isActive()) {
						init_ActiveItem = items[j];
					}
				}

				items[j].setInit_Status(IpssProjectItem.STATUS_CLOSE);
			}
	}

	public void showOpenItems() {

		if (init_OpenItems.size() > 0) {
			for (int j = 0; j < init_OpenItems.size(); j++) {
				// expendTree2Object(init_OpenItems.get(j));
				// graphpad.getCommand("FileProjectOpenItem")
				// .actionPerformed(null);
				try {
					graphpad.OpenProjectItem(init_OpenItems.get(j));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// graphpad.OpenDocument((IpssEditorDocument) init_OpenItems
				// .get(j).getDocument());

			}

			// for (int j = 0; j < init_OpenItems.size(); j++) {
			// graphpad.OpenDocument((IpssEditorDocument) init_OpenItems
			// .get(j).getDocument());
			// }
		}
	}

	public void showActiveItem() {
		if (init_ActiveItem != null)
			graphpad.OpenDocument((IpssEditorDocument) init_ActiveItem
					.getDocument());
	}

	public void showOpenItems(int j) {
		graphpad.OpenProjectItem(init_OpenItems.get(j));
	}

	// add return by Mike
	public IpssProjectItem addNewProjectItem(
			IpssProjectItemCollector itemCollector, IpssDocument doc) {

		// DefaultTreeModel dtm = (DefaultTreeModel) getTree().getModel();
		DefaultMutableTreeNode projectNode = getTreeNode(model, rootNode,
				itemCollector);

		IpssProjectItem[] items = itemCollector.getAllProjectItems();

		for (int j = 0; j < items.length; j++) {
			if ((items[j].isLoaded()) && (items[j].getDocument().equals(doc))) {
				model.insertNodeInto(new IconNode(items[j]), projectNode,
						projectNode.getChildCount());
				return items[j];
			}
		}
		return null;
	}

	public void removeProject(IpssProject project) {

		removeNodebyObject(project);

		EditorSpringFactory.getAppContext().removeProject(project);

	}

	public void removeProjectItem(IpssProjectItem projectitem) {

		DefaultMutableTreeNode selectedNode = getTreeNode(model, rootNode,
				projectitem);
		Object puserObject = ((DefaultMutableTreeNode) selectedNode.getParent())
				.getUserObject();
		((IpssProjectItemCollector) puserObject).removeProjectItem(projectitem);

		removeNodebyObject(projectitem);

	}

	public void removeNodebyObject(Object o) {
		DefaultMutableTreeNode selectedNode = getTreeNode(model, rootNode, o);

		if (selectedNode == null)
			return;

		if (selectedNode.getParent() != null)
			model.removeNodeFromParent(selectedNode);
	}

	public void showExistTree() {
		String wsDir = EditorSpringFactory.getAppContext().getWorkspaceDir();
		IpssLogger.getLogger().info("WS dir for building project tree, " + wsDir);

		File dir = new File(wsDir);

		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		File[] projectDirs = dir.listFiles(fileFilter);

		if (projectDirs == null) {
			// Either dir does not exist or is not a directory
			return;
		}

		init_OpenItems.clear();

		for (int i = 0; i < projectDirs.length; i++) {
			// Get filename of file or directory
			String projectname = projectDirs[i].getName();

			IpssProject project = new IpssProject(projectname, null);

			try {
				String fileName = project.getProjectFilePathName();
				IpssProjectCodec.getInstance(graphpad).read(
						new FileInputStream(fileName), project);

				addNewAllProject(project);
				EditorSpringFactory.getAppContext().addProject(project);
			} catch (Exception ex) {
				EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(
						"InterPSS Project '" + projectname + "' Open Error",
						ex.toString());
				ex.printStackTrace();
			}

			if (i == 0)
				expendTree2Object(project);

		}

		// String[] children = dir.list();
		// if (children == null) {
		// // Either dir does not exist or is not a directory
		// } else {
		// for (int i=0; i<children.length; i++) {
		// // Get filename of file or directory
		// String filename = children[i];
		// }
		// }
		//    
		// // It is also possible to filter the list of returned files.
		// // This example does not return any files that start with `.'.
		// FilenameFilter filter = new FilenameFilter() {
		// public boolean accept(File dir, String name) {
		// return !name.startsWith(".");
		// }
		// };
		// children = dir.list(filter);
		//    
		//    
		// // The list of files can also be retrieved as File objects
		// File[] files = dir.listFiles();
		//    
		// // This filter only returns directories
		// FileFilter fileFilter = new FileFilter() {
		// public boolean accept(File file) {
		// return file.isDirectory();
		// }
		// };
		// files = dir.listFiles(fileFilter);

	}

	public void expendTree2Object(final Object findObject) {
		TreePath findTreepath = getTreePath(findObject);
		getTree().setSelectionPath(findTreepath);
		getTree().scrollPathToVisible(findTreepath);
	}

	public IpssProjectItem findProjectItem(IpssDocument doc) {
		DefaultTreeModel dtm = (DefaultTreeModel) getTree().getModel();
		DefaultMutableTreeNode findedTreeNode = getTreeNode(dtm, rootNode, doc);
		// modified by Mike, findedTreeNode may be null
		if (findedTreeNode != null)
			return (IpssProjectItem) (findedTreeNode.getUserObject());
		else
			return null;
	}

	// public DefaultMutableTreeNode getTreeNode(final Object findObject){
	// DefaultTreeModel dtm = (DefaultTreeModel) getTree().getModel();
	// return getTreeNode(dtm,rootNode,findObject);
	// }

	public DefaultMutableTreeNode getTreeNode(final DefaultTreeModel dtm,
			final DefaultMutableTreeNode root, final Object findObject) {
		if (findObject instanceof IconNode)
			return (IconNode) findObject;
		else if (findObject instanceof IpssProjectRootItem)
			return rootNode;

		DefaultMutableTreeNode tn = null;
		int rodeCounts = root.getChildCount();
		for (int i = 0; i < rodeCounts; i++) {
			DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) dtm
					.getChild(root, i);

			if (((findObject instanceof IpssProject) || (findObject instanceof IpssProjectItem))
					&& (tmpNode.getUserObject().equals(findObject))) {
				tn = tmpNode;
				break;
			} else if ((findObject instanceof IpssDocument)
					&& (tmpNode.getUserObject() instanceof IpssProjectItem)
					&& ((((IpssProjectItem) tmpNode.getUserObject()).isLoaded()) && (((IpssProjectItem) tmpNode
							.getUserObject()).getDocument().equals(findObject)))) {
				tn = tmpNode;
				break;
			}

			if (tn != null)
				return tn;
			else if (!(findObject instanceof IpssProject)) {
				if (tmpNode.getChildCount() > 0) {
					tn = getTreeNode(dtm, tmpNode, findObject);
					if (tn != null)
						return tn;
				}
			}
		}
		return tn;
	}

	public TreePath getTreePath(final Object findObject) {
		DefaultTreeModel dtm = (DefaultTreeModel) getTree().getModel();
		// DefaultMutableTreeNode root = (DefaultMutableTreeNode)
		// (tree.getModel().getRoot());
		DefaultMutableTreeNode findedTreeNode = getTreeNode(dtm, rootNode,
				findObject);

		if (findedTreeNode == null)
			return null;
		Vector v = new Vector();
		do {
			v.add(findedTreeNode);
			findedTreeNode = (DefaultMutableTreeNode) findedTreeNode
					.getParent();
		} while (findedTreeNode.getParent() != null);
		v.add(findedTreeNode);
		int depth = v.size();
		TreePath tp2 = new TreePath((DefaultMutableTreeNode) (v
				.elementAt(depth - 1)));
		for (int j = depth - 2; j >= 0; j--) {
			tp2 = tp2.pathByAddingChild((DefaultMutableTreeNode) (v
					.elementAt(j)));
		}
		return tp2;
	}

	public boolean isProjectSelected() {
		IconNode selectedNode = (IconNode) getTree()
				.getLastSelectedPathComponent();

		if (selectedNode == null)
			return false;

		Object userObject = selectedNode.getUserObject();

		if (userObject == null)
			return false;

		if (userObject instanceof IpssProject)
			return true;

		return false;
	}

	public boolean isTextAddable() {

		IconNode selectedNode = (IconNode) getTree()
				.getLastSelectedPathComponent();

		if (selectedNode == null)
			return false;

		Object userObject = selectedNode.getUserObject();

		if (userObject == null)
			return false;

		if (userObject instanceof IpssProject)
			return true;
		else if (userObject instanceof IpssProjectItem) {
			IpssProjectItem item = (IpssProjectItem) userObject;
			return (item.getFileExt().equals("ipss"))
					|| (Utilities.haveExt(EditorPluginSpringFactory
							.getCustomFileAdapterList(), item.getFileExt()));
		}
		return false;
	}

	public boolean isReportAddable() {
		IconNode selectedNode = (IconNode) getTree()
				.getLastSelectedPathComponent();

		if (selectedNode == null)
			return false;

		Object userObject = selectedNode.getUserObject();

		if (userObject == null)
			return false;

		if (userObject instanceof IpssProject)
			return false;
		else if (userObject instanceof IpssProjectItem) {
			IpssProjectItem item = (IpssProjectItem) userObject;
			return (item.getFileExt().equals("ipss"))
					|| (Utilities.haveExt(EditorPluginSpringFactory
							.getCustomFileAdapterList(), item.getFileExt()));
		}
		return false;

	}

	// public boolean isAddable() {
	// IpssProjectItem item = getCurrentProjectItem();
	// if (item == null) return false;
	// return (item.getFileExt().equals("ipss"))
	// || (Utilities.haveExt(SimuAppSpringAppContext
	// .getCustomFileAdapterList(), item.getFileExt()));
	// }
	//
	// public boolean isTextable() {
	// return isAddable() || (getCurrentProjectItem().get)
	// }

	class PopupTrigger extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if ((e.getClickCount() == 2)
					|| ((e.getClickCount() == 1)
							&& (graphpad.getCurrentProjectItem() != null) && (graphpad
							.getCurrentProjectItem().getDocument() != null))) {
				graphpad.getCommand("FileProjectOpenItem")
						.actionPerformed(null);
			}

			// super.mouseClicked(e);
		}

		// @Override
		// public void mousePressed(MouseEvent e) {
		// if ((graphpad.getCurrentProjectItem() != null)
		// && (graphpad.getCurrentProjectItem().getDocument() != null)) {
		// graphpad.getCommand("FileProjectOpenItem").actionPerformed(
		// null);
		// }
		//
		// super.mousePressed(e);
		// }

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				int x = e.getX();
				int y = e.getY();
				TreePath path = tree.getPathForLocation(x, y);

				if (path != null) {

					tree.setSelectionPath(path);

					Object userObject = ((IconNode) path.getLastPathComponent())
							.getUserObject();

					JPopupMenu popup;

					if (userObject instanceof IpssProjectRootItem)
						popup = popupRoot;
					else if (userObject instanceof IpssProject)
						popup = popupProject;
					else
						popup = popupItem;

					popup.show(tree, x, y);

				}
			}
		}

	}

}

class IconNodeRenderer extends DefaultTreeCellRenderer {

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		Icon icon = ((IconNode) value).getIcon();

		if (icon == null) {
			Hashtable icons = (Hashtable) tree.getClientProperty("JTree.icons");
			String name = ((IconNode) value).getIconName();
			if ((icons != null) && (name != null)) {
				icon = (Icon) icons.get(name);
				if (icon != null) {
					setIcon(icon);
				}
			}
		} else {
			setIcon(icon);
		}

		setText(((IconNode) value).getUserObject().toString());

		return this;
	}
}

class IconNode extends DefaultMutableTreeNode {

	protected Icon icon;

	protected String iconName;

	public IconNode(Object userObject) {
		this(userObject, true, null);
	}

	public IconNode(Object userObject, boolean allowsChildren, Icon icon) {
		super(userObject, allowsChildren);
		this.icon = icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getIconName() {
		if (iconName != null) {
			return iconName;
		} else {
			String str = userObject.toString();
			int index = str.lastIndexOf(".");
			if (index != -1) {
				return str.substring(++index);
			} else {
				return null;
			}
		}
	}

	public void setIconName(String name) {
		iconName = name;
	}

}

class TextIcons extends MetalIconFactory.TreeLeafIcon {

	protected String label;

	private static Hashtable labels;

	protected TextIcons() {
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		super.paintIcon(c, g, x, y);
		if (label != null) {
			FontMetrics fm = g.getFontMetrics();

			int offsetX = (getIconWidth() - fm.stringWidth(label)) / 2;
			int offsetY = (getIconHeight() - fm.getHeight()) / 2 - 2;

			g.drawString(label, x + offsetX, y + offsetY + fm.getHeight());
		}
	}

	public static Icon getIcon(String str) {
		if (labels == null) {
			labels = new Hashtable();
			setDefaultSet();
		}
		TextIcons icon = new TextIcons();
		icon.label = (String) labels.get(str);
		return icon;
	}

	public static void setLabelSet(String ext, String label) {
		if (labels == null) {
			labels = new Hashtable();
			setDefaultSet();
		}
		labels.put(ext, label);
	}

	private static void setDefaultSet() {
		labels.put("graph", "G");
		labels.put("report", "R");
	}
}