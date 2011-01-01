package org.interpss.editor.doc;

import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.Utilities;



public class IpssProjectItem extends IpssProjectItemCollector{
    
    private IpssProject project;
    
    private int projDbId;
    
	private IpssDocument document = null;
	
	private String name = Translator.getString("NewGraph");
	
	private String init_Status = STATUS_CLOSE;

	public static final String STATUS_OPEN="open",STATUS_ACTIVE="active",STATUS_CLOSE="close";
	

//	public IpssProjectItem(IpssProject project,int projDbId){
//		setProject(project);
//		setProjDbId(projDbId);
//	}
	
	public IpssProjectItem(IpssProject project,int projDbId,IpssDocument d){
		setDocument(d);
		setProject(project);
		setProjDbId(projDbId);
//		setCurr_Status(STATUS_OPEN);
	}
	
//no doc object
	public IpssProjectItem(IpssProject project,int projDbId,String name,String status){
		setProject(project);
		setProjDbId(projDbId);
		setName(name);
		setInit_Status(status);
	}
	
	public void setProject(IpssProject project) {
		this.project = project;
	}

	public IpssProject getProject() {
		return project;
		//return getDocument().getProject();
	}

	public String getFileName() {
		return Utilities.getFileName(getName());
	}

	public String getFileNameNoExt() {
		return org.interpss.editor.util.Utilities.getFileNameNoExt(getFileName());
	}
	
	public String getFileExt() {
		return org.interpss.editor.util.Utilities.getFileExt(getFileName());
	}
	
	
	
	public void setName(String name) {
		this.name = name;
		if (this.isLoaded()) this.getDocument().setName(name);
	}

	public String getName() {
//		if (isLoaded() && (document.getName()!=null)) return document.getName();
		return name;
	}

	public void setDocument(IpssDocument document) {
		this.document = document;
		this.name = document.getName();
		if (document.getSimuAppContext() != null) {
			document.getSimuAppContext().getProjData().setProjectDbId(projDbId);
		}
	}

	public IpssDocument getDocument() {
		return document;
	}

	public void setProjDbId(int projDbId) {
		this.projDbId = projDbId;
		// we set projDbId to the projData object
		if (document != null) { 
			document.getSimuAppContext().getProjData().setProjectDbId(projDbId);
		}
	}

	public int getProjDbId() {
		return projDbId;
	}

	public void setInit_Status(String curr_Status) {
		this.init_Status = curr_Status;
	}

	public String getInit_Status(){
		return init_Status;
	}

	public boolean isOpen() {
		return (getInit_Status().equals(STATUS_OPEN) || getInit_Status().equals(STATUS_ACTIVE)) ;
	}
	
	public boolean isActive() {
		return getInit_Status().equals(STATUS_ACTIVE) ;
	}

	
	public boolean isLoaded() {
		return !(getDocument() == null) ;
	}
	
	//*****must inh
	public boolean isMain() {
		return true;
	}
	
	public void clearDocument() {
		this.document = null;
	}
	
	public String toString() {
		return getFileName();
	}
}
