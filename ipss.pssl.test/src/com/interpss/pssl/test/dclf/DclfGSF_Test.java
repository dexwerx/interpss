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
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.SenAnalysisBusXmlType;
import org.ieee.odm.schema.SenBusAnalysisEnumType;
import org.ieee.odm.schema.SensitivityEnumType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType.BranchSFactor;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.NumericUtil;
import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.dclf.BusSenAnalysisType;
import com.interpss.pssl.common.PSSLException;
import com.interpss.pssl.odm.DclfDslODMRunner;
import com.interpss.pssl.odm.DclfDslODMRunner.DclfAnalysisType;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;
import com.interpss.pssl.test.BaseTestSetup;

public class DclfGSF_Test extends BaseTestSetup {
	@Test
	public void gsfXmlTest() throws PSSLException {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();	
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net);
		//AclfModelParser parser = algoDsl.runAnalysis("testData/aclf/DclfGSFRun.xml");
		
		AclfModelParser parser = new AclfModelParser();
		IpssScenarioHelper helper = new IpssScenarioHelper(parser);
		assertTrue(helper.getSenAnalysisList() != null);
		
		DclfSenAnalysisXmlType dclfCase = helper.createSenCase();
		DclfBranchSensitivityXmlType gsf = helper.createGSF(dclfCase);
		dclfCase.getGenShiftFactor().add(gsf);
/*
  					<pss:genShiftFactor>
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
  						<pss:branchSFactor>
  							<pss:branch xsi:type="pss:LineBranchXmlType" id="Bus2_Bus3"  fromBusId="Bus2" toBusId="Bus3"></pss:branch>
  						</pss:branchSFactor>
  						<pss:branchSFactor>
  							<pss:branch xsi:type="pss:LineBranchXmlType" id="Bus4_Bus3" fromBusId="Bus4" toBusId="Bus3"></pss:branch>
  						</pss:branchSFactor>
  					</pss:genShiftFactor>
*/
		gsf.setSenType(SensitivityEnumType.P_ANGLE);
		
		gsf.setInjectBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		SenAnalysisBusXmlType bus = helper.createSenAnalysisBus(gsf.getInjectBusList().getInjectBuses());
		bus.setBusId("Bus2");
		
		gsf.setWithdrawBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		bus = helper.createSenAnalysisBus(gsf.getWithdrawBusList().getWithdrawBuses());
		bus.setBusId("Bus3");
		
		BranchSFactor sf = helper.createBranchSFactor(gsf.getBranchSFactor());
		BranchRefXmlType line = helper.createBranchRefXmlType();
		sf.setBranch(line);
		line.setBranchId("Bus2_Bus3");
		line.setFromBusId("Bus2");
		line.setToBusId("Bus3");
		
		sf = helper.createBranchSFactor(gsf.getBranchSFactor());
		line = helper.createBranchRefXmlType();
		sf.setBranch(line);
		line.setBranchId("Bus3_Bus4");
		line.setFromBusId("Bus3");
		line.setToBusId("Bus4");
		
		gsf = helper.createGSF(dclfCase);
		dclfCase.getGenShiftFactor().add(gsf);
/*
  					<pss:genShiftFactor>
  						<pss:senType>PAngle</pss:senType>
  						<pss:injectBusType>SingleBus</pss:injectBusType>
  						<pss:injectBusList>
  							<pss:injectBus>
  								<pss:busId>Bus2</pss:busId>
  							</pss:injectBus>
  						</pss:injectBusList>
  						<pss:withdrawBusType>MultipleBus</pss:withdrawBusType>
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
  						<pss:branchSFactor>
  							<pss:branch xsi:type="pss:LineBranchXmlType" id="Bus9_Bus14"  fromBusId="Bus9" toBusId="Bus14"></pss:branch>
  						</pss:branchSFactor>
  						<pss:branchSFactor>
  							<pss:branch xsi:type="pss:LineBranchXmlType" id="Bus6_Bus13" fromBusId="Bus6" toBusId="Bus13"></pss:branch>
  						</pss:branchSFactor>
  						<pss:branchSFactor>
  							<pss:branch xsi:type="pss:LineBranchXmlType" id="Bus12_Bus13" fromBusId="Bus12" toBusId="Bus13"></pss:branch>
  						</pss:branchSFactor>
  					</pss:genShiftFactor>
*/
		gsf.setSenType(SensitivityEnumType.P_ANGLE);
		
		gsf.setInjectBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		bus = helper.createSenAnalysisBus(gsf.getInjectBusList().getInjectBuses());
		bus.setBusId("Bus2");
		
		gsf.setWithdrawBusType(SenBusAnalysisEnumType.MULTIPLE_BUS);
		bus = helper.createSenAnalysisBus(gsf.getWithdrawBusList().getWithdrawBuses());
		bus.setBusId("Bus13");
		bus.setPercent(50.0);
		bus = helper.createSenAnalysisBus(gsf.getWithdrawBusList().getWithdrawBuses());
		bus.setBusId("Bus14");
		bus.setPercent(50.0);
		
		sf = helper.createBranchSFactor(gsf.getBranchSFactor());
		line = helper.createBranchRefXmlType();
		sf.setBranch(line);
		line.setBranchId("Bus9_Bus14");
		line.setFromBusId("Bus9");
		line.setToBusId("Bus14");

		sf = helper.createBranchSFactor(gsf.getBranchSFactor());
		line = helper.createBranchRefXmlType();
		sf.setBranch(line);
		line.setBranchId("Bus6_Bus13");
		line.setFromBusId("Bus6");
		line.setToBusId("Bus13");
		
		sf = helper.createBranchSFactor(gsf.getBranchSFactor());
		line = helper.createBranchRefXmlType();
		sf.setBranch(line);
		line.setBranchId("Bus12_Bus13");
		line.setFromBusId("Bus12");
		line.setToBusId("Bus13");
		
		gsf = helper.createGSF(dclfCase);
		dclfCase.getGenShiftFactor().add(gsf);
/*
  					<pss:genShiftFactor>
  						<pss:senType>PAngle</pss:senType>
  						<pss:injectBusType>SingleBus</pss:injectBusType>
  						<pss:injectBusList>
  							<pss:injectBus>
  								<pss:busId>Bus2</pss:busId>
  							</pss:injectBus>
  						</pss:injectBusList>
  						<pss:withdrawBusType>LoadDistribution</pss:withdrawBusType>
  						<pss:minLoadForDistFactor unit="MW" value="5" />
  						<pss:branchSFactor>
  							<pss:branch xsi:type="pss:LineBranchXmlType" id="Bus9_Bus14_1"  fromBusId="Bus9" toBusId="Bus14"></pss:branch>
  						</pss:branchSFactor>
  						<pss:branchSFactor>
  							<pss:branch xsi:type="pss:LineBranchXmlType" id="Bus6_Bus13_1" fromBusId="Bus6" toBusId="Bus13"></pss:branch>
  						</pss:branchSFactor>
  						<pss:branchSFactor>
  							<pss:branch xsi:type="pss:LineBranchXmlType" id="Bus12_Bus13_1" fromBusId="Bus12" toBusId="Bus13"></pss:branch>
  						</pss:branchSFactor>
  					</pss:genShiftFactor>
 */
		gsf.setSenType(SensitivityEnumType.P_ANGLE);
		
		gsf.setInjectBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		bus = helper.createSenAnalysisBus(gsf.getInjectBusList().getInjectBuses());
		bus.setBusId("Bus2");
		
		gsf.setWithdrawBusType(SenBusAnalysisEnumType.LOAD_DISTRIBUTION);
		gsf.setMinLoadForDistFactor(helper.createActivePower(5.0, "MW"));
		
		sf = helper.createBranchSFactor(gsf.getBranchSFactor());
		line = helper.createBranchRefXmlType();
		sf.setBranch(line);
		line.setBranchId("Bus9_Bus14_1");
		line.setFromBusId("Bus9");
		line.setToBusId("Bus14");

		sf = helper.createBranchSFactor(gsf.getBranchSFactor());
		line = helper.createBranchRefXmlType();
		sf.setBranch(line);
		line.setBranchId("Bus6_Bus13_1");
		line.setFromBusId("Bus6");
		line.setToBusId("Bus13");
		
		sf = helper.createBranchSFactor(gsf.getBranchSFactor());
		line = helper.createBranchRefXmlType();
		sf.setBranch(line);
		line.setBranchId("Bus12_Bus13_1");
		line.setFromBusId("Bus12");
		line.setToBusId("Bus13");
		
		new DclfDslODMRunner(algoDsl)
			.runDclfCase(dclfCase, DclfAnalysisType.All);

		
		System.out.println(parser.toXmlDoc(false));
	}
	
	@Test
	public void gsfTest() {
		AclfNetwork net = IpssAdapter.importAclfNet("testData/aclf/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();		
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
			   .withdrawBusId("Bus3");
		System.out.println("\nInj - Bus2, withdraw - Bus3");
		
		double f = algoDsl.monitorBranch("Bus2", "Bus3")
		   					.genShiftFactor();
		//System.out.println("monitorBranch - 2->3");
		//System.out.println("GSF: " + f );	
		assertTrue(NumericUtil.equals(f, 0.559376, 0.00001));

		f = algoDsl.monitorBranch("Bus3", "Bus4")
					.genShiftFactor();
		System.out.println("monitorBranch - 3->4");
		System.out.println("GSF: " + f );	
		assertTrue(NumericUtil.equals(f,-0.440623, 0.00001));

		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
			   .setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS)
			   .addWithdrawBus("Bus14", 50.0)
			   .addWithdrawBus("Bus13", 50.0);
		System.out.println("\nInj - Bus2, withdraw - Bus14 50%, Bus13 50%");
		
		f = algoDsl.monitorBranch("Bus9", "Bus14")
					.genShiftFactor();
		//System.out.println("monitorBranch - 9->14");
		//System.out.println("GSF: " + f );	
		assertTrue(NumericUtil.equals(f, 0.424185, 0.00001));

		f = algoDsl.monitorBranch("Bus6", "Bus13")
					.genShiftFactor();
		//System.out.println("monitorBranch - 6->13");
		//System.out.println("GSF: " + f );	
		assertTrue(NumericUtil.equals(f, 0.447801, 0.00001));

		f = algoDsl.monitorBranch("Bus12", "Bus13")
					.genShiftFactor();
		//System.out.println("monitorBranch - 12->13");
		//System.out.println("GSF: " + f );
		assertTrue(NumericUtil.equals(f, 0.128015, 0.00001));
		
		algoDsl = IpssPTrading.createDclfAlgorithm(net);
		algoDsl.injectionBusId("Bus2")
			   .setWithdrawBusType(BusSenAnalysisType.MULTIPLE_BUS)
			   .addWithdrawBus("Bus14", 90.0)
			   .addWithdrawBus("Bus13", 10.0);
		System.out.println("\nInj - Bus2, withdraw - Bus14 90%, Bus13 10%");
		
		f = algoDsl.monitorBranch("Bus9", "Bus14")
					.genShiftFactor();
		//System.out.println("monitorBranch - 9->14");
		//System.out.println("GSF: " + f );	
		assertTrue(NumericUtil.equals(f, 0.569027, 0.00001));

		f =	algoDsl.monitorBranch("Bus6", "Bus13")
					.genShiftFactor();
		//System.out.println("monitorBranch - 6->13");
		//System.out.println("GSF: " + f);	
		assertTrue(NumericUtil.equals(f, 0.335159, 0.00001));

		f = algoDsl.monitorBranch("Bus12", "Bus13")
					.genShiftFactor();
		//System.out.println("monitorBranch - 12->13");
		//System.out.println("GSF: " + f );	
		assertTrue(NumericUtil.equals(f, 0.095813, 0.00001));
		
		algoDsl.setRefBus()
				.addLoadWithdrawBus(5.0, UnitType.mW);		
		f = algoDsl.injectionBusId("Bus2")
						.monitorBranch("Bus9", "Bus14")
						.genShiftFactor();		
		System.out.print("\nGen 2, monitorBranch - 9->14");
		System.out.println(" GSF: " + f );		

		f = algoDsl.monitorBranch("Bus6", "Bus13")
			.genShiftFactor();		
		System.out.print("\nGen 2, monitorBranch - 6->13");
		System.out.println(" GSF: " + f );		
	}
}

