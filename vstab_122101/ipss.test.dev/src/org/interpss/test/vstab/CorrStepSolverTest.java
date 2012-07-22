package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.display.AclfOutFunc;
import org.interpss.test.DevTestSetup;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.impl.CorrectorStepSolver;
import org.junit.Test;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.simu.util.sample.SampleCases;
import com.interpss.spring.CoreCommonSpringCtx;

public class CorrStepSolverTest extends DevTestSetup {
	/**
	 * Test Lambda as the continuation parameter;
	 */
//	@Test
//	public void testCase1() {
//    IPSSMsgHub msg = 		CoreCommonSpringCtx.getIpssMsgHub();
//	
//	// create a sample 5-bus system for Loadflow 
//	AclfNetwork net = CoreObjectFactory.createAclfNetwork();
//	SampleCases.load_LF_5BusSystem(net);
//	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
//
//	// create CPF algorithm;
//	CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net, msg);
//	double fixVal=cpfAlgo.getLambdaParam().getValue();
////	CustomLfAlgorithm customAlgo=new CustomLfAlgorithm(cpfAlgo);
//	CorrectorStepSolver corrSolver =cpfAlgo.getCorrStepSolver();
//	algo.setNrSolver(corrSolver);
//	net.accept(algo);
//	assertTrue(net.isLfConverged());
//	assertTrue((cpfAlgo.getLambdaParam().getValue()-fixVal)<1e-9);
//
//	}
	
	/**
	 * Test the bus voltage magnitude as the continuation parameter;
	 */
	@Test
	public void testCase2() {
		    IPSSMsgHub msg = CoreCommonSpringCtx.getIpssMsgHub();
			
			// create a sample 5-bus system for Loadflow 
			AclfNetwork net = CoreObjectFactory.createAclfNetwork();
			SampleCases.load_LF_5BusSystem(net);

			CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net, msg);

			double fixVal=0.956;
			net.getAclfBus("2").setVoltageMag(fixVal);
			
			cpfAlgo.setSorNumofContParam(4);// sortNumber=4 ,namely vmag of bus2 is selected as the continuation parameter
			cpfAlgo.setFixedValOfContPara(fixVal);// set fixedVal to 1.5 intentionally for testing
			System.out.println("cpfAlgo SortNum="+cpfAlgo.getSortNumOfContParam());
			CorrectorStepSolver corSolver=cpfAlgo.getCpfSolver().getCorrStepSolver();

			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
			algo.setNrSolver(corSolver);
			net.accept(algo);
			System.out.println(AclfOutFunc.loadFlowSummary(net));
			System.out.println("SortNumber of bus2 ="+net.getAclfBus("2").getSortNumber());
			assertTrue(net.isLfConverged());
			assertTrue((net.getAclfBus("2").getVoltageMag()-fixVal)<1e-5);// bus2

			
	}
}
