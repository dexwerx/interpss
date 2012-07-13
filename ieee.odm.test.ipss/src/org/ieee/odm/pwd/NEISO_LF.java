 /*
  * @(#)CR_UserTestCases.java   
  *
  * Copyright (C) 2008 www.interpss.org
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
  * @Date 02/15/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.pwd;

import java.util.logging.Level;

import org.ieee.odm.common.ODMLogger;
import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.numeric.sparse.base.SparseEquation.SolverType;
import org.interpss.util.FileUtil;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;

public class NEISO_LF {
	public static void main(String args[]) throws InterpssException {
		IpssCorePlugin.init();
        IpssCorePlugin.setSparseEqnSolver(SolverType.Native);
		ODMLogger.getLogger().setLevel(Level.WARNING);

		AclfNetwork net = CorePluginObjFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.PWD)
				.load("testData/pwd/neiso_test.aux")
				.getAclfNet();
		
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		
        //FileUtil.writeText2File("testdata/neisoJmatrix.mtx", net.formJMatrix().toString());
        algo.loadflow();		
        
        FileUtil.writeText2File("testdata/neiso.txt", 
        		AclfOutFunc.loadFlowSummary(net).toString());   
	}	
}

