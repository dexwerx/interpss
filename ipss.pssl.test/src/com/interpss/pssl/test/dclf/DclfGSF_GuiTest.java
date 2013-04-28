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

import org.interpss.numeric.exp.IpssNumericException;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.dclf.LODFSenAnalysisType;
import com.interpss.core.dclf.common.OutageConnectivityException;
import com.interpss.core.dclf.common.ReferenceBusException;
import com.interpss.pssl.common.PSSLException;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.simu.IpssDclf.DclfAlgorithmDSL;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.test.BaseTestSetup;

public class DclfGSF_GuiTest extends BaseTestSetup {
	@Test
	public void gsfTest() throws ReferenceBusException, InterpssException  {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
			   .withdrawBusId("Bus14");
		System.out.println("\nInj - Bus2, withdraw - Bus14");
		
		double f = algoDsl.monitorBranch("Bus4", "Bus9")
		   					.genShiftFactor();
		System.out.println("monitorBranch - 4->9");
		System.out.println("GSF: " + f );	

		f = algoDsl.monitorBranch("Bus4", "Bus7")
			.genShiftFactor();
		System.out.println("monitorBranch - 4->7");
		System.out.println("GSF: " + f );	
		
		f = algoDsl.monitorBranch("Bus5", "Bus6")
			.genShiftFactor();
		System.out.println("monitorBranch - 5->6");
		System.out.println("GSF: " + f );	

		algoDsl.injectionBusId("Bus1")
			.setRefBus("Bus10");
		System.out.println("\nInj - Bus1, withdraw - Bus14");
		
		f = algoDsl.monitorBranch("Bus4", "Bus9")
			.genShiftFactor();
		System.out.println("monitorBranch - 4->9");
		System.out.println("GSF: " + f );	

		f = algoDsl.monitorBranch("Bus4", "Bus7")
			.genShiftFactor();
		System.out.println("monitorBranch - 4->7");
		System.out.println("GSF: " + f );	

		f = algoDsl.monitorBranch("Bus5", "Bus6")
			.genShiftFactor();
		System.out.println("monitorBranch - 5->6");
		System.out.println("GSF: " + f );	
	}
	
	@Test
	public void lodfTest() throws PSSLException, ReferenceBusException, InterpssException  {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net);

		algoDsl.outageBranch("Bus4", "Bus7", "1");
		
		double f = algoDsl.monitorBranch("Bus4", "Bus9", "1")
	       				.lineOutageDFactor();
		System.out.println("LODF (x4->7) -> (4->9): " + f);

		f = algoDsl.monitorBranch("Bus5", "Bus6", "1")
	       			.lineOutageDFactor();
	    System.out.println("LODF (x4->7) -> (5->6): " + f );
	}
	
	@Test
	public void lodfTest1()  throws ReferenceBusException, OutageConnectivityException, InterpssException, IpssNumericException   {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net);

		algoDsl.setLODFAnalysisType(LODFSenAnalysisType.MULTI_BRANCH)
				.addOutageBranch("Bus1", "Bus5", "1")
				.addOutageBranch("Bus3", "Bus4", "1")
				.addOutageBranch("Bus6", "Bus11", "1");

		algoDsl.setRefBus("Bus14");
		
		algoDsl.calLineOutageDFactors("ContId");
		
		double[] factors = algoDsl.monitorBranch("Bus2", "Bus5", "1")
								  .getLineOutageDFactors();
	    for ( double x : factors)
	    	System.out.println( x );
	}
}

