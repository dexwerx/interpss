package org.interpss.test.vstab;

import static org.interpss.CorePluginFunction.AclfResultBusStyle;
import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.ieeecdf.IeeeCDFAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.spring.CorePluginSpringFactory;
import org.interpss.test.VStabTestSetup;
import org.interpss.vstab.util.CustomLfAlgorithm;
import org.junit.Test;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;

public class CutomLFTest extends VStabTestSetup {
	@Test
	public void testCase1() throws InterpssException{
		IODMAdapter adapter= new IeeeCDFAdapter();
		assertTrue(adapter.parseInputFile("testdata/ieee_cdf/Ieee14.ieee"));// error with 005ieee.ieee
		AclfNetwork net=CorePluginSpringFactory
		                .getOdm2AclfMapper()
		                .map2Model((AclfModelParser)adapter.getModel())
		                .getAclfNet();
		CustomLfAlgorithm customLf=new CustomLfAlgorithm(net);
		customLf.loadflow();
		System.out.print(AclfResultBusStyle.f(net));
		
	}

}
