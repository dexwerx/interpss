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

import static com.interpss.core.dclf.funcImpl.DclfFunction.DclfBranchAptr;
import static org.junit.Assert.assertTrue;

import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.NumericUtil;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.dclf.LODFSenAnalysisType;
import com.interpss.core.dclf.common.OutageConnectivityException;
import com.interpss.core.dclf.common.ReferenceBusException;
import com.interpss.core.net.Branch;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.simu.IpssDclf.DclfAlgorithmDSL;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.test.BaseTestSetup;

public class DclfLODFPaper_Test extends BaseTestSetup {
	//@Test  This method does not work
	public void lodfTest_Ipss()  throws ReferenceBusException, OutageConnectivityException, InterpssException   {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net)
										.runDclfAnalysis();
		

		algoDsl.setLODFAnalysisType(LODFSenAnalysisType.MULTI_BRANCH)
				.addOutageBranch("Bus1", "Bus5", "1")
				.addOutageBranch("Bus3", "Bus4", "1")
				.addOutageBranch("Bus6", "Bus11", "1");

		algoDsl.setRefBus("Bus14");
		
		AclfBranch monBranch = net.getAclfBranch("Bus2", "Bus5", "1");

		double sum = 0.0;
		for (Branch bra : algoDsl.outageBranchList()) {
			AclfBranch aclfBra = (AclfBranch)bra;
			double flow = aclfBra.getDclfFlow();
			double gsfFrom = algoDsl.genShiftFactor(aclfBra.getFromBusId(), monBranch);
			double gsfTo = algoDsl.genShiftFactor(aclfBra.getToBusId(), monBranch);
			sum += flow * (gsfFrom - gsfTo);
		}
		System.out.println("Shifted power flow: " + sum);
	}
	
	@Test
	public void lodfTest1()  throws ReferenceBusException, OutageConnectivityException, InterpssException   {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net)
										.runDclfAnalysis();
		
//		// make sure get branch power flow before set the RefBus
//		for (Branch bra : net.getBranchList()) {
//			//System.out.println(bra.getId());
//			AclfBranch aclfBra = (AclfBranch)bra;
//			aclfBra.setWeight(algoDsl.branchFlow(aclfBra, UnitType.PU));
//		}

		algoDsl.setLODFAnalysisType(LODFSenAnalysisType.MULTI_BRANCH)
				.addOutageBranch("Bus1", "Bus5", "1")
				.addOutageBranch("Bus3", "Bus4", "1")
				.addOutageBranch("Bus6", "Bus11", "1");

		algoDsl.setRefBus("Bus14");
		
		algoDsl.calLineOutageDFactors();
		
		double[] factors = algoDsl.monitorBranch("Bus2", "Bus5", "1")
								  .getLineOutageDFactors();
		double sum = 0.0;
		int cnt = 0;
		for (Branch bra : algoDsl.outageBranchList()) {
			//System.out.println(bra.getId());
			AclfBranch aclfBra = (AclfBranch)bra;
			double flow = aclfBra.getDclfFlow();
			sum += flow * factors[cnt++];
		}
		System.out.println("Shifted power flow: " + sum);
		//System.out.println("Total power flow: " + (sum+algoDsl.getMontorBranch().getWeight()));
		//	Shifted power flow: 0.28184073631614476
		//Total power flow: 0.6908804780716143
		double f = DclfBranchAptr.f(algoDsl.getMontorBranch()).getPowerFlow(UnitType.PU);
		assertTrue(NumericUtil.equals(sum+f, 0.690881, 0.00001));
		//System.out.println(new Array2DRowRealMatrix(factors));
		// {{0.5551262632496149},{0.4511165014022788},{-0.06373460005412564}}
		assertTrue(NumericUtil.equals(factors[0], 0.555126, 0.00001));
		assertTrue(NumericUtil.equals(factors[1], 0.451117, 0.00001));
		assertTrue(NumericUtil.equals(factors[2],-0.063735, 0.00001));

		factors = algoDsl.monitorBranch("Bus6", "Bus13", "1")
		  				.getLineOutageDFactors();
		sum = 0.0;
		cnt = 0;
		for (Branch bra : algoDsl.outageBranchList()) {
			//System.out.println(bra.getId());
			AclfBranch aclfBra = (AclfBranch)bra;
			double flow = aclfBra.getDclfFlow();
			sum += flow * factors[cnt++];
		}
		//System.out.println("Shifted power flow: " + sum);
		//Shifted power flow: 0.00846884256008724
		//Total power flow: 0.17880576075139853		
		assertTrue(NumericUtil.equals(sum+algoDsl.getMontorBranch().getDclfFlow(), 0.178806, 0.00001));
		//System.out.println(new Array2DRowRealMatrix(factors));
		// {{-0.011974841796572601},{0.012142880754380832},{0.31591615777672694}}
		assertTrue(NumericUtil.equals(factors[0],-0.011975, 0.00001));
		assertTrue(NumericUtil.equals(factors[1], 0.012149, 0.00001));
		assertTrue(NumericUtil.equals(factors[2], 0.315916, 0.00001));		
	}
	
	@Test
	public void lodfTest2()  throws ReferenceBusException, InterpssException  {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net)
										.setRefBus("Bus14");

		algoDsl.injectionBusId("Bus1").withdrawBusId("Bus5");
		System.out.println("\nInj - Bus1, withdraw - Bus5");
	
		System.out.println("PTDF (1->5): " + 
				algoDsl.monitorBranch("Bus1", "Bus5", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (3->4): " + 
				algoDsl.monitorBranch("Bus3", "Bus4", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (6->11): " + 
				algoDsl.monitorBranch("Bus6", "Bus11", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (2->5): " + 
				algoDsl.monitorBranch("Bus2", "Bus5", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (6->13): " + 
				algoDsl.monitorBranch("Bus6", "Bus13", "1")
				       .pTransferDFactor());

		algoDsl.injectionBusId("Bus3").withdrawBusId("Bus4");
		System.out.println("\nInj - Bus3, withdraw - Bus4");
		
		System.out.println("PTDF (1->5): " + 
				algoDsl.monitorBranch("Bus1", "Bus5", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (3->4): " + 
				algoDsl.monitorBranch("Bus3", "Bus4", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (6->11): " + 
				algoDsl.monitorBranch("Bus6", "Bus11", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (2->5): " + 
				algoDsl.monitorBranch("Bus2", "Bus5", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (6->13): " + 
				algoDsl.monitorBranch("Bus6", "Bus13", "1")
				       .pTransferDFactor());
		
		algoDsl.injectionBusId("Bus6").withdrawBusId("Bus11");
		System.out.println("\nInj - Bus6, withdraw - Bus11");
		
		System.out.println("PTDF (1->5): " + 
				algoDsl.monitorBranch("Bus1", "Bus5", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (3->4): " + 
				algoDsl.monitorBranch("Bus3", "Bus4", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (6->11): " + 
				algoDsl.monitorBranch("Bus6", "Bus11", "1")
				       .pTransferDFactor());
		
		System.out.println("PTDF (2->5): " + 
				algoDsl.monitorBranch("Bus2", "Bus5", "1")
				       .pTransferDFactor());

		System.out.println("PTDF (6->13): " + 
				algoDsl.monitorBranch("Bus6", "Bus13", "1")
				       .pTransferDFactor());	
	}
}

