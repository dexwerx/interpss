package org.interpss.test.odm.opf;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.opf.OpfModelParser;
import org.ieee.odm.schema.OpfNetworkXmlType;
import org.junit.Test;
public class OpfModelParserTest {
	@Test
	public void testOpfModelParser()throws Exception{
		FileInputStream in =new FileInputStream("testdata/ieee_odm/opf_3bus_test.xml");
		OpfModelParser OpfParser = ODMObjectFactory.createOpfModelParser();
		if (OpfParser.parse(in)) {
			System.out.println(OpfParser.toXmlDoc(false));	
			//OpfModelParser OpfParser = new OpfModelParser("testdata/opf_3bus_test.xml");
		    OpfNetworkXmlType opfNet=OpfParser.getOpfNet();
		    assertTrue(opfNet.getBusList().getBus().size()==3);
		    assertTrue(opfNet.getAnglePenaltyFactor()==1);
		    assertTrue(opfNet.getBranchList().getBranch().size()==3);
		}
    }
}
