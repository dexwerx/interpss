package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;
import org.interpss.vstab.cpf.GenDispPattern.GenDispPtn;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.util.sample.SampleCases;

public class CpfSolverTest {
//
//	@Test
//	public void testCpfSolver_OneStep(){
//		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
//		SampleCases.load_LF_5BusSystem(net);
//	    
//		//define load Increase;
//		LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
//		LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
//		assertTrue(ldInc.getPattern().getLoadIncDir().size()==3);
//		// define gen dispatch
//		GenDispatch genDisp=new GenDispatch(net, GenDispPtn.RESERVE_PROPORTION);
//	    // create the cpf algorithm;
//		CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
//		
//		assertTrue(cpfAlgo.getCpfSolver().getSortNumOfContParam()==5);
//		
//		
//		assertTrue(cpfAlgo.getCpfSolver().predictorStep()); // test predictor step;
//
////		System.out.println("before corrector: bus 1 vang="+cpfAlgo.getAclfNetwork().getAclfBus("1").getVoltageAng());
//		assertTrue(cpfAlgo.getCpfSolver().correctorStep()); // corrector step right after predictor step 
////		System.out.println("after corrector: bus 1 vang="+cpfAlgo.getAclfNetwork().getAclfBus("1").getVoltageAng());
//	}
	@Test
	public void testCpfSolver_Loop(){
		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		SampleCases.load_LF_5BusSystem(net);
	    
		//define load Increase;
		LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
		LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
		assertTrue(ldInc.getPattern().getLoadIncDir().size()==3);
		// define gen dispatch
		GenDispatch genDisp=new GenDispatch(net, GenDispPtn.RESERVE_PROPORTION);
	    // create the cpf algorithm;
		CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
		cpfAlgo.setAnalysisStopCriteria(AnalysisStopCriteria.MAX_POWER_POINT);
		cpfAlgo.setMaxIterations(5);
		assertTrue(cpfAlgo.getCpfSolver().solveCPF());
		
	}
}
