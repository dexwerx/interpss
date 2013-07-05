package org.ieee.odm.adapter.psse;

import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DStabNetXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.PSXfrDStabXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;

public class PSSEDynAdapter extends PSSEAcscAdapter<DStabNetXmlType, DStabBusXmlType, LineDStabXmlType, XfrDStabXmlType, PSXfrDStabXmlType>{

	public PSSEDynAdapter(PsseVersion ver) {
		super(ver);
		
	}
	
	@Override protected IODMModelParser parseInputFile(final IFileReader din, String encoding) throws Exception {
		 throw new UnsupportedOperationException("parse dstab data is not supported yet!");
	}
	

	@Override
	protected IODMModelParser parseInputFile(NetType type, IFileReader[] din,
			String encoding) throws Exception {
		if(type !=NetType.DStabNet){
			// initialize the parser first
			throw new ODMException("input type is not Dstab NetType, please check ");
			
		}
		
			// the parser is supposed to be set at the PSSEDstabParser class, which call this method
		if(parser == null){
				parser = new DStabModelParser();
		}
		
		//Use the Acsc Parser to parse the first two files, namely, Aclf and Sequence data.
		super.parseInputFile(type, din, encoding);
		
	    //It is supposed that the third file defines the Dstab data.
		this.parseInputFile(din[2], encoding);
		
		return parser;
	}

}
