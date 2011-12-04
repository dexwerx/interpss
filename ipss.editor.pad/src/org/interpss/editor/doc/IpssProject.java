package org.interpss.editor.doc;

import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.Utilities;
import org.interpss.spring.EditorSpringFactory;


 
public class IpssProject extends IpssProjectItemCollector{

//	private AppSimuContext appsimu = null;

	private String defaultFileName = "default.ipssp";

	private String parentPath = "";

	private String projectName;

	public IpssProject(String projectName, String filePath) {
		this.setParentPath(filePath);
		this.setProjectName(projectName);
//		this.appsimu = new AppSimuContext();
	}


	//	public AppSimuContext getSimuAppContext() {
//		return appsimu;
//	}
//
//	public ProjData getProjData() {
//		return appsimu.getProjData();
//	}
//
//	public void setProjData(ProjData info) {
//		appsimu.setProjData(info);
//	}

	public String getProjectPath() {
		return Utilities.getFilePathName(getParentPath(),getProjectName());
	}

	
	public String getProjectFilePathName() {
		return getProjectPath()+System.getProperty("file.separator")+defaultFileName;
	}

//	public IpssProjectItem getMainItem() {
//
//		if (projectItems != null && projectItems.size() > 0) {
//			for (int i = 0; i < projectItems.size(); i++) {
//				if (projectItems.get(i).getFileName().endsWith("ipss")) {
//					return projectItems.get(i);
//				}
//			}
//		}
//		return null;
//	}


	public IpssProject getProject()
	{
		return this;
	}
	
	public void setProjectName(String projectName) {
		if (projectName == null)
			this.projectName = Translator.getString("NewProject");
		else
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setParentPath(String filePath) {
		if (filePath == null)
			this.parentPath = EditorSpringFactory.getAppContext().getWorkspaceDir();
		else
			this.parentPath = filePath;
	}

	public String getParentPath() {
		return parentPath;
	}

	public String toString() {
		return getProjectName();
	}

}
