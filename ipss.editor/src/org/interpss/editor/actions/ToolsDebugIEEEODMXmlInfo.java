package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.ieeecdf.IeeeCDFAdapter;
import org.ieee.odm.adapter.ucte.UCTE_DEFAdapter;
import org.interpss.editor.coreframework.IpssAbstractActionDefault;
import org.interpss.editor.coreframework.IpssCustomDocument;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.spring.UISpringFactory;

import com.interpss.spring.CoreCommonSpringFactory;


public class ToolsDebugIEEEODMXmlInfo extends IpssAbstractActionDefault {
	private static final long serialVersionUID = 1;
   
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		IpssEditorDocument doc = getCurrentDocument();
		String filepath = graphpad.getCurrentProject().getProjectPath() + "/" +	((IpssCustomDocument)doc).getFileName();
		String str = "IEEE ODM Adapter not implemented";
		IODMAdapter adapter = null;
		if (doc.getFileName().endsWith(".uct")) {
			adapter = new UCTE_DEFAdapter();
		}
		else if (doc.getFileName().endsWith(".ieee")) {
			adapter = new IeeeCDFAdapter();
		}
		if (adapter != null) {
			if (adapter.parseInputFile(filepath)) {
				str = adapter.getModel().toString();
			}
			else
			CoreCommonSpringFactory.getIpssMsgHub().sendErrorMsg("Error: model parsing error, " + adapter.errMessage());
		}
		IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("IEEE ODM Model Xml Document");
  		dialog.display(str);		
	}

	public void update() {
		IpssEditorDocument doc = getCurrentDocument();
		setEnabled(doc instanceof IpssCustomDocument &&
				(doc.getFileName().endsWith(".uct") ||
						doc.getFileName().endsWith(".ieee")));
	}
}
