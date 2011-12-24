 /*
  * @(#)ProjectFileUtil.java   
  *
  * Copyright (C) 2006 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 05/01/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.editor.app;

import java.io.File;

import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.coreframework.IpssTextDocument;
import org.interpss.editor.coreframework.IpssTextFile;
import org.interpss.editor.doc.IpssProjectItem;

import com.interpss.spring.CoreCommonSpringFactory;

public class ProjectFileUtil {
	public static String StdRunStudyCase = "RunStudyCase.xml";
	public static String StdRunStudyCase_Aclf = "RunStudyCaseStdAclf.xml";
	public static String StdRunStudyCase_Acsc = "RunStudyCaseStdAcsc.xml";
	public static String StdRunStudyCase_DStab = "RunStudyCaseStdDStab.xml";
//	public static String StdRunStudyCase_SenAnalysis = "RunStudyCaseStdSenAnalysis.xml";
//	public static String StdRunStudyCase_PTradingAnalysis = "RunStudyCasePTradingAnalysis.xml";
	
	public static String ScriptingRunStudyCase = "RunStudyCaseScripting.xml";

	public static String DStabPlotScriptFilename = "DStabPlotScripts.txt";
	public static String DStabOutputScriptFilename = "DStabOutputScripts.txt";
	
	public static IpssTextFile getProjectStdRunCaseFile(IpssEditorDocument doc, SimuRunEnum caseType) {
		if (caseType == SimuRunEnum.SenAnalysis ||
				caseType == SimuRunEnum.TradingAnalysis)
			return getProjectFile(doc, StdRunStudyCase);

		else if (caseType == SimuRunEnum.Aclf)
			return getProjectFile(doc, StdRunStudyCase_Aclf);
		else if (caseType == SimuRunEnum.Acsc)
			return getProjectFile(doc, StdRunStudyCase_Acsc);
		else if (caseType == SimuRunEnum.DStab)
			return getProjectFile(doc, StdRunStudyCase_DStab);
		else 
			return null;
	}
	
	public static IpssTextFile getProjectScriptRunCaseFile(IpssEditorDocument doc) {
		return getProjectFile(doc, ScriptingRunStudyCase);
	}

	/**
	 * Get a project file object. If the file does not exist, it will be created.
	 * 
	 * @param doc the project editor doc
	 * @param scriptFilename
	 * @return
	 */
	public static IpssTextFile getProjectFile(IpssEditorDocument doc, String scriptFilename) {
		IpssProjectItem item = doc.getGraphpad().getCurrentProjectItem();
		String filename = item.getFileNameNoExt()+scriptFilename;
		String filepath = item.getProject().getProjectPath() + System.getProperty("file.separator") + filename;
		File file = new File(filepath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				CoreCommonSpringFactory.getIpssMsgHub().sendErrorMsg("Cannot create DStab Scripting file: " + filepath);
				return null;
			}
			IpssTextFile ipssFile = new IpssTextFile(filepath);
			IpssTextDocument d = new IpssTextDocument(null, item.getProject(), filename, ipssFile);
			item.addDocument(d, 0);			
		}
		return new IpssTextFile(filepath);
	}
}
