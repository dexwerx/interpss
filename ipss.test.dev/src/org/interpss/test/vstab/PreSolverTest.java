package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.test.DevTestSetup;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.interpss.vstab.cpf.impl.PredictorStepSolver;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
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
		System.out.println(b.getSortNumber());
	}
	//define load Increase;
	LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
	LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
	assertTrue(ldInc.getPattern().getLoadIncDir().size()==3);
	
    // create the cpf algorithm;
	CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc);
	assertTrue(cpfAlgo.getCpfSolver().getSortNumOfContParam()==5);
	
//	 initialize the cpf predictor step solver;
	PredictorStepSolver preSolver=cpfAlgo.getCpfSolver().getPredStepSolver();
	
	preSolver.stepSolver();
	assertTrue((preSolver.getAugmentedJacobi().getA(2, 5).xx-1.60)<1e-9);// bus1.loadP=1.60
	assertTrue((preSolver.getDeltaXLambda().getEntry(0)-(-0.73935))<0.0001);
	assertTrue((preSolver.getDeltaXLambda().getEntry(10)-1.0)<1e-9); // Delta_Lambda=1
	
	// print J-matrix
//    VstabFuncOut.printJmatix(preSolver.getAugmentedJacobi(),6,2);
    // print tangent vector
//    VstabFuncOut.printRealVector(preSolver.getDeltaXLambda());
	}
}
