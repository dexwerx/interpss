package dynamicTest;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.junit.Test;

import BPA.ODMBpaAdapter;

import dynamic.ODMBPAAdapter2;

public class ODM2BPAAdapterTest {
	@Test
	public void testCase1(){
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile("testdata/07c_0615_notBE.dat"));
		
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
//		parser.stdout();
		
		ODMBpaAdapter odm2Bpa=new ODMBpaAdapter(parser);
		odm2Bpa.ODM2BPA("NF10", "XTSJ", "d:/07c_0615_notBE.DAT");
		System.out.println(odm2Bpa.stdout());
		
		
	IODMAdapter adapter2 = new BPAAdapter();
	assertTrue(adapter2.parseInputFile(IODMAdapter.NetType.DStabNet,
			new String[] { "testdata/07c_0615_notBE.dat","testdata/07c_mach_exc_noSE_EA_FJ_FK.swi"},IODMModelParser.chineseEncoding));
	
	DStabModelParser parser2 = (DStabModelParser)adapter2.getModel();
//	parser2.stdout();
	
	ODMBPAAdapter2 odm2Bpa2=new ODMBPAAdapter2(parser2);
	odm2Bpa2.ODM2BPA("07c", "nf10       5", "d:/07c_mach_exc_noSE_EA_FJ_FK.SWI");
	System.out.println(odm2Bpa2.stdout());
	
	}
}
