package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.display.AclfOutFunc;
import org.interpss.test.DevTestSetup;
import org.interpss.test.vstab.TestCaseFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;
import org.junit.Test;

public class CpfSolverTest extends DevTestSetup {

//	@Test
//	public void ieee005_testCpfSolver_OneStep() throws Exception{
//
//		CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee005");
//		
//		assertTrue(cpfAlgo.getCpfSolver().predictorStep()); // test predictor step;
//
////		System.out.println("before corrector: bus 1 vang="+cpfAlgo.getAclfNetwork().getAclfBus("1").getVoltageAng());
//		assertTrue(cpfAlgo.getCpfSolver().correctorStep()); // corrector step right after predictor step 
////		System.out.println("after corrector: bus 1 vang="+cpfAlgo.getAclfNetwork().getAclfBus("1").getVoltageAng());
//	}
	
//	@Test
//	public void ieee005_testCpfSolver_Loop() throws Exception{
//
//		CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee005");
//		cpfAlgo.setAnalysisStopCriteria(AnalysisStopCriteria.MAX_POWER_POINT);
//		cpfAlgo.setCPFMaxInteration(45);
//		assertTrue(cpfAlgo.runCPF());
//		System.out.println(AclfOutFunc.loadFlowSummary(cpfAlgo.getAclfNetwork()));
//		
//	}
	@Test
	public void ieee039_testCpfSolver_Loop() throws Exception{

		CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee039");
		cpfAlgo.setAnalysisStopCriteria(AnalysisStopCriteria.MAX_POWER_POINT);
		cpfAlgo.setCPFMaxInteration(27);
		cpfAlgo.setPflowTolerance(0.01);
		assertTrue(cpfAlgo.runCPF());
		System.out.println(AclfOutFunc.loadFlowSummary(cpfAlgo.getAclfNetwork()));
		
	}
//	@Test
//	public void ieee030_testCpfSolver_Loop() throws Exception{
//
//		CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee30");
//		cpfAlgo.setAnalysisStopCriteria(AnalysisStopCriteria.MAX_POWER_POINT);
//		cpfAlgo.setCPFMaxInteration(2);
//		assertTrue(cpfAlgo.runCPF());
//		System.out.println(AclfOutFunc.loadFlowSummary(cpfAlgo.getAclfNetwork()));
//		
//	}
	
}
