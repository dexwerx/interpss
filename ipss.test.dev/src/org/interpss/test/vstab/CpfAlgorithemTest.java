package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.test.DevTestSetup;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.junit.Test;

public class CpfAlgorithemTest extends DevTestSetup{
	@Test
	public void testCase1() throws Exception {
	
	CPFAlgorithm cpfAlgo = TestCaseFactory.createCpfAlgo("ieee39");
	
	assertTrue(cpfAlgo.getCpfSolver().getSortNumOfContParam()==6);
	assertTrue(cpfAlgo.getLoadIncrease().getPattern().getLoadIncDir().get("1").getReal()-1.6<1e-9);
	}
}
