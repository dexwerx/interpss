/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * JGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.interpss.editor.coreframework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.interpss.editor.IpssPropertiesLoader;
import org.interpss.editor.doc.IpssDocument;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.doc.IpssProjectItem;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.editor.project.IpssProjectCodec;
import org.interpss.editor.project.IpssProjectPanel;
import org.interpss.editor.project.IpssTabbedPane;
import org.interpss.editor.refData.LoadScheduleRefData;
import org.interpss.editor.resources.ImageLoader;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.swing.GPSplitPane;
import org.interpss.editor.swing.tabbedpane.CloseListener;
import org.interpss.editor.swing.tabbedpane.DoubleClickListener;
import org.interpss.editor.util.ICommandRegistery;
import org.interpss.editor.util.SmartFrame;
import org.interpss.editor.util.Utilities;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.spring.BasePluginSpringFactory;
import org.interpss.spring.EditorSpringFactory;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.ui.IRefDataManager;
import org.jgraph.JGraph;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;

/**
 * This is the UI delegate of the JGraphpad multi JGraph document interface. To
 * gain the control of this class, you could subclass this class but since this
 * class delegates to its model its main behaviours, you would better set
 * (register in the XML configuration file) your custom implementations.
 * 
 * 
 * @author Gaudenz Alder
 * @author Sven Luzar
 * @author Raphael Valyi (major refactoring since Jan 2006, contributed as LGPL)
 * @version 1.3.2 Actions moved to an own package
 * @version 1.0 1/1/02
 */
public class GPGraphpad extends JComponent implements ICommandRegistery,
		IGraphicEditor {
	private static final long serialVersionUID = 1;

	/**
	 * parameters that can change from one sessions to another
	 */
	protected GPSessionParameters sessionParameters;

	/**
	 * Boolean for the visible state of the toolbars
	 */
	protected boolean toolBarsVisible = true;

	/**
	 * Log console for the System in and out messages
	 */
	protected Component logger;

	/**
	 * Desktoppane for the internal frames
	 */
	protected IpssTabbedPane desktop;

	protected JSplitPane split;

	/**
	 * Contains the mapping between GPDocument objects and GPInternalFrames.
	 */
	protected Hashtable doc2InternalFrame = new Hashtable();

	/**
	 * The toolbar for this graphpad
	 */
	protected JPanel toolBarMainPanel = new JPanel(new BorderLayout());

	/**
	 * The toolbar for this graphpad
	 */
	protected JPanel toolBarInnerPanel;

	/**
	 * The menubar for this graphpad
	 */
	protected JMenuBar menubar;

	/**
	 * The statusbar for this Graphpad instance
	 */
	protected IAppStatus statusbar;

	/**
	 * The main Panel with the status bar and the desktop pane
	 */
	protected JPanel mainPanel = new JPanel(new BorderLayout());

	private JPanel editorPanel = new JPanel();

	protected IpssProjectPanel projectPanel;

	protected boolean isDocMaxSize = false;

	private JFrame smartFrame = null;

	/**
	 * A configuration specific to the Graphpad instance. Remark: we would
	 * hardly need it, the static configuration should be sufficient.
	 */

	public GPGraphpad(IAppStatus aStatusbar) {
		setDoubleBuffered(true);
		this.statusbar = aStatusbar;
	}

	public GPGraphpad(GPSessionParameters sessionParameters) {
		setDoubleBuffered(true);
		this.sessionParameters = sessionParameters;
	}

	public void init() {
		IpssLogger.getLogger().info(
				"Command search path: "
						+ Translator.getString("CommandSearchPath"));
		GPPluginInvoker.setCommandSearchPath(Utilities.tokenize(Translator
				.getString("CommandSearchPath")));
		// instanciations of the singletons:

		logger = (Component) GPPluginInvoker.createSingleton("Console.class");
		GPPluginInvoker.createGraphpadAwareSingleton("BarFactory.class", this);

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());

		desktop = new IpssTabbedPane();

		desktop.setToolTipText("");

		desktop.addCloseListener(new CloseListener() {

			public void closeOperation(MouseEvent e) {

				int sel = desktop.getOverTabIndex();
				if (sel < 0)
					return;

				desktop.setSelectedIndex(sel);

				getCommand("FileClose").actionPerformed(null);
			}
		});

		desktop.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				update();
				expendTree2Object(getCurrentDocument());
			}
		});

		desktop.addDoubleClickListener(new DoubleClickListener() {

			public void doubleClickOperation(MouseEvent e) {
				hideShowSplit();
			}

		});

		// create the ProjectPanel
		createProjectPanel();

		split = new GPSplitPane(JSplitPane.HORIZONTAL_SPLIT, projectPanel,
				desktop);
		split.setDividerLocation(200);
		split.setOneTouchExpandable(true);

		// build the menu and the toolbar
		toolBarInnerPanel = GPBarFactory.getInstance().createToolBars(
				toolBarMainPanel, this, GPBarFactory.PADTOOLBARS);
		setToolBarsVisible(true);

		menubar = GPBarFactory.getInstance().createMenubar(this);

		add(BorderLayout.NORTH, menubar);
		add(BorderLayout.SOUTH, (JPanel) statusbar);
		add(BorderLayout.CENTER, mainPanel);

		// Don't show internals of pane being dragged
		// for performance
		// desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		update();
	}

	public void initData() {
		this.getProjectPanel().showOpenItems();
	}

	public void initActive() {
		this.getProjectPanel().showActiveItem();
	}

	// others

	public Action getCommand(String key) {
		return getCommand(key, getActionMap());
	}

	public final Action getCommand(final String key, ActionMap map) {
		return GPPluginInvoker.getCommand(key, map, this);
	}

	public final void initCommand(Action action) {
		if (action instanceof IpssAbstractActionDefault)
			((IpssAbstractActionDefault) action).setGraphpad(this);
	}

	/**
	 * return a shutdown routine.
	 */
	public WindowAdapter getAppCloser() {
		return new AppCloser();
	}

	/**
	 * To shutdown when run as an application. This is a fairly lame
	 * implementation. A more self-respecting implementation would at least
	 * check to see if a save was needed.
	 */
	protected final class AppCloser extends WindowAdapter {

		public void windowClosing(WindowEvent e) {
			getCommand("FileExit").actionPerformed(null);
		}
	}

	/**
	 * Find the hosting frame, for the file-chooser dialog.
	 */
	public Frame getFrame() {
		for (Container p = getParent(); p != null; p = p.getParent()) {
			if (p instanceof Frame) {
				return (Frame) p;
			}
		}
		return null;
	}

	public JMenuBar getMenubar() {
		return menubar;
	}

	/**
	 * Create a status bar
	 */
	protected IpssProjectPanel createProjectPanel() {
		projectPanel = new IpssProjectPanel(this);
		return projectPanel;
	}

	public IpssProjectPanel getProjectPanel() {
		return projectPanel;
	}

	/**
	 * Show a dialog with the given error message.
	 */
	public void error(String message) {
		JOptionPane.showMessageDialog(this, message, Translator
				.getString("Title"), JOptionPane.ERROR_MESSAGE);
	}

	// --- actions -----------------------------------

	public JGraph getCurrentGraph() {
		IpssEditorDocument doc = getCurrentDocument();
		if (doc == null)
			return null;
		if (doc instanceof GPDocument) {
			return ((GPDocument) doc).getGraph();
		} else
			return null;

	}

	public JInternalFrame getCurrentInternalFrame() {
		IpssDocInternalFrame internalFrame = (IpssDocInternalFrame) desktop
				.getSelectedComponent();
		// if (internalFrame == null) {
		// JInternalFrame[] frames = desktop.getAllFrames();
		// if (frames.length > 0) {
		// try {
		// frames[0].setSelected(true);
		// internalFrame = (GPDocFrame) frames[0];
		// } catch (PropertyVetoException e) {
		// return null;
		// }
		// }
		// }
		if (internalFrame == null)
			return null;
		return internalFrame;
	}

	public IpssEditorDocument getCurrentDocument() {
		if (desktop == null) // added by Mike, desktop may be null during unit testing
			return null;

		IpssDocInternalFrame internalFrame = (IpssDocInternalFrame)desktop.getSelectedComponent();
		if (internalFrame == null)
			return null;
		return internalFrame.getDocument();
	}

	public IpssProject getCurrentProject() {
		IpssProject[] projects = EditorSpringFactory.getAppContext()
				.getAllProjects();

		// no project
		if (projects == null)
			return null;

		// only one project
		if (projects.length == 1)
			return projects[0];

		// return selected project or project of selected item
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this
				.getProjectPanel().getTree().getLastSelectedPathComponent();

		// // return the first project
		// i
		// return projects[0];

		if (!(selectedNode == null)) {
			Object userObject = selectedNode.getUserObject();
			if (userObject instanceof IpssProject)
				return (IpssProject) userObject;
			if (userObject instanceof IpssProjectItem) {
				DefaultMutableTreeNode pnode = (DefaultMutableTreeNode) selectedNode
						.getParent();

				if (!(pnode.getUserObject() instanceof IpssProject))
					pnode = (DefaultMutableTreeNode) pnode.getParent();

				return (IpssProject) (pnode.getUserObject());

			}
		}

		// return project of currentdoc
		IpssEditorDocument curdoc = getCurrentDocument();
		if (curdoc != null)
			return curdoc.getProject();

		return null;
	}

	public IpssProjectItem getCurrentProjectItem() {

		// return selected project or project of selected item
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this
				.getProjectPanel().getTree().getLastSelectedPathComponent();

		if (!(selectedNode == null)) {
			Object userObject = selectedNode.getUserObject();
			if (userObject instanceof IpssProjectItem) {
				return (IpssProjectItem) userObject;
			} else
				return null;
		}
		return null;
	}

	public IpssEditorDocument[] getAllDocuments() {
		JInternalFrame[] frames = getAllFrames();

		if (frames != null && frames.length > 0) {
			ArrayList<IpssEditorDocument> docs = new ArrayList<IpssEditorDocument>();
			for (int i = 0; i < frames.length; i++) {
				// make sure to only pick up GPDocFrame instances
				if (frames[i] instanceof IpssDocInternalFrame) {
					docs.add(((IpssDocInternalFrame) frames[i]).getDocument());
				}
			}
			return docs.toArray(new IpssEditorDocument[docs.size()]);
		}
		return null;
	}

	public IpssProjectItem[] getAllOpenProjectItem() {

		ArrayList<IpssProjectItem> items = new ArrayList<IpssProjectItem>();

		IpssEditorDocument[] docs = getAllDocuments();

		if ((docs == null) || docs.length == 0)
			return null;

		for (int i = 0; i < docs.length; i++) {
			IpssProjectItem item = this.getProjectPanel().findProjectItem(
					docs[i]);
			if (item != null)
				items.add(item);
		}

		return items.toArray(new IpssProjectItem[items.size()]);

	}

	/**
	 * Returns the undoAction.
	 * 
	 * @return UndoAction
	 */
	public IpssAbstractActionDefault getEditUndoAction() {
		return (IpssAbstractActionDefault) this.getCommand("EditUndo");
	}

	/**
	 * Returns the redoAction.
	 * 
	 * @return RedoAction
	 */
	public IpssAbstractActionDefault getEditRedoAction() {
		return (IpssAbstractActionDefault) this.getCommand("EditRedo");
	}

	public Component getLogConsole() {
		return logger;
	}

	public void setLogConsole(Component console) {
		logger = console;
	}

	public boolean isToolBarsVisible() {
		return this.toolBarsVisible;
	}

	public void setToolBarsVisible(boolean state) {
		this.toolBarsVisible = state;

		// if (state == true) {
		// mainPanel.remove(desktop);
		// toolBarInnerPanel.add(BorderLayout.CENTER, desktop);
		// mainPanel.add(BorderLayout.CENTER, toolBarMainPanel);
		// } else {
		// mainPanel.remove(toolBarMainPanel);
		// toolBarInnerPanel.remove(desktop);
		// mainPanel.add(BorderLayout.CENTER, desktop);
		// }
		// desktop.repaint();

		if (state == true) {
			mainPanel.remove(split);
			toolBarInnerPanel.add(BorderLayout.CENTER, split);
			mainPanel.add(BorderLayout.CENTER, toolBarMainPanel);
		} else {
			mainPanel.remove(toolBarMainPanel);
			toolBarInnerPanel.remove(split);
			mainPanel.add(BorderLayout.CENTER, split);

		}
		split.repaint();

	}

	public void addGPInternalFrame(IpssDocInternalFrame f) {
		desktop.addTab(f.getDocument().getFileName(), f.getFrameIcon(), f, f
				.getDocument().getName());
		// desktop.add(f);
		// try {
		desktop.setSelectedComponent(f);
		// f.setSelected(true);
		// } catch (Exception ex) {
		// System.out.println(ex.toString());
		// }
		doc2InternalFrame.put(f.getDocument(), f);
	}

	/**
	 * removes the specified Internal Frame from the Graphpad
	 */
	public void removeGPInternalFrame(IpssDocInternalFrame f) {
		if (f == null)
			return;
		f.setVisible(false);

		desktop.remove(f);

		doc2InternalFrame.remove(f.getDocument());
		f.cleanUp();
		JInternalFrame[] frames = getAllFrames();
		if ((frames != null) && (frames.length > 0)) {
			try {
				desktop.setSelectedComponent(frames[0]);
				// frames[0].setSelected(true);
			} catch (Exception e) {
			}
		}
	}

	public void exit() {
		if (!sessionParameters.isApplet()) {
			System.exit(0);
		} else {
			// stop the grid if started
			GridEnvHelper.stopDefaultGrid();
			getFrame().dispose();
			String viewPath = sessionParameters.getParam(
					GPSessionParameters.VIEWPATH, false);
			if (viewPath != null) {
				try {
					URL codeBase = sessionParameters.getApplet().getCodeBase();
					URL url = new URL(codeBase, viewPath);
					sessionParameters.getApplet().getAppletContext()
							.showDocument(url, "_self");// NOT SURE IT'S USEFUL
				} catch (MalformedURLException mue) {
					System.out.println(mue);
				}
			}
		}
	}

	/**
	 * ask every action to update itself
	 */
	public void update() {
		IpssEditorDocument currentDoc = getCurrentDocument();
		Object[] keys = getActionMap().keys();
		for (int i = 0; i < keys.length; i++) {
			Action a = getActionMap().get(keys[i]);

			if (a instanceof IpssAbstractActionDefault) {
				((IpssAbstractActionDefault) a).update();
			} else {
				if (currentDoc == null) {
					a.setEnabled(false);
				} else {
					a.setEnabled(true);
				}
			}
		}
	}

	public JInternalFrame[] getAllFrames() {
		if (desktop.getComponents() == null)
			return null;
		else {

			Component[] comps = desktop.getComponents();

			ArrayList<JInternalFrame> iframs = new ArrayList<JInternalFrame>();

			for (int i = 0; i < comps.length; i++) {
				if (comps[i] instanceof JInternalFrame)
					iframs.add((JInternalFrame) comps[i]);
			}

			if (iframs != null && iframs.size() > 0)
				return iframs.toArray(new JInternalFrame[iframs.size()]);
			else
				return null;
		}

		// return (JInternalFrame[])(desktop.getComponents());
	}

	public void addDesktopContainerListener(ContainerListener listener) {
		desktop.addContainerListener(listener);
	}

	public void removeDesktopContainerListener(ContainerListener listener) {
		desktop.removeContainerListener(listener);
	}

	public Hashtable getDoc2InternalFrame() {
		return doc2InternalFrame;
	}

	public void setDoc2InternalFrame(Hashtable doc2InternalFrame) {
		this.doc2InternalFrame = doc2InternalFrame;
	}

	public void addDocument2Frame(IpssEditorDocument doc) {
		IpssDocInternalFrame iframe = new IpssDocInternalFrame(doc);
		addGPInternalFrame(iframe);
		iframe.show();
		iframe.grabFocus();
	}

	public void addGraphDocument(String name, IpssProject p) {
		addGraphDocument(name, p, null);
	}

	public IpssProjectItem addGraphDocument(String name, IpssProject p,
			GPGraphpadFile jgraphpadCEFile) {

		if (p == null)
			return null;

		GPDocument doc = new GPDocument(this, p, name, jgraphpadCEFile);
		doc = GPPluginInvoker.decorateDocument(doc);

		p.addDocument(doc, 0);
		addDocument2Frame(doc);

		IpssProjectItem item = this.getProjectPanel().addNewProjectItem(p, doc);

		expendTree2CurrentDocument();

		return item;
	}

	public void addGraphDocument(IpssProjectItem item,
			GPGraphpadFile jgraphpadCEFile) {

		GPDocument doc = new GPDocument(this, item.getProject(),
				item.getName(), jgraphpadCEFile);
		doc = GPPluginInvoker.decorateDocument(doc);

		item.setDocument(doc);

		addDocument2Frame(doc);

		expendTree2CurrentDocument();
	}

	public void addTextDocument(String name, IpssProjectItem item) {
		addTextDocument(name, item, null, true);
	}

	public void addTextDocument(String name, IpssProjectItem item, IpssTextFile file, boolean openfile) {

		if (item == null)
			return;

		IpssTextDocument doc = new IpssTextDocument(this, item.getProject(),
				name, file);

		item.addDocument(doc, 0);
		if (openfile)
			addDocument2Frame(doc);

		this.getProjectPanel().addNewProjectItem(item, doc);

		expendTree2CurrentDocument();
	}

	public void addTextDocument(String name, IpssProject p) {
		addTextDocument(name, p, null);
	}

	public void addTextDocument(String name, IpssProject p, IpssTextFile file) {

		if (p == null)
			return;

		IpssTextDocument doc = new IpssTextDocument(this, p, name, file);

		p.addDocument(doc, 0);
		addDocument2Frame(doc);

		this.getProjectPanel().addNewProjectItem(p, doc);

		expendTree2CurrentDocument();
	}

	public void addTextDocument(IpssProjectItem item, IpssTextFile file) {

		IpssTextDocument doc = new IpssTextDocument(this, item.getProject(),
				item.getName(), file);

		item.setDocument(doc);

		addDocument2Frame(doc);

		expendTree2CurrentDocument();
	}

	/*
	 * this a duplicated method public void addReportDocument(String name,
	 * IpssProjectItem item, String reportType) { addReportDocument(name, item,
	 * null, reportType); }
	 */

	/*
	 * public void addReportDocument(String name, IpssProjectItem item,
	 * IpssReportFile file, String reportType) {
	 * 
	 * if (item == null) return; try { IpssReportDocument doc = new
	 * IpssReportDocument(this, item.getProject(), name, file, reportType);
	 * 
	 * item.addDocument(doc, 0); addDocument2Frame(doc);
	 * 
	 * this.getProjectPanel().addNewProjectItem(item, doc); } catch (Exception
	 * e) { e.printStackTrace(); } expendTree2CurrentDocument(); }
	 */
	/*
	 * // change name from addReportDocuemnt to newReportDocument public void
	 * newReportDocument(String name, IpssProject p, String reportType) {
	 * addReportDocument(name, p, null, reportType); }
	 */

	/*
	 * refactor by Mike
	 * 
	 * consolidated addReportDocument methods to two new methods:
	 * newReportDocument() - create an new report loadReportDocument() - load an
	 * existing report from the workspace
	 */
	// changed name from addReportDocuemnt to newReportDocument
	// changed such that the report may not add
	// We need to return the created report doc
	public IpssProjectItem newReportDocument(String name, IpssProjectItem item,
			String reportType, boolean add2Project) {

		if (item == null)
			return null;

		try {
			IpssReportDocument doc = new IpssReportDocument(this, item
					.getProject(), name, reportType);
			addDocument2Frame(doc);
			if (add2Project) {
				IpssProjectItem rptitem = item.getItem(name);
				if (rptitem == null) {
					item.addDocument(doc, 0);
					IpssProjectItem newItem = this.getProjectPanel()
							.addNewProjectItem(item, doc);
					expendTree2CurrentDocument();
					// return item for saving report file
					return newItem;
				} else {
					if (rptitem.isLoaded())
						this.closeDocument((IpssEditorDocument) rptitem
								.getDocument());
					rptitem.setDocument(doc);
					this.OpenDocument(doc);
					expendTree2Object(doc);
					return rptitem;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// change name from addReportDocument to loadReportDocument
	public void loadReportDocument(IpssProjectItem item, IpssReportFile file) {
		try {
			IpssReportDocument doc = new IpssReportDocument(this, item
					.getProject(), item.getName(), file);

			item.setDocument(doc);

			addDocument2Frame(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		expendTree2CurrentDocument();
	}

	public void addCustomDocument(IpssProjectItem item, IpssCustomFile file) {

		IpssCustomDocument doc = new IpssCustomDocument(this,
				item.getProject(), item.getName(), file);

		item.setDocument(doc);

		addDocument2Frame(doc);

		expendTree2CurrentDocument();
	}

	public IpssProjectItem addCustomDocument(String name, IpssProject p,
			IpssCustomFile file) {

		if (p == null)
			return null;

		IpssCustomDocument doc = new IpssCustomDocument(this, p, name, file);

		p.addDocument(doc, 0);
		addDocument2Frame(doc);

		IpssProjectItem item = this.getProjectPanel().addNewProjectItem(p, doc);

		expendTree2CurrentDocument();

		return item;
	}

	public void addXmlDocument(String name, IpssProjectItem item) {
		addXmlDocument(name, item, null);
	}

	public void addXmlDocument(String name, IpssProjectItem item,
			IpssXmlFile file) {

		if (item == null)
			return;

		IpssXmlDocument doc = new IpssXmlDocument(this, item.getProject(),
				name, file);

		item.addDocument(doc, 0);
		addDocument2Frame(doc);

		this.getProjectPanel().addNewProjectItem(item, doc);

		expendTree2CurrentDocument();
	}

	public void addXmlDocument(String name, IpssProject p) {
		addXmlDocument(name, p, null);
	}

	public void addXmlDocument(String name, IpssProject p, IpssXmlFile file) {

		if (p == null)
			return;

		IpssXmlDocument doc = new IpssXmlDocument(this, p, name, file);

		p.addDocument(doc, 0);
		addDocument2Frame(doc);

		this.getProjectPanel().addNewProjectItem(p, doc);

		expendTree2CurrentDocument();
	}

	public void addXmlDocument(IpssProjectItem item, IpssXmlFile file) {

		IpssXmlDocument doc = new IpssXmlDocument(this, item.getProject(),
				item.getName(), file);

		item.setDocument(doc);

		addDocument2Frame(doc);

		expendTree2CurrentDocument();
	}

	// public void addCustomDocument(IpssCustomDocument doc) {
	//
	// if (doc.getProject() == null)
	// return;
	//
	// doc.getProject().addDocument(doc, 0);
	//		
	// addDocument2Frame(doc);
	//
	// this.getProjectPanel().addNewProjectItem(doc.getProject(), doc);
	//
	// expendTree2CurrentDocument();
	// }

	public boolean addProject(IpssProject p) {
		if (p == null) {
			return false;
		}

		EditorSpringFactory.getAppContext().addProject(p);

		this.getProjectPanel().addNewAllProject(p);

		expendTree2Object(p);

		return true;
	}

	public void OpenCurrentProjectItem() {
		OpenProjectItem(getCurrentProjectItem());
	}

	public void OpenProjectItem(IpssProjectItem item) {
		if (item == null)
			return;

		// Mike
		IpssLogger.getLogger().info(
				"Open project item, name, file, dbId: " + item.getName() + ", "
						+ item.getFileName() + ", " + item.getProjDbId());

		IpssDocument doc = item.getDocument();

		if (doc == null) {
			if (item.getName().endsWith("ipss")) {
				try {
					GPGraphpadFile file = Utilities.OpenGraphFile(this,
							new FileInputStream(item.getName()));
					if (file == null)
						return;
					addGraphDocument(item, file);
				} catch (Exception ex) {
					BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"InterPSS Graphic File Open Error", ex.toString());
					ex.printStackTrace();
				}
			}
			// Mike else if (item.getName().endsWith("ipssdat")) { we do not put
			// any restriction here
			// else if (item.getName().endsWith("ipssdat")) {
			else if (Utilities.haveExt(PluginSpringFactory.getCustomFileAdapterList(), item.getFileExt())) {
				try {
					// at this point, we open a file, for exmple PSS/E raw. version should be 
					// inside the file
					IpssCustomFile file = Utilities.OpenCustomFile(this, item.getName(), "");

					if (file == null)
						return;
					addCustomDocument(item, file);
				} catch (Exception ex) {
					BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"InterPSS Custom Data File Open Error",
							ex.toString());
					ex.printStackTrace();
				}
			} else if (item.getName().endsWith("xml")) {
				try {
					IpssXmlFile file = Utilities.OpenXmlFile(this, item
							.getName());

					if (file == null)
						return;
					addXmlDocument(item, file);
				} catch (Exception ex) {
					BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"InterPSS Xml Data File Open Error", ex.toString());
					ex.printStackTrace();

				}
			} else if (item.getName().endsWith("txt")) {
				try {
					IpssTextFile file = Utilities.OpenTextFile(this, item
							.getName());

					if (file == null)
						return;
					addTextDocument(item, file);
				} catch (Exception ex) {
					BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"Text File Open Error", ex.toString());
					ex.printStackTrace();
				}
			} else if (item.getName().endsWith("ipssrpt")) {
				try {
					IpssReportFile file = Utilities.OpenReportFile(this, item
							.getName());

					if (file == null)
						return;
					loadReportDocument(item, file);
				} catch (Exception ex) {
					BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(
							"Report Open Error", ex.toString());
					ex.printStackTrace();
				}
			}

			update();
		} else {
			if (doc instanceof IpssEditorDocument) {
				OpenDocument((IpssEditorDocument) doc);
			}
		}

		doc = item.getDocument();
		if (doc instanceof GPDocument || doc instanceof IpssCustomDocument) {
			// Richard: the following logic also need to be applied when we
			// import an exiting graphic or custom project. Begin
			try {
				IAppSimuContext appSimuContext = org.interpss.editor.util.Utilities.loadProjectData(item);
				appSimuContext.getProjData().setProjectName(appSimuContext.getProjData().getFilename());
			// end
				if (doc instanceof GPDocument) {
				// we need synch some data in the graph with the project data,
				// since project data may be
				// created later.
				// String str = ((GPDocument) doc).getGFormContainer()
				// .getGNetForm().getLabel(IUserData.NET_LABEL);
				// IpssLogger.getLogger().info(
				// "ProjectData.name updated to " + str);
					((GPDocument) doc).getGFormContainer().getGNetForm()
						.setNewState(false);
				}
				else {
					((IpssCustomDocument)doc).setSimuAppContext(appSimuContext);
				}
			} catch (Exception e) {
				IpssLogger.logErr(e);
				return;
			}				
		}
	}

	public boolean OpenDocument(IpssEditorDocument doc) {
		if (doc.equals(getCurrentDocument()))
			return true;

		IpssDocInternalFrame iFrame = (IpssDocInternalFrame) getDoc2InternalFrame()
				.get(doc);
		if (iFrame == null) {
			addDocument2Frame(doc);
			return false;
		} else {
			try {
				desktop.setSelectedComponent(iFrame);
				return true;
			} catch (Exception e) {
				return false;
				// e.printStackTrace();
			}
		}
	}

	public void closeDocument(IpssEditorDocument docu) {

		if (!OpenDocument(docu))
			return;

		// close doc
		IpssEditorDocument doc = getCurrentDocument();
		if (doc == null)
			return;

		IpssProject project = doc.getProject();

		if (doc.close(true)) {
			removeDocument(doc);
			project.closeDocument(doc);
		}

	}

	public boolean saveProject(IpssProject project) {

		String fileName = project.getProjectFilePathName();
		if (fileName == null) {
			return false;
		}

		try {
			IpssProjectCodec.getInstance(this).write(
					new FileOutputStream(fileName), project);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

	public void renameTabbedFrames(IpssProjectItem item, String newFilePathName) {
		item.setName(newFilePathName);
		saveProject(item.getProject());

		if (item.isLoaded()) {
			IpssDocInternalFrame iFrame = (IpssDocInternalFrame) getDoc2InternalFrame()
					.get(item.getDocument());

			int index = desktop.indexOfComponent(iFrame);
			desktop.setTitleAt(index, iFrame.getDocument().getFileName());
			desktop.setToolTipTextAt(index, iFrame.getDocument().getName());
		}

	}

	public void removeDocument(IpssEditorDocument doc) {
		IpssDocInternalFrame iFrame = (IpssDocInternalFrame) getDoc2InternalFrame()
				.get(doc);
		removeGPInternalFrame(iFrame);
	}

	public GPSessionParameters getSessionParameters() {
		return sessionParameters;
	}

	public void setSessionParameters(GPSessionParameters sessionParameters) {
		this.sessionParameters = sessionParameters;
	}

	/**
	 * Get the status panel
	 * 
	 */
	public IAppStatus getStatusPanel() {
		return statusbar;
	}

	public IAppStatus getAppStatusPanel() {
		return statusbar;
	}

	public void setStatus(String status) {
		statusbar.getStatusLabel().setText("Status: " + status);
	}

	/**
	 * Check if the background processing thread is busy
	 * 
	 * @return true or false
	 */
	public boolean isBGProcessingBusy() {
		if (getStatusPanel().isBusy()) {
			BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(
					"Processing Thread Busy",
					"Please wait for the completion of "
							+ getStatusPanel().getBusyMsg());
			return true;
		}
		return false;
	}

	public void hideShowSplit() {
		if (isDocMaxSize) {
			split.setDividerLocation(split.getLastDividerLocation());
		} else {
			split.setDividerLocation(0);
		}
		isDocMaxSize = !isDocMaxSize;
		// if (split.getDividerLocation() > 0)
		// split.setDividerLocation(0);
		// else
		// split.setDividerLocation(split.getLastDividerLocation());

	}

	public LoadScheduleRefData getLoadScheduleRefData() {
		return (LoadScheduleRefData) EditorSpringFactory.getRefDataManager()
				.getRefDataObject(IRefDataManager.REFDATA_LoadSchedule);
	}

	public void expendTree2CurrentDocument() {
		expendTree2Object(this.getCurrentDocument());
	}

	public void expendTree2Object(Object o) {
		this.getProjectPanel().expendTree2Object(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.interpss.editor.jgraph.ui.IGraphicEditor#getAppStatus()
	 */
	public IAppStatus getAppStatus() {
		return EditorSpringFactory.getStatusPanel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.interpss.editor.jgraph.ui.IGraphicEditor#getProject()
	 */
	public IAppSimuContext getCurrentAppSimuContext() throws Exception {
		if (getCurrentDocument() != null)
			return getCurrentDocument().getSimuAppContext();
		else
			throw new Exception("Current documentation cannot be found");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.interpss.editor.jgraph.ui.IGraphicEditor#setAppTitle()
	 */
	public String getVersion() {
		return IpssPropertiesLoader.getIpssString("Prog.version");
	}

	public void refreshDocumentEditorPanel(IpssEditorDocument doc) {

		// if ((doc instanceof GPDocument) || (doc instanceof IpssTextDocument))
		// {
		if (doc != null) { // add by Mike, doc may be null during unit testing
			doc.updateFrameTitle();
			IpssDocInternalFrame iFrame = (IpssDocInternalFrame) getDoc2InternalFrame()
					.get(doc);

			int index = desktop.indexOfComponent(iFrame);
			desktop.setTitleAt(index, doc.getTabTitle());
		}

		// IGFormContainer netContainer
		// =((GPDocument)getCurrentDocument()).getGFormContainer();
		// if (netContainer.isDataDirty()) {
		// // When the current graphic file is edited, the dataDirty set
		// true, we need to
		// // do something for example put a dirty * indicator
		// }
		// }
	}

	public void refreshCurrentDocumentEditorPanel() {
		refreshDocumentEditorPanel(getCurrentDocument());
	}

	public String getRootDir() {
		return StringUtil.getInstallLocation();
	}

	public String getWorkspace() {
		return EditorSpringFactory.getAppContext().getWorkspaceDir();
	}

	public String getCurrentProjectFolder() {
		if (getCurrentDocument() != null)
			return getCurrentDocument().getProject().getProjectName();
		else
			return "testing"; // in testing situations, the document is not
								// existing
	}

	public String getCurrentProjectName() {
		if (getCurrentDocument() != null)
			return Utilities.getFileNameNoExt(getCurrentDocument().getName());
		else
			return "TestProject"; // in testing situations, the document is
									// not existing
	}

	public JFrame getSmartFrame() {
		return this.smartFrame;
	}

	public void createEditorPanel(GPSessionParameters sessionParameters) {
		setSessionParameters(sessionParameters);
		init();

		editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());
		editorPanel.add(this, BorderLayout.CENTER);
		editorPanel.setVisible(false);

		this.smartFrame = new SmartFrame();
		smartFrame.setName("MainGraphpad");
		smartFrame.setIconImage(ImageLoader.getImageIcon(
				Translator.getString("Icon")).getImage());
		smartFrame.setTitle(Translator.getString("Title"));
		smartFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		smartFrame.getContentPane().add(editorPanel, BorderLayout.CENTER);
		smartFrame.addWindowListener(getAppCloser());
		smartFrame.setVisible(true);

		initData();
		editorPanel.setVisible(true);
		initActive();
	}

	public JPanel getEditorPanel() {
		return editorPanel;
	}

	public void closeWorkspace(ActionEvent e) {
		expendTree2Object(getCurrentDocument());
		IpssProjectItem aitem = getCurrentProjectItem();

		IpssProjectItem[] items = getAllOpenProjectItem();

		getCommand("FileCloseAllOpenItem").actionPerformed(e);

		if ((items != null) && (items.length > 0))
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(aitem))
					items[i].setInit_Status(IpssProjectItem.STATUS_ACTIVE);
				else
					items[i].setInit_Status(IpssProjectItem.STATUS_OPEN);
			}
		// Save projects
		IpssProject[] projects = EditorSpringFactory.getAppContext()
				.getAllProjects();
		if ((projects != null) && (projects.length > 0))
			for (int i = 0; i < projects.length; i++)
				saveProject(projects[i]);
	}
}
