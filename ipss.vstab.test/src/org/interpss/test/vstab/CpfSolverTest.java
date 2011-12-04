package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.display.AclfOutFunc;
import org.interpss.test.VStabTestSetup;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;
import org.interpss.vstab.util.VstabFuncOut;
import org.junit.Test;

public class CpfSolverTest extends VStabTestSetup {

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
//		cpfAlgo.setCPFMaxInteration(40);
//		cpfAlgo.getCpfSolver().getLambda().setValue(0.16);
//		assertTrue(cpfAlgo.runCPF());
//		System.out.println(AclfOutFunc.loadFlowSummary(cpfAlgo.getAclfNetwork()));
////		cpfAlgo.getCpfSolver().predictorStep();
////		cpfAlgo.getCpfSolver().correctorStep();
////		cpfAlgo.getCpfSolver().backToLastConvgState();
////		System.out.println("back to last state");
//////		cpfAlgo.getCpfSolver().getPredStepSolver().stepSolver();
////		System.out.println(AclfOutFunc.loadFlowSummary(cpfAlgo.getAclfNetwork()));
//		System.out.println(cpfAlgo.getCpfSolver().getLambdaList().toString());
//		VstabFuncOut.pvResult2EXL(cpfAlgo,"E:/ieee5_cpf.xls");
//	}
	@Test
	public void ieee039_testCpfSolver_Loop() throws Exception{

		CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee039");
		cpfAlgo.setAnalysisStopCriteria(AnalysisStopCriteria.MAX_POWER_POINT);
		cpfAlgo.getCpfSolver().getLambda().setValue(0.0);
		cpfAlgo.setCPFMaxInteration(200);
		assertTrue(cpfAlgo.runCPF());
		System.out.println(AclfOutFunc.loadFlowSummary(cpfAlgo.getAclfNetwork()));
		VstabFuncOut.pvResult2EXL(cpfAlgo,"E:/ieee39_cpf_all_load_0.1_qLimit_0319.xls");
		
	}
//	@Test
//	public void ieee039_testEachBusIncrease() throws Exception{
//		String filePath="testData/ieee_cdf/ieee039.ieee";
//		IpssFileAdapter adapter = PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF);
//		SimuContext simuCtx = adapter.load(filePath);
//
//		AclfNetwork net = simuCtx.getAclfNet();
//		net.getAclfBus("Bus32").setQGenLimit(new LimitType(3.0,0));
//		// set GenPLimit;
//		TestCaseFactory.setGenPLimit(net,0.2);
////		for(int i=0;i<net.getBusList().size();i++){
////		AclfBus b=(AclfBus) net.getBusList().get(i);	
////		if(b.isLoad()){
//		// define the load increase 
//		int i=14;
//		Bus[] incBus={net.getBusList().get(i)};
//		LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.BUS,LoadIncType.CONST_PF,incBus);
//		LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
//		// define gen dispatch
//		GenDispPattern pattern=new GenDispPattern(net, Pattern.BASE_CASE_DIR);
//		GenDispatch genDisp=new GenDispatch(net, pattern);
//		// create a cpf algorithm;
//		CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
//		cpfAlgo.setAnalysisStopCriteria(AnalysisStopCriteria.MAX_POWER_POINT);
//		cpfAlgo.getCpfSolver().getLambda().setValue(3);
//		cpfAlgo.setCPFMaxInteration(200);
//		cpfAlgo.setPflowTolerance(0.005);
//		assertTrue(cpfAlgo.runCPF());
//		VstabFuncOut.pvResult2EXL(cpfAlgo,"E:/ieee39_cpf_load_bus_"+(i+1)+".xls");
//		System.out.println(AclfOutFunc.loadFlowSummary(cpfAlgo.getAclfNetwork()));
//		 }
//		}
//		
//	}
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
