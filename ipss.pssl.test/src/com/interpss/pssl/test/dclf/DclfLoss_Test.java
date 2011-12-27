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

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.ext.ipss.IpssScenarioHelper;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.GenLossFactorXmlType;
import org.ieee.odm.schema.SenAnalysisBusXmlType;
import org.ieee.odm.schema.SenBusAnalysisEnumType;
import org.ieee.odm.schema.SensitivityEnumType;
import org.interpss.display.AclfOutFunc;
import org.interpss.numeric.util.NumericUtil;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.path.LossAllocationAlgorithm;
import com.interpss.core.algo.path.NetPathWalkDirectionEnum;
import com.interpss.core.dclf.BusSenAnalysisType;
import com.interpss.pssl.common.PSSLException;
import com.interpss.pssl.odm.DclfDslODMRunner;
import com.interpss.pssl.odm.DclfDslODMRunner.DclfAnalysisType;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.simu.IpssAclf;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;
import com.interpss.pssl.test.BaseTestSetup;

public class DclfLoss_Test extends BaseTestSetup {
	@Test
	public void lossFactorTest()  throws PSSLException {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();	
		
	  	IpssAclf.createAlgo(net)
	  			.lfMethod(AclfMethod.NR)
	  			.nonDivergent(true)
	  			.runLoadflow();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net);
		//AclfModelParser parser = algoDsl.runAnalysis("testData/aclf/DclfLossRun.xml");
		
		AclfModelParser parser = new AclfModelParser();
		IpssScenarioHelper helper = new IpssScenarioHelper(parser);
		assertTrue(helper.getSenAnalysisList() != null);
		
		DclfSenAnalysisXmlType dclfCase = helper.createSenCase();
		GenLossFactorXmlType lfactor = helper.createGenLossFactor(dclfCase);
		/*
			<pss:genLossFactor>
				<pss:senType>PAngle</pss:senType>
				<pss:injectBusType>SingleBus</pss:injectBusType>
				<pss:injectBusList>
					<pss:injectBus>
						<pss:busId>Bus2</pss:busId>
					</pss:injectBus>
				</pss:injectBusList>
				<pss:withdrawBusType>SingleBus</pss:withdrawBusType>
				<pss:withdrawBusList>
					<pss:withdrawBus>
						<pss:busId>Bus3</pss:busId>
					</pss:withdrawBus>
				</pss:withdrawBusList>
			</pss:genLossFactor>
*/
		lfactor.setSenType(SensitivityEnumType.P_ANGLE);
		
		lfactor.setInjectBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		SenAnalysisBusXmlType bus = helper.createSenAnalysisBus(lfactor.getInjectBusList().getInjectBuses());
		bus.setBusId("Bus2");
		
		lfactor.setWithdrawBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		bus = helper.createSenAnalysisBus(lfactor.getWithdrawBusList().getWithdrawBuses());
		bus.setBusId("Bus3");		
		
		
		lfactor = helper.createGenLossFactor(dclfCase);
/*		
  					<pss:genLossFactor>
  						<pss:senType>PAngle</pss:senType>
  						<pss:injectBusType>SingleBus</pss:injectBusType>
  						<pss:injectBusList>
  							<pss:injectBus>
  								<pss:busId>Bus2</pss:busId>
  							</pss:injectBus>
  						</pss:injectBusList>
  						<pss:withdrawBusType>SingleBus</pss:withdrawBusType>
  						<pss:withdrawBusList>
  							<pss:withdrawBus>
  								<pss:busId>Bus13</pss:busId>
  								<pss:percent>50.0</pss:percent>
  							</pss:withdrawBus>
  							<pss:withdrawBus>
  								<pss:busId>Bus14</pss:busId>
  								<pss:percent>50.0</pss:percent>
  							</pss:withdrawBus>
  						</pss:withdrawBusList>
  					</pss:genLossFactor>
 		
 */
		lfactor.setSenType(SensitivityEnumType.P_ANGLE);
		
		lfactor.setInjectBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		bus = helper.createSenAnalysisBus(lfactor.getInjectBusList().getInjectBuses());
		bus.setBusId("Bus2");
		
		lfactor.setWithdrawBusType(SenBusAnalysisEnumType.MULTIPLE_BUS);
		bus = helper.createSenAnalysisBus(lfactor.getWithdrawBusList().getWithdrawBuses());
		bus.setBusId("Bus13");
		bus.setPercent(50.0);
		bus = helper.createSenAnalysisBus(lfactor.getWithdrawBusList().getWithdrawBuses());
		bus.setBusId("Bus14");
		bus.setPercent(50.0);		
		
		new DclfDslODMRunner(algoDsl)
				.runDclfCase(dclfCase, DclfAnalysisType.All);
		
		
		System.out.println(parser.toXmlDoc(false));
	}
	
	@Test
	public void lossAllocationStep() throws Exception {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
	  	IpssAclf.createAlgo(net)
	  			.lfMethod(AclfMethod.NR)
	  			.nonDivergent(true)
	  			.runLoadflow();

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

	  	IpssAclf.createAlgo(net)
	  			.lfMethod(AclfMethod.NR)
	  			.runLoadflow();
  		
		System.out.println("Inj - Bus2, withdraw - Bus3");
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
			   .withdrawBusId("Bus3");
		//System.out.println("LossFactor: " + algoDsl.lossFactor());
		assertTrue(NumericUtil.equals(algoDsl.lossFactor(), 0.032298, 0.00001));

		System.out.println("Inj - Bus2, withdraw - Bus3 100%");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
			   .setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS)
		       .addWithdrawBus("Bus3", 100.0);
		//System.out.println("LossFactor: " + algoDsl.lossFactor());
		assertTrue(NumericUtil.equals(algoDsl.lossFactor(), 0.032298, 0.00001));

		System.out.println("Inj - Bus2, withdraw - Bus14");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
		   	   .withdrawBusId("Bus14");
		//System.out.println("LossFactor: " + algoDsl.lossFactor());
		assertTrue(NumericUtil.equals(algoDsl.lossFactor(), 0.095169, 0.00001));

		System.out.println("Inj - Bus2, withdraw - Bus13");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
	   	       .withdrawBusId("Bus13");
		//System.out.println("LossFactor: " + algoDsl.lossFactor());
		assertTrue(NumericUtil.equals(algoDsl.lossFactor(), 0.039520, 0.00001));

		System.out.println("Inj - Bus2, withdraw - Bus12");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
	           .withdrawBusId("Bus12");
		//System.out.println("LossFactor: " + algoDsl.lossFactor());
		assertTrue(NumericUtil.equals(algoDsl.lossFactor(),-0.005858, 0.00001));

		System.out.println("Inj - Bus2, withdraw - Bus14 50%, Bus13 50%");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
		       .setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS)
		       .addWithdrawBus("Bus14", 50.0)
		       .addWithdrawBus("Bus13", 50.0);
		//System.out.println("LossFactor: " + algoDsl.lossFactor());
		assertTrue(NumericUtil.equals(algoDsl.lossFactor(), 0.055101, 0.00001));

		System.out.println("Inj - Bus2, withdraw - Bus14 90%, Bus13 10%");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
	           .setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS)
	           .addWithdrawBus("Bus14", 90.0)
	           .addWithdrawBus("Bus13", 10.0);
		//System.out.println("LossFactor: " + algoDsl.lossFactor());
		assertTrue(NumericUtil.equals(algoDsl.lossFactor(), 0.083428, 0.00001));

		System.out.println("Inj - Bus2, withdraw - Bus14 40%, Bus13 30%, Bus12 30%");
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
               .setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS)
               .addWithdrawBus("Bus14", 40.0)
               .addWithdrawBus("Bus13", 30.0)
               .addWithdrawBus("Bus12", 30.0);
		//System.out.println("LossFactor: " + algoDsl.lossFactor());
		assertTrue(NumericUtil.equals(algoDsl.lossFactor(), 0.045895, 0.00001));
	}		
}

