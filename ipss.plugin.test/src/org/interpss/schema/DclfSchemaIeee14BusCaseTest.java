package org.interpss.schema;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.PluginTestSetup;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.interpss.xml.schema.BranchRecXmlType;
import org.interpss.xml.schema.BusRecXmlType;
import org.interpss.xml.schema.DclfBranchSensitivityXmlType;
import org.interpss.xml.schema.DclfBusSensitivityXmlType;
import org.interpss.xml.schema.DclfStudyCaseXmlType;
import org.interpss.xml.schema.SenBusAnalysisDataType;
import org.interpss.xml.schema.SensitivityDataType;
import org.junit.Test;

import com.interpss.core.dclf.DclfAlgorithm;
import com.interpss.core.dclf.SenAnalysisType;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class DclfSchemaIeee14BusCaseTest extends PluginTestSetup {
	@Test
	public void doNothingCaseTest() throws Exception {
		
	}
	
	// change 0 -> (n-1) : @Test
	public void runSingleAclfCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK, msg);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		DclfAlgorithm algo = IpssPTrading.createDclfAlgorithm(simuCtx.getAclfNet())
				.runSenAnalysis("testData/xml/RunDclfCase.xml");		

		File xmlFile = new File("testData/xml/RunDclfCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == AnalysisRunDataType.RUN_DCLF);
	  	
		DclfStudyCaseXmlType dclfCase = parser.getRunDclfStudyCase().getDclfStudyCaseList().getDclfStudyCase().get(0);

		if (dclfCase.isCaculatelDclf()) {
			algo.calculateDclf();
			//System.out.println("0014->" + algo.getBusAngle(simuCtx.getAclfAdjNet().getBus("0014").getSortNumber())*Constants.RtoD);
			assertTrue(Math.abs(Math.toDegrees(algo.getBusAngle(simuCtx.getAclfNet().getBus("0012").getSortNumber()))+16.27115) < 0.0001);
			assertTrue(Math.abs(Math.toDegrees(algo.getBusAngle(simuCtx.getAclfNet().getBus("0013").getSortNumber()))+16.43665) < 0.0001);
			assertTrue(Math.abs(Math.toDegrees(algo.getBusAngle(simuCtx.getAclfNet().getBus("0014").getSortNumber()))+17.42943) < 0.0001);
		}
		
		for (DclfBusSensitivityXmlType sen : dclfCase.getSensitivity()) {
			String inBusId = sen.getInjectBusList().getInjectBus().get(0).getBusId();
			if (sen.getSenType() == SensitivityDataType.P_ANGLE) {
				algo.calculateSensitivity(SenAnalysisType.PANGLE, inBusId);
				//System.out.println("P-0013->" + algo.getBusSensitivity(DclfSensitivityType.PANGLE, "0013"));
				//System.out.println("p-0012->" + algo.getBusSensitivity(DclfSensitivityType.PANGLE, "0012"));
				/*
				P-0013->-0.06163210033109512
				p-0012->-0.06132364240087546
				*/				
				for (BusRecXmlType bus : sen.getBus()) {
					double pang = algo.getBusSensitivity(SenAnalysisType.PANGLE, inBusId, bus.getBusId());
					assertTrue(	Math.abs(pang+0.06163) < 0.0001 ||
								Math.abs(pang+0.06132) < 0.0001);
				}
			}
			else if (sen.getSenType() == SensitivityDataType.Q_VOLTAGE) {
				algo.calculateSensitivity(SenAnalysisType.QVOLTAGE, inBusId);
				//System.out.println("Q-0013->" + algo.getBusSensitivity(DclfSensitivityType.QVOLTAGE, "0013", msg));
				//System.out.println("Q-0012->" + algo.getBusSensitivity(DclfSensitivityType.QVOLTAGE, "0012", msg));
				/*
				Q-0013->-0.06086676048791753
				Q-0012->-0.025252754409984517
				*/				
				for (BusRecXmlType bus : sen.getBus()) {
					double qvolt = algo.getBusSensitivity(SenAnalysisType.QVOLTAGE, inBusId, bus.getBusId());
					assertTrue(	Math.abs(qvolt+0.060867) < 0.0001 ||
								Math.abs(qvolt+0.025253) < 0.0001);
				}
			}
		}
		
		for (DclfBranchSensitivityXmlType gsFactor : dclfCase.getGenShiftFactor()) {
			String inBusId = gsFactor.getInjectBusList().getInjectBus().get(0).getBusId();
			for (BranchRecXmlType branch : gsFactor.getBranch()) {
				double gsf = algo.getGenShiftFactor(inBusId, branch.getFromBusId(), branch.getToBusId(), "1");
				//System.out.println("GSF " + branch.getFromBusId() + "->" + branch.getToBusId() + " " + gsf);
				/*
				GSF 0004->0007 0.011086980682516566
				GSF 0004->0009 0.006361588236203221
				GSF 0005->0006 -0.017448568918720087
				*/				
				assertTrue(	Math.abs(gsf-0.011087) < 0.0001 ||
							Math.abs(gsf-0.006362) < 0.0001 ||
							Math.abs(gsf+0.017449) < 0.0001 );
			}
		}

		for (DclfBranchSensitivityXmlType tdFactor : dclfCase.getPTransferDistFactor()) {
			String inBusId = tdFactor.getInjectBusList().getInjectBus().get(0).getBusId();
			String wdBusId = tdFactor.getWithdrawBusList().getWithdrawBus().get(0).getBusId();
			if (tdFactor.getWithdrawBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
				double sum = 0.0;
				for (BranchRecXmlType branch : tdFactor.getBranch()) {
					double ptdf = algo.pTransferDistFactor(inBusId, wdBusId, 
							branch.getFromBusId(), branch.getToBusId(), "1");
					sum += ptdf;
					//System.out.println("PTDF " + branch.getFromBusId() + "->" + branch.getToBusId() + " " + ptdf);
				}
				assertTrue(Math.abs(sum-1.0) < 0.0001);
			}
			else if (tdFactor.getWithdrawBusType() == SenBusAnalysisDataType.MULTIPLE_BUS) {
				double sum = 0.0;
				for (BranchRecXmlType branch : tdFactor.getBranch()) {
					double ptdf = algo.pTransferDistFactor(inBusId, branch.getFromBusId(), branch.getToBusId(), "1");
					sum += ptdf;
					//System.out.println("PTDF " + branch.getFromBusId() + "->" + branch.getToBusId() + " " + ptdf);
				}
				//assertTrue(Math.abs(sum-1.0) < 0.0001);
			}  
		}

		// repeat for testing the cached sensitivity results
		for (DclfBranchSensitivityXmlType tdFactor : dclfCase.getPTransferDistFactor()) {
			String inBusId = tdFactor.getInjectBusList().getInjectBus().get(0).getBusId();
			String wdBusId = tdFactor.getWithdrawBusList().getWithdrawBus().get(0).getBusId();
			if (tdFactor.getWithdrawBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
				double sum = 0.0;
				for (BranchRecXmlType branch : tdFactor.getBranch()) {
					double ptdf = algo.pTransferDistFactor(inBusId, wdBusId, 
									branch.getFromBusId(), branch.getToBusId(), "1");
					sum += ptdf;
					//System.out.println("PTDF " + branch.getFromBusId() + "->" + branch.getToBusId() + " " + ptdf);
				}
				assertTrue(Math.abs(sum-1.0) < 0.0001);
			}
		}
	}
}
