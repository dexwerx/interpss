package org.interpss.editor.coreframework;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.report.IpssRptViewer;
import org.interpss.report.IpssReportFactory;


public class IpssReportDocument extends IpssEditorDocument{
	
	/**
	 * Container for the graph so that you can scroll over the graph
	 */
	protected JScrollPane scrollPane;
	
//	private static JTextArea mainTextArea;
	
	protected IpssReportFile docFile;
	
	IpssRptViewer viewer;
	
	/**
	 * create a new report of type reportType
	 * 
	 * @param gp
	 * @param p
	 * @param name
	 * @param reportType
	 * @throws Exception
	 */
	public IpssReportDocument(GPGraphpad gp, IpssProject p, String name, String reportType) throws Exception {
		super();
		init(gp, p, name, null, reportType);
	}

	/**
	 * Load an existing report for the file
	 * 
	 * @param gp
	 * @param p
	 * @param name
	 * @param file
	 * @throws Exception
	 */
	public IpssReportDocument(GPGraphpad gp, IpssProject p, String name,IpssReportFile file) throws Exception {
		super();
		if (file == null) {
			throw new Exception("Programming error, report file is null");
		}
		init(gp, p, name, file, "Not Defined");
	}

	private void init(GPGraphpad gp, IpssProject p, String name,IpssReportFile file, String reportType) throws Exception {
		setDoubleBuffered(true);
		updateUI();
		
		this.setName(name);
		setGraphpad(gp);
		setProject(p);
		
		viewer = new IpssRptViewer();
		if (file != null) {
			this.docFile = file;
			viewer.loadReport(name);
		} else {
			this.docFile = new IpssReportFile(name);
		    viewer.loadReport(IpssReportFactory.createReport(reportType, GraphSpringFactory.getIpssGraphicEditor().getVersion()));
		}
		
		
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());
		
		scrollPane = new JScrollPane(viewer);
		this.add(BorderLayout.CENTER, scrollPane);
	}
	
	public IpssReportFile getDocFile() {
		return this.docFile;
	}
	
	public boolean isModified() {
		return this.docFile.isModified();
	}
	
	public void setModified(boolean dirty) {
		this.docFile.setModified(dirty);
	}
	
	/**
	 * return the main text area for text project
	 * 
	 * @return
	 */
	public IpssRptViewer getMainViewer() {
		return viewer;
	}
	
/////////////////////////
/////////////////////////
/////////////////////////
/////////////////////////
	public String getFrameTitle() {
//		return (this.getName() == null ? Translator.getString("NewGraph")
//				: this.getName())
//				+ (modified ? "*" : "");
		return "";
	}
	
	public boolean close(boolean showConfirmDialog){
		return true;
	}
}
