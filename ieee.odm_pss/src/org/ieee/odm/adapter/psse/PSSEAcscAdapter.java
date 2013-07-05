package org.ieee.odm.adapter.psse;

import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.NetworkXmlType;

public class PSSEAcscAdapter <
TNetXml extends NetworkXmlType, 
TBusXml extends BusXmlType,
TLineXml extends BranchXmlType,
TXfrXml extends BranchXmlType,
TPsXfrXml extends BranchXmlType> extends PSSELFAdapter<TNetXml, TBusXml, TLineXml, TXfrXml, TPsXfrXml>{

	public PSSEAcscAdapter(PsseVersion ver) {
		super(ver);
		// TODO Auto-generated constructor stub
	}
	
	
	/*
	 *First record in the first valid line: Change code -- IC
	 *
	 *IC = 0 indicates initialization setting is enabled, those with
	 *no data record entered are set to be their default values.
	 *
	 *IC = 1 indicates modification/update setting is enabled, the records defined
	 *in the input sequence data will be changed to the new value, while the rest are unchanged
	 *
	 */
     
	
	
	/*
	 * post processing
	 * 
	 * 1. Gen zero sequence
	 * 
	 * During the initial input of sequence data (i.e., IC = 0 on the first data record), any machine for which
       no data record of this category is entered has its zero sequence generator impedance, ZZERO, set
       equal to ZPOS, the positive sequence generator impedance
       
       For those machines at which the step-up transformer is represented as part of the generator data
      (i.e., XTRAN is non-zero), ZZERO (i.e., RZERO + j XZERO) is not used and, in the fault analysis
       activities, the step-up transformer is assumed to be a delta wye transformer. Refer to Modeling of
       Generator Step-Up Transformers (GSU).
       
	 * 
	 * 2. bus negative sequence shunt load
	 * 
	 * For any bus where no such data record is specified, or GNEG and BNEG are both specified as zero,
       the load elements are assumed to be equal in the positive and negative sequence networks.
	 * 
	 */
	
	@Override protected IODMModelParser parseInputFile(final IFileReader din, String encoding) throws Exception {
		 throw new UnsupportedOperationException("parse acsc data alone, without load flow info, is not supported yet!");
	}
	
	
	
	/**
	 * 
	 */
	@Override
	protected IODMModelParser parseInputFile(NetType type, IFileReader[] din,
			String encoding) throws Exception {
		if(type ==NetType.AcscNet){
			// initialize the parser first
			if(parser == null) setParser(new AcscModelParser());
			
		}
		else if(type==NetType.DStabNet){
			// the parser is supposed to be set at the PSSEDstabParser class, which call this method
			if(parser == null){
				throw new ODMException("Parser is not initialized before parsing Acsc data for Dstab NetType1 ");
			}
		}
		
		if(parser != null){
		//the first file is supposed to be the Load flow data file
		super.parseInputFile(din[0], encoding);
		
		//the second one should be the sequence data file;
		this.parseInputFile(din[1], encoding);
		}
		
		return parser;
	}
	 
}
