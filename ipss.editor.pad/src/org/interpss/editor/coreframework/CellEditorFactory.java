package org.interpss.editor.coreframework;

/**
 * 	Factory class for creating Branch, Bus and Network editor
 */

import java.awt.Dialog;

import javax.swing.JDialog;

import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.jgraph.cells.BranchEdge;
import org.interpss.editor.jgraph.cells.BusCell;
import org.interpss.editor.ui.edit.NBBranchEditDialog;
import org.interpss.editor.ui.edit.NBBusEditDialog;
import org.interpss.editor.ui.edit.NBProjectEditDialog;

import com.interpss.spring.CoreCommonSpringFactory;

public class CellEditorFactory {
	public final static byte EditorType_Normal = 0,
			EditorType_FindDistObject = 1, EditorType_FindTransObject = 2;

	/**
	 * Create a cell editor according to the cell type
	 * 
	 * @param cell
	 *            the cell object, null object for create network editor
	 * @param graph
	 *            the IpssGraph object
	 * @return the created cell editor
	 */
	public static Dialog getEditorDialog(java.awt.Frame parentFrame,
			Object obj, GPDocument doc, byte type) {
		JDialog dialog = null;
		switch (type) {
		case EditorType_Normal:
			if ((obj instanceof BusCell) || (obj instanceof GBusForm)) {
				NBBusEditDialog busEditor = new NBBusEditDialog(parentFrame,
						CoreCommonSpringFactory.getIpssMsgHub());

				if (obj instanceof BusCell) {
					// This is called when a new bus is added
					GBusForm form = (GBusForm) (((BusCell) obj).getUserObject());

					busEditor.init(doc.getGFormContainer(), form);
				} else if (obj instanceof GBusForm) {
					// This is called when editing an existng bus
					busEditor.init(doc.getGFormContainer(), obj);
				}
				return busEditor;
			} else if ((obj instanceof BranchEdge)
					|| (obj instanceof GBranchForm)) {
				NBBranchEditDialog branchEditor = new NBBranchEditDialog(
						parentFrame, CoreCommonSpringFactory.getIpssMsgHub());
				if (obj instanceof BranchEdge) {
					GBranchForm form = (GBranchForm) (((BranchEdge) obj)
							.getUserObject());
					branchEditor.init(doc.getGFormContainer(), form);
				} else if (obj instanceof GBranchForm) {
					branchEditor.init(doc.getGFormContainer(), obj);
				}
				return branchEditor;
			} else if (obj == null) { // this is the case of NetEditor
				NBProjectEditDialog netEditor = new NBProjectEditDialog(
						parentFrame, CoreCommonSpringFactory.getIpssMsgHub());
				netEditor.init(doc.getGFormContainer(), doc.getProjData());
				return netEditor;
			}
			break;
/*			
		case EditorType_FindDistObject:
			NBSearchElementDialog findObjDialog = new NBSearchElementDialog(parentFrame);
			findObjDialog.init(doc.getGFormContainer(), null);
			return findObjDialog;
		default:
			dialog = new JDialog(UIConfig.getRootFrame(), "Programming error");
			dialog.pack();
			dialog.setVisible(true);
*/			
		}
		return dialog;

	}

	public static Dialog getEditorDialog(java.awt.Frame parentFrame,
			Object obj, GPDocument doc) {

		return getEditorDialog(parentFrame, obj, doc, EditorType_Normal);
	}
}