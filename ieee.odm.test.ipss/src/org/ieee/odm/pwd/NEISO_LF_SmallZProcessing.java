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
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.numeric.datatype.ComplexFunc;
import org.interpss.numeric.sparse.base.SparseEquation.SolverType;
import org.interpss.numeric.util.Number2String;
import org.interpss.numeric.util.NumericUtil;
import org.interpss.util.FileUtil;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import org.interpss.algo.ZeroZBranchProcesor;

public class NEISO_LF_SmallZProcessing {
	public static void main(String args[]) throws InterpssException {
		IpssCorePlugin.init();
        IpssCorePlugin.setSparseEqnSolver(SolverType.Native);
		ODMLogger.getLogger().setLevel(Level.ALL);
		AclfNetwork net = CorePluginObjFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.PWD)
				.load("testData/pwd/neiso_test.aux")
				.getAclfNet();
		
		for(Bus b:net.getBusList())if(((AclfBus)b).isSwing()){
			System.out.println("Swing Bus: "+b.getId() +", name:"+ b.getName());
		}
		
		System.out.println("No of buses: " + net.getNoBus() + ", branches: " + net.getNoBranch());
		
		
		for(Bus b:net.getBusList())if(b.isIslandBus()){
			System.out.println("isolated Bus: "+b.getId() +", name:"+ b.getName());
			b.setStatus(false);
		}

		double smallBranchZ = 0.00001;
	  	net.accept(new ZeroZBranchProcesor(smallBranchZ));
		System.out.println("net zero branch processed:"+net.isZeroZBranchProcessed());
		
		//check small impedance branch
		String smallBra="";
		for(Branch bra:net.getBranchList()){
			if(bra.isActive()&&((AclfBranch)bra).getZ().abs()<=0.00001)
				smallBra+="small impedance branch: "+bra.getId() +", Z:"+ Number2String.toStr(((AclfBranch)bra).getZ())+"\n";
		}
		FileUtil.writeText2File("testdata/neisoSmallBranch.txt", smallBra);
		System.out.println("data check :"+net.checkData(CoreObjectFactory.createDefultDataCheckConfiguration()));

		
		
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		
		net.initializeBusVoltage();
		/*
		net.setBusNumberArranged(true);
		int sortNum=0;
		for(Bus b:net.getBusList()){
			b.setSortNumber(sortNum++);
		}
        */
        //FileUtil.writeText2File("testdata/neisoJmatrix.mtx", net.formJMatrix().toString());
		algo.setLfMethod(AclfMethod.PQ);
        //algo.loadflow();		
		//System.out.println("Bus 2952 sortNum:" +net.getAclfBus("Bus2952").getSortNumber());
	}	
}

