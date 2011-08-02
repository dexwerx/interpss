package test;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.junit.Test;

import BPA.ODMBpaAdapter;

public class Odm2BpaAdapterTest {
	@Test
	public void testCase1(){
	IODMAdapter adapter = new BPAAdapter();
	assertTrue(adapter.parseInputFile("testdata/IEEE9.dat"));
	
	AclfModelParser parser = (AclfModelParser)adapter.getModel();
	parser.stdout();
	
	ODMBpaAdapter odm2Bpa=new ODMBpaAdapter(parser);
	odm2Bpa.ODM2BPA("ieee9", "IEEE9", "d:/ieee9byAdapter.DAT");
	System.out.println(odm2Bpa.stdout());
	
	}

}
