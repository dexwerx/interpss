 /*
  * @(#)IAppSimuContext.java   
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
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.editor.jgraph.ui.app;

import javax.swing.JPopupMenu;

import org.interpss.db.IpssDBCase;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.jgraph.ui.data.IProjectData;

public interface IAppSimuContext {
	//public static enum CaseType { Aclf, Acsc, DStab, Scripts, SenAnalysis, NotDefined };

	void reset();
	void releaseResource();
	
	Object getSimuCtx();
	void setSimuCtx(Object ctx);
	boolean isSimuNetDataDirty();
	void setSimuNetDataDirty(boolean b);
	
	IProjectData getProjData();
	void setProjData(IProjectData info);
	
	boolean isLfConverged();
	void setLfConverged(boolean b);

	boolean isScCalculated();
	void setScCalculated(boolean scCalculated);
   
	String getCurrentCaseName(SimuRunEnum caseType);

	SimuRunEnum getLastRunType();
	void setLastRunType(SimuRunEnum lastRunType);	
	boolean hasLastRun();	
	Object getDStabRunForm();

	/**
	 * Get the current DStab simu case DB id 
	 *  
	 * @return
	 */
	int getDbSimuCaseId();
	
	/**
	 * Set the current DStab simu case DB id
	 * 
	 * @param n db id
	 */
	void setDbSimuCaseId(int n);
	
	/**
	 * Check if the current AcscRunForm has a non-symmetric fault
	 * 
	 * @return
	 */
	boolean isNonSymmetricFault();
	
	void addPopupMenuAction(JPopupMenu menu, final Object cell);
	
	public IpssDBCase getCaseData(String casename, SimuRunEnum caseType);
}