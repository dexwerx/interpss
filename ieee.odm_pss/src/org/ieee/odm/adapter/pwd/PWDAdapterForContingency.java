package org.ieee.odm.adapter.pwd;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.modify.NetModificationHelper;
import org.ieee.odm.schema.BranchChangeRecXmlType;
import org.ieee.odm.schema.NetModificationXmlType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
 /**
  * PowerWorld-TO-ODM Adapter based on power world v16 data definition
  * 
  * @version 0.1  04/03/2012
  * @author Tony Huang
  * 
  */
public class PWDAdapterForContingency extends AbstractODMAdapter{

	public PWDAdapterForContingency(){
		super();
	}
	
	@Override
	protected IODMModelParser parseInputFile(IFileReader din, String encoding) {
		AclfModelParser parser=new AclfModelParser(encoding);
		
		parser.setLFTransInfo(OriginalDataFormatEnumType.POWER_WORLD);
		
		NetModificationHelper helper = new NetModificationHelper(parser);
		
		NetModificationXmlType netModList = helper.createNetModificationList();
		
		BranchChangeRecXmlType branchChange = helper.createBranchChangeRecXmlType(netModList);
		
		branchChange.setBranchId("");
		// ...

		return parser;
	}
	
	@Override
	protected IODMModelParser parseInputFile(NetType type, IFileReader[] din, String encoding) {
		ODMLogger.getLogger().severe("Method not implemented");
		return null;
	}	
}
