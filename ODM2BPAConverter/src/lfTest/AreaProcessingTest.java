package lfTest;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.interpss.odm.converter.BPA.lf.BpaAclfNetworkRecord;
import org.interpss.odm.converter.BPA.lf.BpaAreaPowerExchgRecord;
import org.junit.Test;


public class AreaProcessingTest {
	@Test
	public void testCase1(){
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile("testdata/AreaTransTestData.DAT"));
		
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
		parser.stdout();
		
		String bpaStr="";
		//process head info
		BpaAclfNetworkRecord netRecord=new BpaAclfNetworkRecord(parser);
		bpaStr=netRecord.setNetHeadInfo("Test", "AreaTest",bpaStr);
		 //process area data and interarea power exchange data,if any
		
		BpaAreaPowerExchgRecord areaExchg=new BpaAreaPowerExchgRecord(parser);
		bpaStr=areaExchg.setAreaRecord(bpaStr);
			try {
				bpaStr=areaExchg.setInterAreaExchangeRecord(bpaStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 String[] line=bpaStr.split("\n");
		 System.out.println(bpaStr);
		 System.out.println(line[9]);
		 String line9="AC+GD                              HY CC JY ST SW ";
		 assertTrue(line[9].equals(line9));
		 //System.out.println(bpaStr);
	}

}
