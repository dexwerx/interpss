package org.interpss.editor.coreframework.jgraphsubclassers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.TransferHandler;

import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.cells.AnnotateLabelCell;
import org.interpss.editor.jgraph.cells.BranchEdge;
import org.interpss.editor.jgraph.cells.BusCell;
import org.interpss.editor.jgraph.cells.LabelCell;
import org.interpss.editor.jgraph.cells.SimpleLabelCell;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.ParentMap;
import org.jgraph.plaf.basic.BasicGraphUI;

import com.interpss.common.util.IpssLogger;

/**
 * The base class JGraph BasicGraphUI subclasser for GPGraphpad. It allows to
 * render backround pictures and handles the copy/paste outside from GPGraphpad.
 */
public class GPGraphUI extends BasicGraphUI {

	GPDocument document;

	public void setDocument(GPDocument document) {
		this.document = document;
	}

	public GPGraphUI() {
		super();
	}

	public CellHandle createHandle(GraphContext context) {
		if (context != null && !context.isEmpty() && graph.isEnabled()) {
			try {
				return new RootHandle(context) {

					public void mouseReleased(MouseEvent event) {
						try {
							if (event != null && !event.isConsumed()) {
								if (activeHandle != null) {
									activeHandle.mouseReleased(event);
									activeHandle = null;
								} else if (isMoving
										&& !event.getPoint().equals(start)) {
									if (cachedBounds != null) {
										double dx = event.getX() - start.getX();
										double dy = event.getY() - start.getY();
										Point2D tmp = graph
												.fromScreen(new Point2D.Double(
														dx, dy));
										GraphLayoutCache.translateViews(views,
												tmp.getX(), tmp.getY());
									}
									CellView[] all = graphLayoutCache
											.getAllDescendants(views);
									Map attributes = GraphConstants
											.createAttributes(all, null);
									if (event.isControlDown()
											&& graph.isCloneable()) { // Clone
										// Cells
										Object[] cells = graph
												.getDescendants(graph
														.order(context
																.getCells()));
										// Include properties from hidden cells
										Map hiddenMapping = graphLayoutCache
												.getHiddenMapping();
										for (int i = 0; i < cells.length; i++) {
											Object witness = attributes
													.get(cells[i]);
											if (witness == null) {
												CellView view = (CellView) hiddenMapping
														.get(cells[i]);
												if (view != null
														&& !graphModel
																.isPort(view
																		.getCell())) {
													// TODO: Clone required?
													// Same in
													// GraphConstants.
													AttributeMap attrs = (AttributeMap) view
															.getAllAttributes()
															.clone();
													// Maybe translate?
													// attrs.translate(dx, dy);
													attributes.put(cells[i],
															attrs.clone());
												}
											}
										}
										ConnectionSet cs = ConnectionSet
												.create(graphModel, cells,
														false);
										ParentMap pm = ParentMap.create(
												graphModel, cells, false, true);
										cells = graphLayoutCache.insertClones(
												cells, graph.cloneCells(cells), attributes, cs,
												pm, 0, 0);

										for (int i = 0; i < cells.length; i++) {
											if (cells[i] instanceof BusCell) {
												BusCell buscell = (BusCell) cells[i];
												buscell
														.set_labelAnnotate(new AnnotateLabelCell(
																buscell,
																buscell
																		.getUserObject()));
												buscell
														.setLabel(new LabelCell(
																buscell,
																buscell
																		.getUserObject()));

												Rectangle2D bounds = GraphConstants
														.getBounds(buscell
																.getAttributes());

												buscell.insertLabel(graph,
														bounds);
												buscell.insertLabelAnnotate(
														graph, bounds);
											}
										}
									} else if (graph.isMoveable()) { // Move
										// Cells
										ParentMap pm = null;

										// Moves into group
										if (targetGroup != null) {
											pm = new ParentMap(context
													.getCells(), targetGroup
													.getCell());
										} else if (graph.isMoveOutOfGroups()
												&& (ignoreTargetGroup != null && !ignoreTargetGroup
														.getBounds()
														.intersects(
																AbstractCellView
																		.getBounds(views)))) {
											pm = new ParentMap(context
													.getCells(), null);
										}
										graph.getGraphLayoutCache().edit(
												attributes, disconnect, pm,
												null);
									}
									event.consume();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ignoreTargetGroup = null;
							targetGroup = null;
							initialLocation = null;
							isDragging = false;
							disconnect = null;
							offscreen = null;
							firstDrag = true;
							start = null;
						}
					}

				};
			} catch (NullPointerException e) {
				// ignore for now...
			}
		}
		return null;
	}

	protected TransferHandler createTransferHandler() {
		return new GPTransferHandler();
	}

	/**
	 * Paint the background of this graph. Calls paintGrid.
	 */
	protected void paintBackground(Graphics g) {
		Rectangle pageBounds = graph.getBounds();
		if (document == null)
			return;
		if (document.getBackgroundImage() != null) {
			// Use clip and pageBounds
			double s = graph.getScale();
			Graphics2D g2 = (Graphics2D) g;
			AffineTransform tmp = g2.getTransform();
			g2.scale(s, s);
			g.drawImage(document.getBackgroundImage(), 0, 0, graph);
			g2.setTransform(tmp);
		} else if (document.isPageVisible()) { // FIX: Use clip
			int w = (int) (document.getPageFormat().getWidth());
			int h = (int) (document.getPageFormat().getHeight());
			Point2D p = graph.toScreen(new Point(w, h));
			w = (int) p.getX();
			h = (int) p.getY();
			g.setColor(graph.getHandleColor());
			g.fillRect(0, 0, graph.getWidth(), graph.getHeight());
			g.setColor(Color.darkGray);
			g.fillRect(3, 3, w, h);
			g.setColor(graph.getBackground());
			g.fillRect(1, 1, w - 1, h - 1);
			pageBounds = new Rectangle(0, 0, w, h);
		}
		if (graph.isGridVisible())
			paintGrid(graph.getGridSize(), g, pageBounds);
	}

	protected boolean startEditing(Object cell, MouseEvent event) {
		completeEditing();

		if (cell instanceof SimpleLabelCell) {
			return super.startEditing(cell, event);
		} else if (cell instanceof LabelCell) {
			LabelCell lcell = (LabelCell) cell;
			if (lcell.getParentCell() instanceof BusCell
					|| lcell.getParentCell() instanceof BranchEdge) {
				IpssLogger
						.getLogger()
						.info(
								"Starting Editing a Bus/Branch object and launch Bus Dialog");
				GraphSpringFactory.getEditorDialog(lcell.getParentCell(),
						document.getGraph());
				graph.clearSelection();
				return true;
			}
		} else if (cell instanceof BusCell || cell instanceof BranchEdge) {
			IpssLogger
					.getLogger()
					.info(
							"Starting Editing a Bus/Branch object and launch Bus/BranchEditor Dialog");
			GraphSpringFactory.getEditorDialog(cell, document.getGraph());
			graph.clearSelection();
			return true;
		}

		return false;
	}

	public GPDocument getDocument() {
		return document;
	}
}
