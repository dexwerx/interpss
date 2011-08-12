 /*
  * @(#)AclfSampleTest.java   
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
  * @Date 07/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package com.interpss.pssl.test.dclf;

import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.plugin.IpssUtil;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;
import com.interpss.pssl.test.BaseTestSetup;

public class Dclf_Test extends BaseTestSetup {
	@Test
	public void dclfTest() {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net)
				.runDclfAnalysis();

		System.out.println(IpssUtil.outDclfResult(algoDsl)
				.toString());		
	}

	@Test
	public void lodfTest() {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net)
				.runDclfAnalysis();

		System.out.println("\nLODF Analysis");

		AclfBranch outageBranch = net.getAclfBranch("Bus4", "Bus7", "1");
		
		AclfBranch monitorBranch = net.getAclfBranch("Bus4", "Bus9", "1");
		double f1 = algoDsl.lineOutageDFactor(outageBranch, monitorBranch);
		System.out.println("LODF (4->7) -> (4->9): " + f1);

		monitorBranch = net.getAclfBranch("Bus5", "Bus6", "1");
		double f2 = algoDsl.lineOutageDFactor(outageBranch, monitorBranch);
		System.out.println("LODF (4->7) -> (5->6): " + f2);

		System.out.println(f1 + " + " + f2 + " = " + (f1+f2));
	}

	@Test
	public void n_1Test() {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net)
				.runDclfAnalysis();

		double flow_4_7 = algoDsl.branchFlow("Bus4", "Bus7", "1", UnitType.mW);
		double flow_4_9 = algoDsl.branchFlow("Bus4", "Bus9", "1", UnitType.mW);
		double flow_5_6 = algoDsl.branchFlow("Bus5", "Bus6", "1", UnitType.mW);
		
		AclfBranch outageBranch = net.getAclfBranch("Bus4", "Bus7", "1");
		
		AclfBranch monitorBranch = net.getAclfBranch("Bus4", "Bus9", "1");
		double f1 = algoDsl.lineOutageDFactor(outageBranch, monitorBranch);

		monitorBranch = net.getAclfBranch("Bus5", "Bus6", "1");
		double f2 = algoDsl.lineOutageDFactor(outageBranch, monitorBranch);

		System.out.println("\nN-1 Contingency Analysis");
		System.out.println("4->9 before:" + flow_4_9 + ", after: " + (flow_4_9+f1*flow_4_7));
		System.out.println("5->6 before:" + flow_5_6 + ", after: " + (flow_5_6+f2*flow_4_7));
	}
}

