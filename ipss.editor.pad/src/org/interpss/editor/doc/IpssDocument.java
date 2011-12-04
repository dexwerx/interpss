package org.interpss.editor.doc;

import javax.swing.JComponent;

import org.interpss.editor.data.proj.ProjData;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.data.IProjectData;
import org.interpss.editor.util.Utilities;
import org.interpss.spring.EditorSpringFactory;


public abstract class IpssDocument extends JComponent{
	 
    private IpssProject project;
    
	private IAppSimuContext appsimu = null;

	protected boolean modified = false;
	
	public IpssDocument() {
		super();
		this.appsimu = EditorSpringFactory.getAppSimuContext();
		appsimu.reset();
	}

	public void setProject(IpssProject project) {
		this.project = project;
	}

	public IpssProject getProject() {
		return project;
	}
	
	
    public String getFileName(){
    	return Utilities.getFileName(getName());
    }
    
	public void setSimuAppContext(IAppSimuContext aContext) {
		this.appsimu=aContext;
	}

    public IAppSimuContext getSimuAppContext() {
		return appsimu;
	}

	public IProjectData getProjData() {
		return appsimu.getProjData();
	}
	
	public void setProjData(ProjData info) {
		appsimu.setProjData(info);
	}
	
	// modified by Mike
	public boolean isModified() {
		return modified || appsimu.getProjData().isDirty();
	}
	
	public void setModified(boolean dirty) {
		this.modified = dirty;
	}
   
}
