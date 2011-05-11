/*
 * @(#)BPADynamicRecord.java   
 *
 * Copyright (C) 2006-2008 www.interpss.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @Author Stephen Hau
 * @Version 1.0
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */
package org.ieee.odm.adapter.bpa.impl.dynamic;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.adapter.bpa.impl.BusRecord;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.model.dstab.DStabDataSetter;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.ClassicMachineXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.Eq11Ed11MachineXmlType;
import org.ieee.odm.schema.Eq1Ed1MachineXmlType;
import org.ieee.odm.schema.GeneratorEnumType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.XfrDStabXmlType;
import org.ieee.odm.schema.XfrZeroSeqConnectLocationEnumType;
import org.ieee.odm.schema.ZUnitType;


public class BPADynamicSequenceRecord {

	public static void processSequenceData(String str, DStabModelParser parser) throws ODMException {				
		
		final String strAry[]=getSequenceDataFields(str);		
		
		if(strAry[0].equals("XO")){
			final String fromId = BusRecord.getBusId(strAry[1]);
			final String toId = BusRecord.getBusId(strAry[3]);
			String cirId="1";
			if(!strAry[6].equals("")){
				cirId=strAry[6];				
			}			
			// retrieve the branch from the parser
			BranchXmlType branch = (BranchXmlType)parser.getBranch(fromId, toId, cirId);
			if (branch == null) {
				throw new ODMException("Branch not found " + fromId + "->" + toId + "(" + cirId + ")");
			}
			// cast the branch from aclfXfr to dstabXfr
			XfrDStabXmlType xfr = (XfrDStabXmlType)ModelStringUtil.casting(branch, "aclfXfr", "dstabXfr");
			
			//TODO "get" or "create"?In other data cards have the same problem exists.
			//XfrDStabXmlType xfr =(XfrDStabXmlType)parser.getXfrBranch(fromId, toId, cirId);
			//TODO Do we have to set the following rated Voltage?
			//When we get the XfrBranch with the method"parser.getXfrBranch()",if these Info are included?
			double fVbase=ModelStringUtil.getDouble(strAry[2], 0.0);
			xfr.getXfrInfo().setFromRatedVoltage(DStabDataSetter.createVoltageValue(fVbase, VoltageUnitType.KV));
			double tVbase=ModelStringUtil.getDouble(strAry[4], 0.0);
			xfr.getXfrInfo().setToRatedVoltage(DStabDataSetter.createVoltageValue(tVbase, VoltageUnitType.KV));
			
			int location= new Integer(strAry[5]).intValue();
			if(location==1){
				xfr.getXfrZeroSeq().setConectionLocation(XfrZeroSeqConnectLocationEnumType.AT_BUS_1);
			}else if(location==2){
				xfr.getXfrZeroSeq().setConectionLocation(XfrZeroSeqConnectLocationEnumType.AT_BUS_2);
			}else {
				xfr.getXfrZeroSeq().setConectionLocation(XfrZeroSeqConnectLocationEnumType.BETWEEN_BUS_1_N_BUS_2);
			}
			xfr.setCircuitId(cirId);
			//X0
			double x0=ModelStringUtil.getDouble(strAry[7], 0.0);
			xfr.getZ0().setRe(x0);
						
			//R0
			double r0=ModelStringUtil.getDouble(strAry[8], 0.0);
			xfr.getZ0().setIm(r0);
		}
	    else if(strAry[0].equals("XR")){
	    	final String busId = BusRecord.getBusId(strAry[1]);
        	DStabBusXmlType bus = parser.getDStabBus(busId);
        	double busBase = ModelStringUtil.getDouble(strAry[2], 0.0);
        	bus.setBaseVoltage(DStabDataSetter.createVoltageValue(busBase, VoltageUnitType.KV));
        	double r0 = ModelStringUtil.getDouble(strAry[3], 0.0);
        	double x0 = ModelStringUtil.getDouble(strAry[4], 0.0);
        	bus.getScShuntLoadData().getZeroZ().setRe(r0);
        	bus.getScShuntLoadData().getZeroZ().setIm(x0);
	    }
	    else if(strAry[0].equals("LO")){
	    	final String fromId = BusRecord.getBusId(strAry[1]);
			final String toId = BusRecord.getBusId(strAry[3]);
			String cirId="1";
			if(!strAry[6].equals("")){
				cirId=strAry[6];				
			}			
			// retrieve the branch from the parser
			BranchXmlType branch = (BranchXmlType)parser.getBranch(fromId, toId, cirId);
			if (branch == null) {
				throw new ODMException("Branch not found " + fromId + "->" + toId + "(" + cirId + ")");
			}
	    	LineDStabXmlType line = (LineDStabXmlType)ModelStringUtil.casting(branch, "aclfLine", "dstabLine");
			//TODO can't set the rated voltage of frombus and tobus .When we get the branch,these info have been included?
			
			//R0			
			double r0=ModelStringUtil.getDouble(strAry[7], 0.0);
			line.getZ0().setRe(r0);
			    		
    		//X0
			double x0=ModelStringUtil.getDouble(strAry[8], 0.0);
			line.getZ0().setIm(x0);
			     		
    		//G1
			double g1=ModelStringUtil.getDouble(strAry[9], 0.0);
			line.getY0ShuntFromSide().setRe(g1);
			   		
    		//B1
			double b1=ModelStringUtil.getDouble(strAry[10], 0.0);
			line.getY0ShuntFromSide().setIm(b1);
			     		
    		//G2
    		double g2=ModelStringUtil.getDouble(strAry[11], 0.0);
    		line.getY0ShuntToSide().setRe(g2);
			     		
    		//B2
			double b2=ModelStringUtil.getDouble(strAry[12], 0.0);
			line.getY0ShuntToSide().setIm(b2);
	    }
	    else if(strAry[0].equals("LM")){
	    	final String line1fId = BusRecord.getBusId(strAry[1]);
			final String line1tId = BusRecord.getBusId(strAry[3]);
			String line1cirId="1";
			if(!strAry[6].equals("")){
				line1cirId=strAry[5];				
			}			
			// retrieve the branch from the parser
			BranchXmlType branch1 = (BranchXmlType)parser.getBranch(line1fId, line1tId, line1cirId);
			if (branch1 == null) {
				throw new ODMException("Branch not found " + line1fId + "->" + line1tId + "(" + line1cirId + ")");
			}
	    	LineDStabXmlType line1 = (LineDStabXmlType)ModelStringUtil.casting(branch1, "aclfLine", "dstabLine");
			
			final String line2fId =  BusRecord.getBusId(strAry[6]);
			final String line2tId =  BusRecord.getBusId(strAry[8]);
			String line2cirId="1";
			if(!strAry[10].equals("")){
				line2cirId=strAry[10];				
			}
			BranchXmlType branch2 = (BranchXmlType)parser.getBranch(line2fId, line2tId, line2cirId);
			if (branch2 == null) {
				throw new ODMException("Branch not found " + line2fId + "->" + line2tId + "(" + line2cirId + ")");
			}
			LineDStabXmlType line2 = (LineDStabXmlType)ModelStringUtil.casting(branch2, "aclfLine", "dstabLine");
			
			//TODO How to get the zero-sequence mutual inductance between line1 and line2?
			double rm=ModelStringUtil.getDouble(strAry[11], 0.0);
			double xm=ModelStringUtil.getDouble(strAry[12], 0.0);
			line1.getLineMutualZeroZ().get(0).getZM().setRe(rm);
			line1.getLineMutualZeroZ().get(0).getZM().setRe(xm);
			line2.getLineMutualZeroZ().get(0).getZM().setRe(rm);
			line2.getLineMutualZeroZ().get(0).getZM().setRe(xm);			
	    }
	}
/*
		}else if(strAry[0].equals("LM")){
			ZeroSequenceDataListXmlType.MutualImpedanceZeroList.MutualImpedanceZero mutZero=
				XBeanTranStabSimuHelper.addNewMutualZero(tranSimu);
			//bus1
			String bus1=strAry[1];
			mutZero.addNewBranch1BusI().setName(bus1);			
			//bus2
			String bus2=strAry[3];
			mutZero.addNewBranch1BuJ().setName(bus2);
			//par
			String cirId="";
			if(!strAry[5].equals("")){
				cirId=strAry[5];
				mutZero.setBranch1CirId(cirId);
			}	    		
    		//busK line2
			String bus3=strAry[6];
			mutZero.addNewBranch2BusK().setName(bus3);			
			//bus2
			String bus4=strAry[8];
			mutZero.addNewBranch2BusL().setName(bus4);    		
    		//par2
			String cir2Id="";
			if(!strAry[10].equals("")){
				cirId=strAry[10];
				mutZero.setBranch2CirId(cir2Id);
			}	    		
			//R0			
			double r0=ModelStringUtil.getDouble(strAry[11], 0.0);
			mutZero.setRM(r0);
			
    		//X0
			double x0=ModelStringUtil.getDouble(strAry[12], 0.0);
			mutZero.setXM(x0);
		}
*/		
	public static void processNegativeData( DStabModelParser parser){
		// negative sequence generator data
		for( JAXBElement<? extends BusXmlType> busXml:parser.getDStabNet().getBusList().getBus()){
			DStabBusXmlType bus=(DStabBusXmlType)busXml.getValue();
			for(DynamicGeneratorXmlType dynGen:bus.getDynamicGenList().getDynamicGen()){
		//for(DynamicGeneratorXmlType dynGen:parser.getFactory().createDStabBusXmlTypeDynamicGenList().getDynamicGen()){
				double xd1=0.0;
				double x2=0.0;
				double tq01=0.0;
				if(dynGen.getMachineModel().equals(GeneratorEnumType.SUB_TRANSIENT)){
					Eq11Ed11MachineXmlType subGen=(Eq11Ed11MachineXmlType)dynGen.getMachineModel().getValue();
					xd1=subGen.getXd1();
					tq01=subGen.getTq01().getValue();				
				}else if(dynGen.getMachineModel().equals(GeneratorEnumType.TRANSIENT)){
					Eq1Ed1MachineXmlType tranGen=(Eq1Ed1MachineXmlType)dynGen.getMachineModel().getValue();
					xd1=tranGen.getXd1();
					tq01=tranGen.getTq01().getValue();
				}else if(dynGen.getMachineModel().equals(GeneratorEnumType.CLASSICAL)){
					ClassicMachineXmlType claGen=(ClassicMachineXmlType)dynGen.getMachineModel().getValue();
					xd1=claGen.getXd1();
					tq01=0.0;  //TODO Why the tq01 in classic model is equal to 0.0?		
				}
				//TODO b= X”d/X’d , b can be set in the CASE card,the default value is 0.65 .
				//so how can we get the b value from the CASE card?
				//non-salient pole machine
				if(tq01!=0.0){
					x2=1.22*0.65*xd1;
				}
				//salient pole generator 
				else{
					x2=0.65*xd1;
				}
				//TODO How to set the negative sequence impedance to associate to generator?
				bus.getScGenData().setNegativeZ(DStabDataSetter.createZValue(0.0, x2, ZUnitType.PU));
				//How about the case that several generators is in parallel on the bus?
				
				//parser.getFactory().createSequenceBusDataXmlTypeGenData().setNegativeZ(DStabDataSetter.createZValue(0.0, x2, ZUnitType.PU));
			}			
		}		
/*						
		for(GeneratorXmlType gen:tranSimu.getDynamicDataList().getBusDynDataList()
				.getGeneratorDataList().getGeneratorArray()){
			double xd1=0.0;
			double x2=0.0;
			double tq01=0.0;
			if(gen.getGeneratorType().equals(GeneratorXmlType.GeneratorType.SUBTRANS_MODEL)){
				SubTransientMachineXmlType subGen=
					gen.getGeneratorModel().getSubTransientModel();
				xd1=subGen.getXd1();
				tq01=subGen.getTq01().getValue();				
			}else if(gen.getGeneratorType().equals(GeneratorXmlType.GeneratorType.TRANSIENT_MODEL)){
				TransientMachineXmlType tranGen=
					gen.getGeneratorModel().addNewTransModel();
					xd1=tranGen.getXd1();
					tq01=tranGen.getTq01().getValue();
			}else if(gen.getGeneratorType().equals(GeneratorXmlType.GeneratorType.CLASSICAL_MODEL)){
				ClassicMachineXmlType claGen=
					gen.getGeneratorModel().getClassicalModel();
				xd1=claGen.getXd1();
				tq01=0.0;;				
			}
			//non-salient pole machine
			if(tq01!=0.0){
				x2=1.22*0.65*xd1;
			}
			//salient pole generator 
			else{
				x2=0.65*xd1;
			}
			NegativeSequenceDataListXmlType.GeneratorNegativeList.GeneratorNegative xfrNeg=
				XBeanTranStabSimuHelper.addNewGenNeg(tranSimu);
			
			String busId=gen.getLocatedBus().getName();
			String genId="";
			if(gen.getGenId()!=null){
				genId=gen.getGenId().getName();
				xfrNeg.addNewMacId().setName(genId);
			}
			xfrNeg.addNewBusId().setName(busId);
			xfrNeg.setZXNeg(x2);			
		}
*/
		// negative sequence load data
		for( JAXBElement<? extends BusXmlType> busXml:parser.getDStabNet().getBusList().getBus()){
			DStabBusXmlType bus=(DStabBusXmlType)busXml.getValue();
			//TODO Judge whether this node is load node?
			if(!bus.getLoadData().getEquivLoad().equals(null)){
				//TODO 这里将负荷负序导纳等效成对地支路负序阻抗，但节点本身的并联接地支路的负序参数呢？
				//hard coded values
				bus.getScShuntLoadData().getNegativeZ().setRe(0.19);
				bus.getScShuntLoadData().getNegativeZ().setIm(0.36);
			}
		}		
/*
		for( LoadCharacteristicXmlType load: tranSimu.getDynamicDataList().
				getBusDynDataList().getLoadCharacteristicDataList().getLoadArray() ){
			NegativeSequenceDataListXmlType.ShuntLoadNegativeList.ShuntLoadNegative loadNeg=
				XBeanTranStabSimuHelper.addNewShuntLoadNeg(tranSimu);
			
			if(load.getLocation().equals(LoadCharacteristicXmlType.Location.AT_AREA)){
				loadNeg.setLoadLocation(NegativeSequenceDataListXmlType.ShuntLoadNegativeList.
						ShuntLoadNegative.LoadLocation.AT_AREA);
				
			}else if(load.getLocation().equals(LoadCharacteristicXmlType.Location.AT_BUS)){
				loadNeg.setLoadLocation(NegativeSequenceDataListXmlType.ShuntLoadNegativeList.
						ShuntLoadNegative.LoadLocation.AT_BUS);
			}else {
				loadNeg.setLoadLocation(NegativeSequenceDataListXmlType.ShuntLoadNegativeList.
						ShuntLoadNegative.LoadLocation.AT_ZONE);
			}			
			loadNeg.addNewLocationId().setName(load.getLocationId().getName());
// TODO: hard coded values
			loadNeg.setRNeg(0.19);
			loadNeg.setXNeg(0.36);
		}
*/			
	}	
	
	private static String[] getSequenceDataFields(String str){
		final String[] strAry= new String[13];
		
		try{
			if(str.substring(0, 2).startsWith("XO")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		//bus1
	    		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,5, 12).trim();
	    		//bus1 Voltage
	    		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,13, 16).trim();
	    		//bus2
	    		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,19, 26).trim();
	    		//bus2 Voltage
	    		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,27, 30).trim();
	    		//zrLocation
	    		strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,32, 32).trim();
	    		//par
	    		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,34, 34).trim();
	    		//X0
	    		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,38, 44).trim();
	    		//R0
	    		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,45, 51).trim();		
				
			}else if(str.substring(0, 2).startsWith("XR")){			
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		//bus1
	    		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,5, 12).trim();
	    		//bus1 Voltage
	    		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,13, 16).trim();
	    		//R0
	    		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,22, 28).trim();
	    		//X0
	    		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,29, 35).trim();
			}else if(str.substring(0, 2).startsWith("LO")){			
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		//bus1
	    		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,5, 12).trim();
	    		//bus1 Voltage
	    		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,13, 16).trim();
	    		//bus2
	    		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,19, 26).trim();
	    		//bus2 Voltage
	    		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,27, 30).trim();    		
	    		//par
	    		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,33, 33).trim();
	    		//R0
	    		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,36, 42).trim();
	    		//X0
	    		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,43, 49).trim();
	    		//G1
	    		strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,50, 56).trim();
	    		//B1
	    		strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,57, 63).trim();
	    		//G2
	    		strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,64, 70).trim();
	    		//B2
	    		strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,71, 77).trim();
			}else if(str.substring(0, 2).startsWith("LM")){
				strAry[0]=ModelStringUtil.getStringReturnEmptyString(str,1, 2).trim();
	    		//busI line 1
	    		strAry[1]=ModelStringUtil.getStringReturnEmptyString(str,5, 12).trim();
	    		//busI Voltage
	    		strAry[2]=ModelStringUtil.getStringReturnEmptyString(str,13, 16).trim();
	    		//busJ line 1
	    		strAry[3]=ModelStringUtil.getStringReturnEmptyString(str,19, 26).trim();
	    		//busJ Voltage
	    		strAry[4]=ModelStringUtil.getStringReturnEmptyString(str,27, 30).trim();    		
	    		//par1
	    		strAry[5]=ModelStringUtil.getStringReturnEmptyString(str,33, 33).trim();
	    		//busK line2
	    		strAry[6]=ModelStringUtil.getStringReturnEmptyString(str,36, 43).trim();
	    		//busK voltage
	    		strAry[7]=ModelStringUtil.getStringReturnEmptyString(str,44, 47).trim();
	    		//busL line2
	    		strAry[8]=ModelStringUtil.getStringReturnEmptyString(str,50, 57).trim();
	    		//busL voltage
	    		strAry[9]=ModelStringUtil.getStringReturnEmptyString(str,58, 61).trim();
	    		//par2
	    		strAry[10]=ModelStringUtil.getStringReturnEmptyString(str,64, 64).trim();
	    		//Rm
	    		strAry[11]=ModelStringUtil.getStringReturnEmptyString(str,67, 73).trim();
	    		//Xm
	    		strAry[12]=ModelStringUtil.getStringReturnEmptyString(str,74, 80).trim();
			}
		}catch(Exception e){
			ODMLogger.getLogger().severe(e.toString());
		}				
		return strAry;
	}	
}
