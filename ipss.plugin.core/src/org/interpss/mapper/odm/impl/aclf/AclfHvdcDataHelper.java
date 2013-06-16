package org.interpss.mapper.odm.impl.aclf;

import org.ieee.odm.schema.DCLineData2TXmlType;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.hvdc.HvdcLine2T;


public class AclfHvdcDataHelper {
	private AclfNetwork aclfNet = null;
	private HvdcLine2T hvdc2T = null;
	
	public AclfHvdcDataHelper(AclfNetwork aclfNet, HvdcLine2T hvdc2T){
		this.aclfNet = aclfNet;
		this.hvdc2T = hvdc2T;
	}
	//TODO to handle two kinds of HVDC under the same method
	/*
	public boolean setHvdcData(DCLineDataXmlType hvdcXml){
		if(hvdcXml instanceof DCLineData2TXmlType){
			setHvdc2TData((DCLineData2TXmlType)hvdcXml)
		}
		else
           setVSCHvdcData(DCLineDataVSCXmlType)hvdcXml)
	}
	*/
	private boolean setHvdc2TData(DCLineData2TXmlType hvdc2TXml){
		return false;
		
	}
	/*
    private boolean setVSCHvdcData(DCLineDataVSCXmlType hvdc2TXml){
		
	}
	*/
}
