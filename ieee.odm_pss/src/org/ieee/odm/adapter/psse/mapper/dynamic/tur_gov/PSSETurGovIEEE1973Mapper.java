package org.ieee.odm.adapter.psse.mapper.dynamic.tur_gov;

import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.adapter.psse.mapper.aclf.BasePSSEDataMapper;
import org.ieee.odm.adapter.psse.parser.dynamic.tur_gov.PSSETurGovIEEE1973Parser;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.model.dstab.DStabParserHelper;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DStabGenDataXmlType;
import org.ieee.odm.schema.GovIEEE1981Type1XmlType;
import org.ieee.odm.schema.GovSteamTCSRXmlType;

public class PSSETurGovIEEE1973Mapper extends BasePSSEDataMapper{
    
	public PSSETurGovIEEE1973Mapper(PsseVersion ver) {
		super(ver);
		this.dataParser = new PSSETurGovIEEE1973Parser(ver);
	}
	
	
	public void procLineString(String lineStr, DStabModelParser parser) throws ODMException {
		this.dataParser.parseFields(lineStr);
		
	/*
	  //  0----------1----------2----------3----------4
		 "IBUS", "Type",   "MachId",   "T1",       "T2",
	  //  5----------6----------7----------8----------9
	     "T3",     "T4",      "T5",      "T6",     "K1",  	 
	  //  10----------11--------12---------13---------14	
		 "K2",      "K2",       "K3",   "PMAX",   "PMIN"	
	 */			
		
		/*
		 * PSSE IEESGO, corresponds to IEEE 1973 Tandem Compound, Single Reheat.
		 * 
		 * T4        = Tch
		 * 1-K2      = Fhp
		 * K2*(1-K3) = Fip
		 * K2*K3     = Flp
		 * 
		*/
		int i = dataParser.getInt("IBUS");
	    final String busId = AbstractModelParser.BusIdPreFix+i;
	    String genId = dataParser.getString("MachId");
	    
	    //check model type
	    if(!dataParser.getString("Type").equals("IEEET1")){
	    	throw new ODMException(" Exciter of machine  : Id"+
		             genId+" @ Bus"+i+"is not a IEEET1 type");
	    }

	   DStabBusXmlType busXml = parser.getBus(busId);
	    
	   DStabGenDataXmlType dstabGenData = DStabParserHelper.getDStabContritueGen(busXml, genId);
	   
	   GovSteamTCSRXmlType gov = DStabParserHelper.createGovSteamTCSRXmlType(dstabGenData);
	   
	   
	
	
	}
}
