package org.ieee.odm.pwd;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.pwd.PWDAdapterForContingency;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.BranchChangeRecSetXmlType;
import org.ieee.odm.schema.BranchChangeRecXmlType;
import org.ieee.odm.schema.NetModificationXmlType;
import org.junit.Test;

public class PWD_Contingency_test {
	
		@Test
		public void ContingencySample_test(){
			IODMAdapter adapter = new PWDAdapterForContingency();
			assertTrue(adapter.parseInputFile("testdata/pwd/ctg_sample.AUX"));
			AclfModelParser parser=(AclfModelParser) adapter.getModel();
			parser.stdout();
			
            
			//check network data
			NetModificationXmlType netModify=(NetModificationXmlType) parser.getStudyCase().getModificationList().getModification().get(0);
			List<BranchChangeRecSetXmlType> branchChangeRecSetList=netModify.getBranchChangeRecSetList();
			//rec set
			BranchChangeRecSetXmlType braRecSet=branchChangeRecSetList.get(0);
			//System.out.println("RecSetList.size()="+branchChangeRecSetList.size());
			assertTrue(braRecSet.getBranchChangeRecords().size()==5);
			
			//rec element
			BranchChangeRecXmlType braCtgElement=braRecSet.getBranchChangeRecords().get(0);
			assertTrue(braCtgElement.getBranchId().equals("Bus7514_to_Bus7512_cirId_1"));
			assertTrue(braCtgElement.isOffLine()==true);
			 
		
			
			
			
		}

}
