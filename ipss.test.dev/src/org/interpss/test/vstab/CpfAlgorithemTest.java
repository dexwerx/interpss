package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.test.DevTestSetup;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.GenDispPattern.GenDispPtn;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.util.sample.SampleCases;

public class CpfAlgorithemTest extends DevTestSetup{
	@Test
	public void testCase1() {
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
	
	assertTrue(cpfAlgo.getCpfSolver().getSortNumOfContParam()==6);
	assertTrue(cpfAlgo.getLoadIncrease().getPattern().getLoadIncDir().get("1").getReal()-1.6<1e-9);
	}
}
