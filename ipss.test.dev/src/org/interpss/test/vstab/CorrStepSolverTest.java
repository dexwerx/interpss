package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.display.AclfOutFunc;
import org.interpss.test.DevTestSetup;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.GenDispPattern.GenDispPtn;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.CorrectorStepSolver;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.junit.Test;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.util.sample.SampleCases;
import com.interpss.spring.CoreCommonSpringCtx;

public class CorrStepSolverTest extends DevTestSetup {
	/**
	 * Test Lambda as the continuation parameter;
	 */
	@Test
	public void testCase1() {
    IPSSMsgHub msg = 		CoreCommonSpringCtx.getIpssMsgHub();
	
	// create a sample 5-bus system for Loadflow 
	AclfNetwork net = CoreObjectFactory.createAclfNetwork();
	SampleCases.load_LF_5BusSystem(net);
	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
	//define load Increase;
	LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
	LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
	// define gen dispatch
	GenDispatch genDisp=new GenDispatch(net, GenDispPtn.RESERVE_PROPORTION);
	// create CPF algorithm;
	CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net, ldInc,genDisp);
	double fixVal=cpfAlgo.getCpfSolver().getLambda().getValue();

	CorrectorStepSolver corrSolver =cpfAlgo.getCpfSolver().getCorrStepSolver();
	algo.setNrSolver(corrSolver);
	net.accept(algo);
	assertTrue(net.isLfConverged());
	assertTrue((cpfAlgo.getCpfSolver().getLambda().getValue()-fixVal)<1e-9);
	System.out.println(AclfOutFunc.loadFlowSummary(net));
	}
	
	/**
	 * Test the bus voltage magnitude as the continuation parameter;
	 */
	@Test
	public void testCase2() {
		   
			// create a sample 5-bus system for Loadflow 
			AclfNetwork net = CoreObjectFactory.createAclfNetwork();
			SampleCases.load_LF_5BusSystem(net);
			// define the load increase 
			LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
			LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
			// define gen dispatch
			GenDispatch genDisp=new GenDispatch(net, GenDispPtn.RESERVE_PROPORTION);
			// create a cpf algorithm;
			CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);

			double fixVal=1.034;
			net.getAclfBus("3").setVoltageMag(fixVal);
			
			cpfAlgo.getCpfSolver().setSorNumofContParam(4);// sortNumber=2 ,namely vmag of bus1 is selected as the continuation parameter
			System.out.println("cpfAlgo SortNum="+cpfAlgo.getCpfSolver().getSortNumOfContParam());
			CorrectorStepSolver corSolver=cpfAlgo.getCpfSolver().getCorrStepSolver();

			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
			algo.setNrSolver(corSolver);
			algo.setMaxIterations(100);
			algo.setTolerance(0.001);
			net.accept(algo);
			System.out.println(AclfOutFunc.loadFlowSummary(net));
			System.out.println("SortNumber of bus1 ="+net.getAclfBus("1").getSortNumber());
			assertTrue(net.isLfConverged());
			assertTrue((net.getAclfBus("1").getVoltageMag()-fixVal)<1e-5);// bus2

			
	}
}
