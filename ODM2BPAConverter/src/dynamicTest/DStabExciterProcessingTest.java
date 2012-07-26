package dynamicTest;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.interpss.odm.converter.BPA.dynamic.BpaDynamicExciterRecord;
import org.junit.Test;



public class DStabExciterProcessingTest {
	@Test
	public void testCase1() throws ODMException{
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile(IODMAdapter.NetType.DStabNet,
				new String[] { "testdata/IEEE90.DAT","testdata/ieee90.swi"},IODMModelParser.chineseEncoding));		
		DStabModelParser parser = (DStabModelParser)adapter.getModel();
		parser.stdout();
		
		String bpa="";		
		NetworkXmlType.BusList busList=parser.getDStabNet().getBusList();
		int size=busList.getBus().size();
		for(int i=0;i<size;i++){
			BusXmlType Bus = busList.getBus().get(i).getValue();
			DStabBusXmlType DStabBus=parser.getDStabBus(Bus.getId());
			if(DStabBus.getDynamicGen()!=null){
				for(DynamicGeneratorXmlType dynGen:DStabBus.getDynamicGen()){
					if(dynGen.getExciter()!=null){
						BpaDynamicExciterRecord bpaExcRecord=new BpaDynamicExciterRecord(parser);
						bpaExcRecord.setDynamicExciterData(bpa, DStabBus, dynGen);
						System.out.println(bpaExcRecord.stdout());
					}
				}
			}
	    }
	}
}
