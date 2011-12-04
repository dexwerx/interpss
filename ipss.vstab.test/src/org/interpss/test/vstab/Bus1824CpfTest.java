package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.custom.IpssFileAdapter;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.test.VStabTestSetup;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.GenDispPattern;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;
import org.interpss.vstab.cpf.GenDispPattern.Pattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.interpss.vstab.util.VstabFuncOut;
import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.SimuContext;

public class Bus1824CpfTest extends VStabTestSetup{
	@Test
	public void testCase() throws Exception{
		IpssFileAdapter adapter = PluginSpringFactory.getCustomFileAdapter("ipssdat");
		SimuContext simuCtx = adapter.load("testData/ipssdata/BUS1824.ipssdat");
  		System.out.println("End loading data ...");

		AclfNetwork net = simuCtx.getAclfNet();

	// set GenPLimit;
	TestCaseFactory.setGenPLimit(net,0.1);
//	TestCaseFactory.setGenQLimit(net,0.5);
	// define the load increase 
	LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
	LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
	// define gen dispatch
	GenDispPattern pattern=new GenDispPattern(net, Pattern.BASE_CASE_DIR);
	GenDispatch genDisp=new GenDispatch(net, pattern);
	// create a cpf algorithm;
	CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
	
	cpfAlgo.setAnalysisStopCriteria(AnalysisStopCriteria.MAX_POWER_POINT);
	cpfAlgo.getCpfSolver().getLambda().setValue(0.0);
	cpfAlgo.setCPFMaxInteration(200);
	assertTrue(cpfAlgo.runCPF());
	VstabFuncOut.pvResult2EXL(cpfAlgo,"E:/Bus1824_cpf.xls");
	}
}
