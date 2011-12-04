package org.interpss.db;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginTestSetup;
import org.interpss.editor.io.ProjectDataDBManager;
import org.interpss.editor.jgraph.ui.data.IProjectData;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.ui.Workspace;
import org.junit.Test;

public class DbUtilTools  extends PluginTestSetup {
	public void createProjectId(String filename, String projName) {
		Workspace.setCurrentType(Workspace.Type.Sample);
		
		ProjectDataDBManager manager = (ProjectDataDBManager)PluginSpringFactory.getProjectDataDBManager();
		int projDbId = 0;
		IProjectData projData = manager.loadProjectDataFromDB(projDbId, filename, projName);
		assertTrue(projData.getProjectDbId() > 0);
		System.out.println("ProjDbId, Filename, ProjectName: " + projData.getProjectDbId() + ", " + filename + ", " + projName);
	}
	
	@Test
	public void createProjDbId() {
//		createProjectId("testData/dstab_test/DStab-5Bus.ipss", "DStab-5Bus");
		// ProjDbId, Filename, ProjectName: 83, testData/dstab_test/DStab-5Bus.ipss, DStab-5Bus
	}
}
