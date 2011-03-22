package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.test.VStabTestSetup;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.GenDispPattern.Pattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.util.sample.SampleCases;

public class CpfAlgorithemTest extends VStabTestSetup{
	@Test
	public void testCase1() throws Exception {
	
	CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee39");
	
	assertTrue(cpfAlgo.getCpfSolver().getSortNumOfContParam()==6);
	assertTrue(cpfAlgo.getLoadIncrease().getPattern().getLoadIncDir().get("1").getReal()-1.6<1e-9);
	}
}
