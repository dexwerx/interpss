package org.interpss.core.dclf;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginTestSetup;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.junit.Test;

import com.interpss.SimuObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class DclfIeee14BusCaseTest extends PluginTestSetup {
	@Test
	public void runSingleAclfCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(simuCtx.getAclfNet())
					.runDclfAnalysis(true);

		//System.out.println(DclfOutFunc.dclfResults(algo));
		
		//System.out.println(algo.getBranchFlow("0001", "0002", "1", Type.mW));
		assertTrue(Math.abs(algoDsl.branchFlow("0001", "0002", "1", UnitType.mW) - 147.88) < 0.01);
	}

	@Test
	public void lineOutageCaseTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(simuCtx.getAclfNet())
					.runDclfAnalysis(true);

		double x1 = algoDsl.setInjectionBusId("0004")
			.setWithdrawBusId("0007")
			.setBranchFromBusId("0004").toBusId("0007")
			.genShiftFactor();
		//System.out.println(x1);

		double x2 = algoDsl.setInjectionBusId("0004")
			.setWithdrawBusId("0007")
			.setBranchFromBusId("0004").toBusId("0009")
			.genShiftFactor();
		//System.out.println(x2);

		double x3 = algoDsl.setInjectionBusId("0004")
			.setWithdrawBusId("0007")
			.setBranchFromBusId("0005").toBusId("0006")
			.genShiftFactor();
		//System.out.println(x3);
	
		assertTrue(Math.abs(x1 + x2 + x3 - 1.0) < 0.0001);
		
		double pFlow = algoDsl.branchFlow("0004", "0007", "1", UnitType.mW);
		double derating1 = pFlow * x2 / ( 1.0 - x1);
		double derating2 = pFlow * x3 / ( 1.0 - x1);

		//System.out.println("Mw flow 4->7: " + pFlow);
		//System.out.println("derating 4->9: " + derating1);
		//System.out.println("derating 5->6: " + derating2);
		assertTrue(Math.abs(derating1 - 14.71922) < 0.0001);
		assertTrue(Math.abs(derating2 - 14.26586) < 0.0001);
		
		AclfBranch outageBranch = algoDsl.aclfNet().getAclfBranch("0004", "0007", "1");
		AclfBranch transferBranch = algoDsl.aclfNet().getAclfBranch("0004", "0009", "1");
		double f = algoDsl.lineOutageDFactor(outageBranch, transferBranch);
		//System.out.println("LODF (4->7) -> (4->9): " + f);
		assertTrue(Math.abs(f - 0.50782) < 0.0001);
		
		//System.out.println(DclfOutFunc.lineOutageAnalysisTitle("", outageBranch.getId()));		
		//System.out.println(DclfOutFunc.lineOutageAnalysisBranchFlow(transferBranch, algo, pFlow, f));		
	}

	@Test
	public void lineOutageCaseTest1() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(simuCtx.getAclfNet())
					.runDclfAnalysis(true);

		AclfBranch outageBranch = algoDsl.aclfNet().getAclfBranch("0004", "0007", "1");
		AclfBranch transferBranch = algoDsl.aclfNet().getAclfBranch("0004", "0009", "1");
		double f = algoDsl.lineOutageDFactor(outageBranch, transferBranch);
		//System.out.println("LODF (4->7) -> (4->9): " + f);
		assertTrue(Math.abs(f - 0.50782) < 0.0001);
		
		//System.out.println(DclfOutFunc.lineOutageAnalysisTitle("", outageBranch.getId()));		
		//System.out.println(DclfOutFunc.lineOutageAnalysisBranchFlow(transferBranch, algo, pFlow, f));		
	}
}
