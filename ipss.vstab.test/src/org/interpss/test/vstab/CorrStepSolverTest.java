package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.CorePluginObjFactory;
import org.interpss.display.AclfOutFunc;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.test.VStabTestSetup;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.GenDispPattern;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.GenDispPattern.Pattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.CorrectorStepSolver;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;

public class CorrStepSolverTest extends VStabTestSetup {
	/**
	 * Test Lambda as the continuation parameter;
	 * @throws Exception 
	 */
	@Test
	public void ieee005_LambdatestCase() throws Exception {

		// create a cpf algorithm;
    CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee005");
    AclfNetwork net=cpfAlgo.getAclfNetwork();
    
	Complex dir_PQ=cpfAlgo.getLoadIncrease().getPattern().getLoadIncDir().get("Bus3");
	System.out.println("dir of bus3"+dir_PQ.getReal()+","+dir_PQ.getImaginary());
	
	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();

	double fixVal=cpfAlgo.getCpfSolver().getLambda().getValue();

	CorrectorStepSolver corrSolver =cpfAlgo.getCpfSolver().getCorrStepSolver();
	algo.setNrSolver(corrSolver);
	net.accept(algo);
	assertTrue(net.isLfConverged());
	assertTrue(Math.abs(cpfAlgo.getCpfSolver().getLambda().getValue()-fixVal)<1e-3);
	System.out.println(AclfOutFunc.loadFlowSummary(net));
	}
	
	/**
	 * Test the bus voltage magnitude as the continuation parameter;
	 * @throws Exception 
	 */
	@Test
	public void ieee005_VmagTestCase() throws Exception {

			// create a cpf algorithm;
			CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee005");
			

			
            AclfNetwork net=cpfAlgo.getAclfNetwork();
			double fixVal=1.036;
			net.getAclfBus("Bus3").setVoltageMag(fixVal);
			
			cpfAlgo.getCpfSolver().setSorNumofContParam(4);// sortNumber=2 ,namely vmag of bus1 is selected as the continuation parameter
			System.out.println("cpfAlgo SortNum="+cpfAlgo.getCpfSolver().getSortNumOfContParam());
			CorrectorStepSolver corSolver=cpfAlgo.getCpfSolver().getCorrStepSolver();

			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
			algo.setNrSolver(corSolver);
			algo.setMaxIterations(20);
			algo.setTolerance(0.001);
			net.accept(algo);
			System.out.println(AclfOutFunc.loadFlowSummary(net));
//			System.out.println("SortNumber of bus3 ="+net.getAclfBus("Bus3").getSortNumber());
			assertTrue(net.isLfConverged());
			assertTrue(Math.abs(net.getAclfBus("Bus3").getVoltageMag()-fixVal)<1e-5);// bus3

			
	}
//	/**
//	 * Using lambda as the continuation parameter
//	 * @throws Exception
//	 */
//	@Test
//	public void ieee039_LambdaTestCase() throws Exception {
//		IpssFileAdapter adapter=PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF);
//	    SimuContext sim=adapter.load("testData/ieee_cdf/ieee039.DAT");
//	    AclfNetwork net=sim.getAclfNet();
//	    LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
//		LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
//		// define gen dispatch
//		GenDispPattern genPtn=new GenDispPattern(net,Pattern.BASE_CASE_DIR);
//		GenDispatch genDisp=new GenDispatch(net, genPtn);
//		// create a cpf algorithm;
//		CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
//		// check algo data
//        cpfAlgo.checkDataForCPF();
//        
//		double fixVal=0.012;
//		cpfAlgo.getCpfSolver().getLambda().setValue(fixVal);
//		cpfAlgo.getCpfSolver().setSorNumofContParam(39);//lamda parameter sortNumber=39 
//		System.out.println("cpfAlgo SortNum="+cpfAlgo.getCpfSolver().getSortNumOfContParam());
//		CorrectorStepSolver corSolver=cpfAlgo.getCpfSolver().getCorrStepSolver();
//
//		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
//		algo.setNrSolver(corSolver);
//		algo.setMaxIterations(5);
//		algo.setTolerance(0.0005);
//	    assertTrue(net.accept(algo));
//	    assertTrue(Math.abs(cpfAlgo.getCpfSolver().getLambda().getValue()-fixVal)<1e-5);// Lambda should be constant
//
//	    
//	
//	}
	
	@Test
	public void ieee039_VmagTestCase() throws Exception {
		IpssFileAdapter adapter=CorePluginObjFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF);
	    SimuContext sim=adapter.load("testData/ieee_cdf/ieee039.ieee");
	    AclfNetwork net=sim.getAclfNet();
	    LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
		LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
		// define gen dispatch
		GenDispPattern genPtn=new GenDispPattern(net,Pattern.BASE_CASE_DIR);
		GenDispatch genDisp=new GenDispatch(net, genPtn);
		// create a cpf algorithm;
		CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
		// check algo data
        cpfAlgo.checkDataForCPF();
        
		double fixVal=1.025;
		AclfBus bus3=net.getAclfBus("Bus3");
		net.getAclfBus("Bus3").setVoltageMag(fixVal);//Bus3 sortNumber=9 
		
		cpfAlgo.getCpfSolver().setSorNumofContParam(37);//namely vmag of bus1 is selected as the continuation parameter
		System.out.println("cpfAlgo SortNum="+cpfAlgo.getCpfSolver().getSortNumOfContParam());
		CorrectorStepSolver corSolver=cpfAlgo.getCpfSolver().getCorrStepSolver();

		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		algo.setNrSolver(corSolver);
		algo.setMaxIterations(10);
		algo.setTolerance(0.0005);
	    assertTrue(net.accept(algo));
	    assertTrue(Math.abs(net.getAclfBus("Bus3").getVoltageMag()-fixVal)<1e-5);

	    
	
	}
	
}
