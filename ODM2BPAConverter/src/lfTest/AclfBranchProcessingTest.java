package lfTest;

import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.interpss.odm.converter.BPA.lf.BpaAclfBranchRecord;
import org.junit.Test;

public class AclfBranchProcessingTest {
	@Test
	public void testCase1(){
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile("testdata/IEEE9.dat"));
		
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
		parser.stdout();
		String bpa="";
		for(JAXBElement<? extends BaseBranchXmlType> b:parser.getAclfNet().getBranchList().getBranch()){
			BaseBranchXmlType xmlBranch = b.getValue();
			BpaAclfBranchRecord bpaBraRecord=new BpaAclfBranchRecord(parser);
			bpaBraRecord.setAclfBranchData(bpa, xmlBranch);
			System.out.println(bpaBraRecord.stdout());
		 

	    }
	}

}
