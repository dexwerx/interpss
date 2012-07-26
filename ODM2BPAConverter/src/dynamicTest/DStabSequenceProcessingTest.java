package dynamicTest;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.bpa.BPAAdapter;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.MutualZeroZXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;
import org.interpss.odm.converter.BPA.dynamic.BpaDynamicSequenceRecord;
import org.junit.Test;


public class DStabSequenceProcessingTest {
	@Test
	public void testCase1() throws ODMException{
		IODMAdapter adapter = new BPAAdapter();
		assertTrue(adapter.parseInputFile(IODMAdapter.NetType.DStabNet,
				new String[] { "testdata/IEEE90.DAT","testdata/ieee90.swi"},IODMModelParser.chineseEncoding));		
		DStabModelParser parser = (DStabModelParser)adapter.getModel();
		parser.stdout();
		
		String bpa="";		
		NetworkXmlType.BranchList branchList=parser.getDStabNet().getBranchList();
		int branchSize=branchList.getBranch().size();
		for(int i=0;i<branchSize;i++){
			BaseBranchXmlType Branch = branchList.getBranch().get(i).getValue();
			if(Branch instanceof LineDStabXmlType) {
				LineDStabXmlType line=(LineDStabXmlType)Branch;
				if(line.getZ0()!=null || line.getY0ShuntFromSide()!=null || line.getY0ShuntToSide()!=null){
					BpaDynamicSequenceRecord bpaSeqRecord=new BpaDynamicSequenceRecord(parser);
					bpaSeqRecord.setDynamicSequenceData(bpa, line);
					System.out.println(bpaSeqRecord.stdout());
				}
				if(line.getLineMutualZeroZ().size()!=0){
					BpaDynamicSequenceRecord bpaSeqRecord=new BpaDynamicSequenceRecord(parser);
					bpaSeqRecord.setDynamicMutualZeroData(bpa, line);
					System.out.println(bpaSeqRecord.stdout());
				}
			}
			else if(Branch instanceof XfrDStabXmlType) {
				XfrDStabXmlType xfr=(XfrDStabXmlType)Branch;
				if(xfr.getZ0()!=null){
					BpaDynamicSequenceRecord bpaSeqRecord=new BpaDynamicSequenceRecord(parser);
					bpaSeqRecord.setDynamicSequenceData(bpa, xfr);
					System.out.println(bpaSeqRecord.stdout());
				}
			}
	    }
		NetworkXmlType.BusList busList=parser.getDStabNet().getBusList();
		int busSize=busList.getBus().size();
		for(int i=0;i<busSize;i++){
			BusXmlType Bus = busList.getBus().get(i).getValue();
			DStabBusXmlType DStabBus=parser.getDStabBus(Bus.getId());
			if(DStabBus.getShuntLoadZeroZ()!=null){
				BpaDynamicSequenceRecord bpaSeqRecord=new BpaDynamicSequenceRecord(parser);
				bpaSeqRecord.setDynamicSequenceData(bpa, DStabBus);
				System.out.println(bpaSeqRecord.stdout());
			}
		}
	}
}
