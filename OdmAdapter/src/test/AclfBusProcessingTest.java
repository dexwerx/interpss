package test;

import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.junit.Test;

import BPA.BpaAclfBusRecord;
import BPA.BpaAclfNetworkRecord;

public class AclfBusProcessingTest {
	@Test
	public void testCase1(){
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile("testdata/IEEE9.dat"));
		
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
		parser.stdout();
		String bpa="";
		for(JAXBElement<? extends BusXmlType> bus:parser.getAclfNet().getBusList().getBus()){
			LoadflowBusXmlType busXml=(LoadflowBusXmlType) bus.getValue();
			BpaAclfBusRecord bpaBusRecord=new BpaAclfBusRecord(parser);
			bpaBusRecord.setAclfBusData(bpa,busXml);
			System.out.println(bpaBusRecord.stdOut());
		}
	}

}
