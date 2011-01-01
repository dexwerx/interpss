package org.interpss.editor.coreframework;


public class IpssReportFile {
//	private IAppSimuContext appSimuContext;
	private String filePathName;
// Mike
	/** 
	 * True if this documents graph model was modified since last save.
	 */
	protected boolean modified = false;

	public IpssReportFile(String filePath) {
		setFilePathName(filePath);
//		setModified(false);
	}
//	public void setSimuAppContext(IAppSimuContext appSimuContext) {
//		this.appSimuContext = appSimuContext;
//	}
//
//	public IAppSimuContext getSimuAppContext() {
//		return appSimuContext;
//	}

	public void setFilePathName(String filePathName) {
		this.filePathName = filePathName;
	}

	public String getFilePathName() {
		return filePathName;
	}

	/**
	 * @return the dirty
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @param dirty the dirty to set
	 */
	public void setModified(boolean dirty) {
		this.modified = dirty;
	}
}
