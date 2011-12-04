package org.interpss.editor.coreframework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.MissingResourceException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;

import org.interpss.editor.coreframework.jgraphsubclassers.GPGraphModel;
import org.interpss.editor.coreframework.jgraphsubclassers.GPMarqueeHandler;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.graphcellsbase.cellviews.JGraphBusView;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.cells.BusCell;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.ICommandRegistery;
import org.interpss.editor.util.IEdgeFactory;
import org.interpss.editor.util.IVertexFactory;
import org.interpss.editor.util.Rule;
import org.jgraph.JGraph;
import org.jgraph.event.GraphLayoutCacheEvent;
import org.jgraph.event.GraphLayoutCacheListener;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.Port;
import org.jgraph.plaf.GraphUI;


/**
 * A Document represents a single instance of a graph view with associated
 * library and overview panes. The document deals with a lot of the listening
 * required on the graph, prompting for save if modified, undo handling and top
 * level UI issues relating to pane positioning.
 * 
 * You could subclass GPDocument, but often subclassing the objects GPDocument
 * deals with should be sufficient. In order to ensure subclassers will be used
 * by GPDocuments, critical objects are instanciated by a factory where you can
 * register custom subclassers.
 * 
 * @see org.interpss.editor.coreframework.GPGraphpadModel
 */
public class GPDocument extends IpssEditorDocument implements
		GraphSelectionListener, ComponentListener, Printable,
		GraphModelListener, PropertyChangeListener, GraphLayoutCacheListener,
		ICommandRegistery {

	/**
	 */
	protected boolean enableTooltips;

	/**
	 * Container for the graph so that you can scroll over the graph
	 */
	protected JScrollPane scrollPane;

	/**
	 * The joint graph for this document
	 */
	protected JGraph graph;

	/**
	 * Action used for fitting the size
	 */
	protected Action fitAction;

	/**
	 * The overview Dialog for this document. Can be <tt>null</tt>.
	 * 
	 */
	protected JDialog overviewDialog;

	/**
	 * The column rule for the graph
	 */
	protected Rule columnRule;

	/**
	 * The row rule for the graph
	 */
	protected Rule rowRule;

	/**
	 * The graphUndoManager manager for the joint graph.
	 * 
	 * @see #graph
	 */
	protected GraphUndoManager graphUndoManager;

	/**
	 * The graphUndoManager handler for the current document. Each document has
	 * his own handler. So you can make an graphUndoManager seperate for each
	 * document.
	 * 
	 */
	protected GPUndoHandler undoHandler;

	/**
	 * True if this documents graph model was modified since last save.
	 */
	// protected boolean modified = false;
	/**
	 * true if the current graph is Metric. default is true.
	 */
	protected static boolean isMetric = true;

	/**
	 * true if the ruler show is activated
	 */
	protected static boolean showRuler = true;

	/**
	 * contains the find pattern for this document
	 */
	protected String findPattern;

	/**
	 * contains the last found object
	 */
	protected Object lastFound;

	protected GPGraphpadFile jGraphpadCEFile;

	protected JPanel overviewPane;

	protected ArrayList edgeCreators = new ArrayList();

	protected ArrayList vertexnPortsCreators = new ArrayList();

	protected Image backgroundImage;

	protected boolean pagevisible = false;

	protected transient PageFormat pageFormat = new PageFormat();

	// private GFormContainer _netContainer = null;

	/**
	 * Static initializer. Initializes some static variables.
	 */
	static {
		isMetric = new Boolean(Translator.getString("IsMetric")).booleanValue();
		showRuler = new Boolean(Translator.getString("ShowRuler"))
				.booleanValue();
	}

	/**
	 * Constructor for GPDocument.
	 */
	public GPDocument(GPGraphpad gp, IpssProject p, String name,
			GPGraphpadFile file) {
		setDoubleBuffered(true);
		updateUI();

		this.setName(name);
		setGraphpad(gp);
		setProject(p);

		/*
		 * Mike: use the SpringFramework to manage graph object GraphModel
		 * graphModel = (GraphModel) GPPluginInvoker
		 * .instanciateObjectForKey("GraphModel.class"); // this._netContainer =
		 * new // GFormContainer(SpringAppContext.getIpssMsgHub());
		 * 
		 * graph = (JGraph) GPPluginInvoker
		 * .instanciateObjectForKey("JGraph.class"); graph.setModel(graphModel);
		 */

		graph = GraphSpringFactory.getIpssGraph();

		graph.setUI((GraphUI) GPPluginInvoker
				.instanciateDocumentAwareObjectForKey("GraphUI.class", this,
						true));

		graph.invalidate();
		graph.getGraphLayoutCache().setFactory(
				(CellViewFactory) GPPluginInvoker
						.instanciateObjectForKey("ViewFactory.class"));

		graph.setDragEnabled(false);
		graph.setJumpToDefaultPort(true);
		graph.setInvokesStopCellEditing(true);
		graph.setCloneable(true);
		if (file != null) {
			this.setProjData(file.getProjData());
			graph.setGraphLayoutCache(file.getGraphLayoutCache());
			this.jGraphpadCEFile = file;
		} else {
			this.jGraphpadCEFile = new GPGraphpadFile();
			jGraphpadCEFile.setGraphLayoutCache(graph.getGraphLayoutCache());
		}

		undoHandler = new GPUndoHandler(this);
		this.graphUndoManager = createGraphUndoManager();

		// Add this as a listener for undoable edits.
		graph.getModel().addUndoableEditListener(undoHandler);

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());

		Component comp = createScrollPane();
		JPanel toolBarInnerPanel;
		JPanel toolBarMainPanel = new JPanel(new BorderLayout());
		toolBarInnerPanel = GPBarFactory.getInstance().createToolBars(
				toolBarMainPanel, this, GPBarFactory.DOCTOOLBARS);

		this.add(BorderLayout.NORTH, toolBarMainPanel);
		this.add(BorderLayout.CENTER, comp);

		GPMarqueeHandler marqueeHandler = (GPMarqueeHandler) GPPluginInvoker
				.instanciateDocumentAwareObjectForKey("MarqueeHandler.class",
						this, true);
		graph.setMarqueeHandler(marqueeHandler);

		registerListeners(graph);
		addComponentListener(this);

		setEnableTooltips(false);
		update();
	}

	/**
	 * Get the NetContainer object
	 * 
	 * @return the net container object
	 */
	public IGFormContainer getGFormContainer() {
		return ((GPGraphModel) (graph.getModel())).getGFormContainer();
		// return _netContainer;
	}

	protected Component createScrollPane() {
		scrollPane = new JScrollPane(graph);
		JViewport port = scrollPane.getViewport();
		try {
			String vpFlag = Translator.getString("ViewportBackingStore");
			Boolean bs = new Boolean(vpFlag);
			if (bs.booleanValue()) {
				port.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
			} else {
				port.setScrollMode(JViewport.BLIT_SCROLL_MODE);
			}
		} catch (MissingResourceException mre) {
			// just use the viewport default
		}
		columnRule = new Rule(Rule.HORIZONTAL, isMetric, graph);
		rowRule = new Rule(Rule.VERTICAL, isMetric, graph);
		if (showRuler) {
			scrollPane.setColumnHeaderView(columnRule);
			scrollPane.setRowHeaderView(rowRule);
		}
		return scrollPane;
	}

	protected GraphUndoManager createGraphUndoManager() {
		return new GraphUndoManager();
	}

	/**
	 * Fetch the editor contained in this panel
	 */
	public JGraph getGraph() {
		return graph;
	}

	/**
	 * Returns the model of the graph
	 */
	public GraphModel getModel() {
		return graph.getModel();
	}

	/**
	 * Returns the view from the current graph
	 * 
	 */
	public GraphLayoutCache getGraphLayoutCache() {
		return graph.getGraphLayoutCache();
	}

	/* Add this documents listeners to the specified graph. */
	public void registerListeners(JGraph graph) {
		graph.getSelectionModel().addGraphSelectionListener(this);
		graph.getModel().addGraphModelListener(this);
		graph.getGraphLayoutCache().addGraphLayoutCacheListener(this);
		graph.addPropertyChangeListener(this);
		graph.getGraphLayoutCache().addGraphLayoutCacheListener(this);
	}

	public void setModified(boolean modified) {
		this.modified = modified;
		if (!modified)
			getGFormContainer().setDataDirty(false);
		graphpad.refreshDocumentEditorPanel(this);
		updateFrameTitle();
		getGraphpad().update();
	}
	
	// added by Mike
	public boolean isModified() {
		return super.isModified() || getGFormContainer().isDataDirty();
	}

	/* Return the scale of this document as a string. */
	protected String getDocumentScale() {
		return Integer.toString((int) (graph.getScale() * 100)) + "%";
	}

	/* Sets the attributes of the selected cells. */
	public void setSelectionAttributes(Map map) {
		map = new Hashtable(map);
		map.remove(GraphConstants.BOUNDS);
		map.remove(GraphConstants.POINTS);
		graph.getGraphLayoutCache().edit(graph.getSelectionCells(), map);
	}

	/* Sets the attributes of the selected cells. */
	public void setFontSizeForSelection(float size) {
		Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(),
				graph.getSelectionCells()).toArray();
		// Filter ports out
		java.util.List list = new ArrayList();
		for (int i = 0; i < cells.length; i++)
			if (!(cells[i] instanceof Port))
				list.add(cells[i]);
		cells = list.toArray();

		Map nested = new Hashtable();
		for (int i = 0; i < cells.length; i++) {
			CellView view = graph.getGraphLayoutCache().getMapping(cells[i],
					false);
			if (view != null) {
				Font font = GraphConstants.getFont(view.getAllAttributes());
				AttributeMap attr = new AttributeMap();
				GraphConstants.setFont(attr, font.deriveFont(size));
				nested.put(cells[i], attr);
			}
		}
		graph.getGraphLayoutCache().edit(nested, null, null, null);
	}

	/* Sets the attributes of the selected cells. */
	public void setFontStyleForSelection(int style) {
		Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(),
				graph.getSelectionCells()).toArray();
		// Filter ports out
		java.util.List list = new ArrayList();
		for (int i = 0; i < cells.length; i++)
			if (!(cells[i] instanceof Port))
				list.add(cells[i]);
		cells = list.toArray();

		Map nested = new Hashtable();
		for (int i = 0; i < cells.length; i++) {
			CellView view = graph.getGraphLayoutCache().getMapping(cells[i],
					false);
			if (view != null) {
				Font font = GraphConstants.getFont(view.getAllAttributes());
				AttributeMap attr = new AttributeMap();
				GraphConstants.setFont(attr, font.deriveFont(style));
				nested.put(cells[i], attr);
			}
		}
		graph.getGraphLayoutCache().edit(nested, null, null, null);
	}

	/* Sets the attributes of the selected cells. */
	public void setFontNameForSelection(String name) {
		Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(),
				graph.getSelectionCells()).toArray();
		// Filter ports out
		java.util.List list = new ArrayList();
		for (int i = 0; i < cells.length; i++)
			if (!(cells[i] instanceof Port))
				list.add(cells[i]);
		cells = list.toArray();

		Map nested = new Hashtable();
		for (int i = 0; i < cells.length; i++) {
			CellView view = graph.getGraphLayoutCache().getMapping(cells[i],
					false);
			if (view != null) {
				Font font = GraphConstants.getFont(view.getAllAttributes());
				AttributeMap attr = new AttributeMap();
				GraphConstants.setFont(attr, new Font(name, font.getStyle(),
						font.getSize()));
				nested.put(cells[i], attr);
			}
		}
		graph.getGraphLayoutCache().edit(nested, null, null, null);
	}

	public void rotateBusForSelection() {
		Object[] cells = DefaultGraphModel.getDescendants(graph.getModel(),
				graph.getSelectionCells()).toArray();
		// Filter ports out
		java.util.List list = new ArrayList();
		for (int i = 0; i < cells.length; i++)
			if (!(cells[i] instanceof Port) && (cells[i] instanceof BusCell))
				list.add(cells[i]);
		cells = list.toArray();

		Map nested = new Hashtable();
		for (int i = 0; i < cells.length; i++) {
			BusCell cell = ((BusCell) cells[i]);

			cell.getBusForm().setOrientation(
					cell.isVertical() ? GBusForm.H_Orientation
							: GBusForm.V_Orientation);
			CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
			((JGraphBusView) view).rotateSetAttributes();
		}
		graph.getGraphLayoutCache().edit(nested, null, null, null);
	}

	// -----------------------------------------------------------------
	// Component Listener
	// -----------------------------------------------------------------
	public void setResizeAction(AbstractAction e) {
		fitAction = e;
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		if (fitAction != null)
			fitAction.actionPerformed(null);
	}

	public void componentShown(ComponentEvent e) {
		componentResized(e);
	}

	public void setScale(double scale) {
		scale = Math.max(Math.min(scale, 16), .01);
		graph.setScale(scale);
		componentResized(null);
	}

	// ----------------------------------------------------------------------
	// Printable
	// ----------------------------------------------------------------------

	/**
	 * not from Printable interface, but related
	 */
	public void updatePageFormat() {
		PageFormat f = getPageFormat();
		columnRule.setActiveOffset((int) (f.getImageableX()));
		rowRule.setActiveOffset((int) (f.getImageableY()));
		columnRule.setActiveLength((int) (f.getImageableWidth()));
		rowRule.setActiveLength((int) (f.getImageableHeight()));
		if (isPageVisible()) {
			int w = (int) (f.getWidth());
			int h = (int) (f.getHeight());
			graph.setMinimumSize(new Dimension(w + 5, h + 5));
		} else
			graph.setMinimumSize(null);
		invalidate();
		// Execute fitAction...
		componentResized(null);
		graph.repaint();
	}

	public int print(Graphics g, PageFormat pF, int page) {
		int pw = (int) pF.getImageableWidth();
		int ph = (int) pF.getImageableHeight();
		int cols = (graph.getWidth() / pw) + 1;
		int rows = (graph.getHeight() / ph) + 1;
		int pageCount = cols * rows;
		if (page >= pageCount)
			return NO_SUCH_PAGE;
		int col = page % cols;
		int row = page % rows;
		g.translate(-col * pw, -row * ph);
		g.setClip(col * pw, row * ph, pw, ph);
		graph.paint(g);
		g.translate(col * pw, row * ph);
		return PAGE_EXISTS;
	}

	//
	// Listeners
	//

	// PropertyChangeListener
	public void propertyChange(PropertyChangeEvent evt) {
		if (getGraphpad() != null)
			update();
	}

	// GraphSelectionListener
	public void valueChanged(GraphSelectionEvent e) {
		update();
	}

	// View Observer
	public void graphLayoutCacheChanged(GraphLayoutCacheEvent e) {
		modified = true;
		update();
	}

	// GraphModelListener
	public void graphChanged(GraphModelEvent e) {
		modified = true;
		update();
		// System.out.println("Change:\n"+buttonEdge.getChange().getStoredAttributeMap());
	}

	protected void update() {
		updateFrameTitle();
		getGraphpad().update();
		getGraphpad().getAppStatus().setMessage(getDocumentStatus());
		getGraphpad().getAppStatus().setScale(this.getDocumentScale());
	}

	/* Return the status of this document as a string. */
	protected String getDocumentStatus() {
		String s = null;
		int n = graph.getSelectionCount();
		if (n > 0)
			s = n + " " + Translator.getString("Selected");
		else {
			int c = graph.getModel().getRootCount();
			if (c == 0) {
				s = Translator.getString("Empty");
			} else {
				s = c + " ";
				if (c > 1)
					s += Translator.getString("Cells");
				else
					s += Translator.getString("Cell");
				c = graph.getSelectionCount();
				s = s + " / " + c + " ";
				if (c > 1)
					s += Translator.getString("Components");
				else
					s += Translator.getString("Component");
			}
		}
		return s;
	}

	/**
	 * Returns the graphUndoManager.
	 * 
	 * @return GraphUndoManager
	 */
	public GraphUndoManager getGraphUndoManager() {
		return graphUndoManager;
	}

	/**
	 * Sets the graphUndoManager.
	 * 
	 * @param graphUndoManager
	 *            The graphUndoManager to set
	 */
	public void setGraphUndoManager(GraphUndoManager graphUndoManager) {
		this.graphUndoManager = graphUndoManager;
	}

	/**
	 * Resets the Graph undo manager
	 */
	public void resetGraphUndoManager() {
		graphUndoManager.discardAllEdits();
	}



	/**
	 * Returns true if the user really wants to close. Gives chance to save
	 * work.
	 */
	public boolean close(boolean showConfirmDialog) {
		// set default to save on close
		int r = JOptionPane.YES_OPTION;

		if (isModified()) {
			if (showConfirmDialog)
				r = JOptionPane.showConfirmDialog(getGraphpad().getFrame(), "'"
						+ getFileName() + "'"
						+ Translator.getString("GraphicSaveChangesDialog"),
						Translator.getString("Title"),
						JOptionPane.YES_NO_CANCEL_OPTION);

			// if yes, then save and close
			if (r == JOptionPane.YES_OPTION) {
				getGraphpad().getCommand("FileSave").actionPerformed(null);
				return true;
			}
			// if no, then don't save and just close
			else if (r == JOptionPane.NO_OPTION) {
				return true;
			}
			// all other conditions (cancel and dialog's 'X' button)
			// don't save and don't close
			else
				return false;
		}
		return true;
	}

	/**
	 * This will change the source of the actionevent to graph.
	 */
	protected class EventRedirector implements ActionListener {

		protected Action action;

		public EventRedirector(Action a) {
			this.action = a;
		}

		public void actionPerformed(ActionEvent e) {
			JComponent source = graph;
			e = new ActionEvent(source, e.getID(), e.getActionCommand(), e
					.getModifiers());
			action.actionPerformed(e);
		}

	}

	/**
	 * Returns the lastFound.
	 * 
	 * @return Object
	 */
	public Object getLastFound() {
		return lastFound;
	}

	/**
	 * Sets the lastFound.
	 * 
	 * @param lastFound
	 *            The lastFound to set
	 */
	public void setLastFound(Object lastFound) {
		this.lastFound = lastFound;
	}

	/**
	 * Returns the overviewDialog.
	 * 
	 * @return JDialog
	 */
	public JDialog getOverviewDialog() {
		return overviewDialog;
	}

	/**
	 * Sets the overviewDialog.
	 * 
	 * @param overviewDialog
	 *            The overviewDialog to set
	 */
	public void setOverviewDialog(JDialog overviewDialog) {
		this.overviewDialog = overviewDialog;
	}

	/**
	 * Returns the scrollPane.
	 * 
	 * @return JScrollPane
	 */
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * Sets the scrollPane.
	 * 
	 * @param scrollPane
	 *            The scrollPane to set
	 */
	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	/**
	 * Returns the columnRule.
	 * 
	 * @return Rule
	 */
	public Rule getColumnRule() {
		return columnRule;
	}

	/**
	 * Returns the rowRule.
	 * 
	 * @return Rule
	 */
	public Rule getRowRule() {
		return rowRule;
	}

	/**
	 * Sets the columnRule.
	 * 
	 * @param columnRule
	 *            The columnRule to set
	 */
	public void setColumnRule(Rule columnRule) {
		this.columnRule = columnRule;
	}

	/**
	 * Sets the rowRule.
	 * 
	 * @param rowRule
	 *            The rowRule to set
	 */
	public void setRowRule(Rule rowRule) {
		this.rowRule = rowRule;
	}

	/**
	 * Returns the enableTooltips.
	 * 
	 * @return boolean
	 */
	public boolean isEnableTooltips() {
		return enableTooltips;
	}

	/**
	 * Sets the enableTooltips.
	 * 
	 * @param enableTooltips
	 *            The enableTooltips to set
	 */
	public void setEnableTooltips(boolean enableTooltips) {
		this.enableTooltips = enableTooltips;

		if (this.enableTooltips)
			ToolTipManager.sharedInstance().registerComponent(graph);
		else
			ToolTipManager.sharedInstance().unregisterComponent(graph);
	}

	public String getFrameTitle() {
		return (this.isModified() ? "*" : "")
				+ (this.getName() == null ? Translator.getString("NewGraph")
						: this.getName());
	}



	public GPMarqueeHandler getMarqueeHandler() {
		return (GPMarqueeHandler) graph.getMarqueeHandler();
	}

	public void setMarqueeHandler(GPMarqueeHandler marqueeHandler) {
		graph.setMarqueeHandler(marqueeHandler);
	}

	public GPGraphpadFile getJGraphpadCEFile() {
		return jGraphpadCEFile;
	}

	public void setJGraphpadCEFile(GPGraphpadFile jGraphpadCEFile) {
		this.jGraphpadCEFile = jGraphpadCEFile;
		graph.setGraphLayoutCache(jGraphpadCEFile.getGraphLayoutCache());
	}

	public JPanel getOverviewPane() {
		return overviewPane;
	}

	public void setOverviewPane(JPanel overviewPane) {
		this.overviewPane = overviewPane;
	}

	public ArrayList getEdgeCreators() {
		return edgeCreators;
	}

	public void setEdgeCreators(ArrayList edgeCreators) {
		this.edgeCreators = edgeCreators;
	}

	public ArrayList getVertexnPortsCreators() {
		return vertexnPortsCreators;
	}

	public void setVertexnPortsCreators(ArrayList vertexnPortsCreators) {
		this.vertexnPortsCreators = vertexnPortsCreators;
	}

	public Action getCommand(String key) {
		return GPPluginInvoker.getCommand(key, getActionMap(), this);
	}

	public void initCommand(Action action) {
		getGraphpad().initCommand(action);
		if (action instanceof IVertexFactory) {
			vertexnPortsCreators.add(action);
		} else if (action instanceof IEdgeFactory) {
			edgeCreators.add(action);
		}
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Image backGroundImage) {
		this.backgroundImage = backGroundImage;
	}

	public boolean isPageVisible() {
		return pagevisible;
	}

	public void setPageVisible(boolean pagevisible) {
		this.pagevisible = pagevisible;
	}

	public PageFormat getPageFormat() {
		return pageFormat;
	}

	public void setPageFormat(PageFormat pageFormat) {
		this.pageFormat = pageFormat;
	}

	public Action getFitAction() {
		return fitAction;
	}

}
