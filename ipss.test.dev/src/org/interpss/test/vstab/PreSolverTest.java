package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.test.DevTestSetup;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.GenDispPattern;
import org.interpss.vstab.cpf.GenDispPattern.Pattern;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.interpss.vstab.cpf.impl.PredictorStepSolver;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;
import com.interpss.simu.util.sample.SampleCases;

public class PreSolverTest extends DevTestSetup {
	@Test
	public void testCase1() {
	// create a sample 5-bus system for Loadflow 
	AclfNetwork net = CoreObjectFactory.createAclfNetwork();
	SampleCases.load_LF_5BusSystem(net);
	
	// run Loadflow analysis of the base case
	LoadflowAlgorithm algo =CoreObjectFactory.createLoadflowAlgorithm();
	net.accept(algo);
	for(Bus b:net.getBusList()){
		System.out.println(b.getId()+", sort Number: "+ b.getSortNumber());
	}
	//define load Increase;
	
	LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
	LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
	assertTrue(ldInc.getPattern().getLoadIncDir().size()==3);
	
	// define gen dispatch
	GenDispPattern pattern=new GenDispPattern(net, Pattern.BASE_CASE_DIR);
	GenDispatch genDisp=new GenDispatch(net, pattern);
	
    // create the cpf algorithm;
	
	CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
	assertTrue(cpfAlgo.getCpfSolver().getSortNumOfContParam()==5);
	System.out.println("before: bus4 Vang="+cpfAlgo.getAclfNetwork().getAclfBus("4").getVoltageAng());
	double v0=cpfAlgo.getAclfNetwork().getAclfBus("4").getVoltageAng();
	
//	 get the cpf predictor step solver;
	PredictorStepSolver preSolver=cpfAlgo.getCpfSolver().getPredStepSolver();

	assertTrue(preSolver.stepSolver());// run predictor step

	System.out.println("X(0)="+preSolver.getDeltaXLambda().getEntry(0));// output the result;

	assertTrue((Math.abs(preSolver.getDeltaXLambda().getEntry(0)-(0.46385)))<1e-4); // bus4 delta Vang
	assertTrue(Math.abs((preSolver.getDeltaXLambda().getEntry(10)-(1.0)))<1e-9);      // Delta_Lambda=1
	
	// check step size
	assertTrue(cpfAlgo.getStepSize()-0.05<1e-9);
	
	// check predictive result;
	double v1=cpfAlgo.getAclfNetwork().getAclfBus("4").getVoltageAng(); 
	System.out.println("after: bus4 Vang="+cpfAlgo.getAclfNetwork().getAclfBus("4").getVoltageAng());
	assertTrue(Math.abs(v1-v0-(0.4638)*cpfAlgo.getStepSize())<1e-3);

	}

	@Test
	public void testCase2() {
		// create a sample 5-bus system for Loadflow 
		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		SampleCases.load_LF_5BusSystem(net);
		
		// run Loadflow analysis of the base case
		LoadflowAlgorithm algo =CoreObjectFactory.createLoadflowAlgorithm();
		net.accept(algo);
		for(Bus b:net.getBusList()){
			System.out.println(b.getSortNumber());
		}
		//define load Increase;
		LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
		LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
		assertTrue(ldInc.getPattern().getLoadIncDir().size()==3);
		// define gen dispatch
		GenDispPattern pattern=new GenDispPattern(net, Pattern.BASE_CASE_DIR);
		GenDispatch genDisp=new GenDispatch(net, pattern);
	    // create the cpf algorithm;
		CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
		assertTrue(cpfAlgo.getCpfSolver().getSortNumOfContParam()==5);
		
//		 initialize the cpf predictor step solver;
		PredictorStepSolver preSolver=cpfAlgo.getCpfSolver().getPredStepSolver();
		
		//the find the next step cont' param;
		preSolver.stepSolver();

		System.out.println(preSolver.getDeltaV().toString());
	}
}
