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

import static org.junit.Assert.assertTrue;

import org.interpss.display.AclfOutFunc;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.algo.path.LossAllocationAlgorithm;
import com.interpss.core.algo.path.NetPathWalkDirectionEnum;
import com.interpss.core.dclf.BusSenAnalysisType;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;
import com.interpss.pssl.test.BaseTestSetup;

public class DclfLoss_Test extends BaseTestSetup {
	@Test
	public void lossAllocationStep() throws Exception {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.NR);
	  	algo.setNonDivergent(true);
  		assertTrue(algo.loadflow());

  		LossAllocationAlgorithm lossAlgo = CoreObjectFactory.createLossAllocationAlgorithm();
  		assertTrue(!lossAlgo.hasActivePowerLoop(net));
  		
  		lossAlgo.setDirection(NetPathWalkDirectionEnum.ALONG_PATH); 
  		net.accept(lossAlgo);
		System.out.println(AclfOutFunc.loadLossAllocation(net));

  		lossAlgo.setDirection(NetPathWalkDirectionEnum.OPPOSITE_PATH); 
  		net.accept(lossAlgo);
		System.out.println(AclfOutFunc.genLossAllocation(net));
	}		

	@Test
	public void lossFactor() throws Exception {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		

	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.NR);
  		assertTrue(algo.loadflow());
  		
		System.out.println("Inj - Bus2, withdraw - Bus3");
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net);
		double f = algoDsl.getAlgorithm().lossFactor("Bus2", "Bus3");
		System.out.println("LossFactor: " + f);

		System.out.println("Inj - Bus2, withdraw - Bus3 100%");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS);
		algoDsl.addWithdrawBus("Bus3", 100.0);
		f = algoDsl.getAlgorithm().lossFactor("Bus2");
		System.out.println("LossFactor: " + f);

		System.out.println("Inj - Bus2, withdraw - Bus14");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		f = algoDsl.getAlgorithm().lossFactor("Bus2", "Bus14");
		System.out.println("LossFactor: " + f);

		System.out.println("Inj - Bus2, withdraw - Bus13");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		f = algoDsl.getAlgorithm().lossFactor("Bus2", "Bus13");
		System.out.println("LossFactor: " + f);

		System.out.println("Inj - Bus2, withdraw - Bus12");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		f = algoDsl.getAlgorithm().lossFactor("Bus2", "Bus12");
		System.out.println("LossFactor: " + f);

		System.out.println("Inj - Bus2, withdraw - Bus14 50%, Bus13 50%");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS);
		algoDsl.addWithdrawBus("Bus14", 50.0);
		algoDsl.addWithdrawBus("Bus13", 50.0);
		f = algoDsl.getAlgorithm().lossFactor("Bus2");
		System.out.println("LossFactor: " + f);

		System.out.println("Inj - Bus2, withdraw - Bus14 90%, Bus13 10%");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS);
		algoDsl.addWithdrawBus("Bus14", 90.0);
		algoDsl.addWithdrawBus("Bus13", 10.0);
		f = algoDsl.getAlgorithm().lossFactor("Bus2");
		System.out.println("LossFactor: " + f);

		System.out.println("Inj - Bus2, withdraw - Bus14 40%, Bus13 30%, Bus12 30%");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS);
		algoDsl.addWithdrawBus("Bus14", 40.0);
		algoDsl.addWithdrawBus("Bus13", 30.0);
		algoDsl.addWithdrawBus("Bus12", 30.0);
		f = algoDsl.getAlgorithm().lossFactor("Bus2");
		System.out.println("LossFactor: " + f);
	}		
}

