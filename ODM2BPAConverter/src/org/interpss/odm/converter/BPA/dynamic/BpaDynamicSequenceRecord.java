package org.interpss.odm.converter.BPA.dynamic;

import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.MutualZeroZXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;
import org.ieee.odm.schema.XfrZeroSeqConnectLocationEnumType;

import util.StringUtil;

public class BpaDynamicSequenceRecord {
	private static DStabBusXmlType busXml=null;
	private static BaseBranchXmlType braXml=null;
	private static StringBuffer seqStr=null;
	private static double baseMVA=100.0D;
	private static DStabModelParser parser=null;
	
	public BpaDynamicSequenceRecord(DStabModelParser parser){
		this.seqStr=new StringBuffer();
		this.parser=parser;
		this.baseMVA=parser.getDStabNet().getBasePower().getValue();		
	}
	public String setDynamicSequenceData(String bpaLfNetStr,BaseBranchXmlType DStabBranchXml){
		braXml=DStabBranchXml;
		seqStr=new StringBuffer();
		setDynamicSequenceBaseData();
		setDynamicSequenceDSData();
		//set type specific data;
		
		// add to the net
		bpaLfNetStr+=seqStr+"\n";
		return bpaLfNetStr;		
	}
	private static void setDynamicSequenceBaseData(){
		//get type
		String type=getType();
		//fromBusName ,BaseVoltage
		DStabBusXmlType fromBus=(DStabBusXmlType)braXml.getFromBus().getIdRef();
		String fBusName=StringUtil.addSpace(fromBus.getName(),8);
		double fBusRatedV=fromBus.getBaseVoltage().getValue();
		String fBusRatedVStr=StringUtil.format(fBusRatedV, 4, 0);
		//toBusName ,BaseVoltage
		DStabBusXmlType toBus=(DStabBusXmlType)braXml.getToBus().getIdRef();
		String tBusName=StringUtil.addSpace(toBus.getName(),8);
		double tBusRatedV=toBus.getBaseVoltage().getValue();
		String tBusRatedVStr=StringUtil.format(tBusRatedV, 4, 0);
		
		seqStr.append(type).append(StringUtil.getSpace(2)).append(fBusName).append(fBusRatedVStr)
			.append(StringUtil.getSpace(2)).append(tBusName).append(tBusRatedVStr);		
	}
	private static void setDynamicSequenceDSData() {
		if(getType()=="LO") setLOData();
		else if(getType()=="XO") setXOData();
	}
	private static void setLOData() {
		LineDStabXmlType line=(LineDStabXmlType)braXml;
		//PAR平行码
		String Par=(braXml.getCircuitId()=="1")?" ":braXml.getCircuitId();
		String R0Str=StringUtil.getSpace(7);
		String X0Str=StringUtil.getSpace(7);
		if(line.getZ0()!=null){
			//R0
			double R0=line.getZ0().getRe();
			R0Str=StringUtil.format(R0, 7, 4);
			//X0
			double X0=line.getZ0().getIm();
			X0Str=StringUtil.format(X0, 7, 4);
		}
		String Ga0Str=StringUtil.getSpace(7);
		String Ba0Str=StringUtil.getSpace(7);
		String Gb0Str=StringUtil.getSpace(7);
		String Bb0Str=StringUtil.getSpace(7);
		if(line.getY0ShuntFromSide()!=null||line.getY0ShuntToSide()!=null){
			//Ga0
			double Ga0=line.getY0ShuntFromSide().getRe();
			Ga0Str=StringUtil.format(Ga0, 7, 4);
			//Ba0
			double Ba0=line.getY0ShuntFromSide().getIm();
			Ba0Str=StringUtil.format(Ba0, 7, 4);
			//Gb0
			double Gb0=line.getY0ShuntToSide().getRe();
			Gb0Str=StringUtil.format(Gb0, 7, 4);
			//Bb0
			double Bb0=line.getY0ShuntToSide().getIm();
			Bb0Str=StringUtil.format(Bb0, 7, 4);
		}
		seqStr.append(StringUtil.getSpace(2)).append(Par).append(StringUtil.getSpace(2)).append(R0Str)
			.append(X0Str).append(Ga0Str).append(Ba0Str).append(Gb0Str).append(Bb0Str);
	}
	private static void setXOData() {
		XfrDStabXmlType xfr=(XfrDStabXmlType)braXml;
		//ConectionLocation
		String SID=" ";
		if(xfr.getXfrZeroSeq().getConectionLocation()==XfrZeroSeqConnectLocationEnumType.AT_BUS_1)
			SID="1";
		else if(xfr.getXfrZeroSeq().getConectionLocation()==XfrZeroSeqConnectLocationEnumType.AT_BUS_2)
			SID="2";
		else if(xfr.getXfrZeroSeq().getConectionLocation()==XfrZeroSeqConnectLocationEnumType.BETWEEN_BUS_1_N_BUS_2)
			SID="3";
		//PAR平行码
		String Par=(braXml.getCircuitId()=="1")?" ":braXml.getCircuitId();
		//X0
		double X0=xfr.getZ0().getIm();
		String X0Str=StringUtil.format(X0, 7, 4);
		//R0
		double R0=xfr.getZ0().getRe();
		String R0Str=StringUtil.format(R0, 7, 4);
		
		seqStr.append(StringUtil.getSpace(1)).append(SID).append(StringUtil.getSpace(1)).append(Par)
			.append(StringUtil.getSpace(3)).append(X0Str).append(R0Str);
	}
	public String setDynamicSequenceData(String bpaLfNetStr, DStabBusXmlType DStabBus) {
		busXml=DStabBus;
		seqStr=new StringBuffer();
		//type
		String type="XR";
		//busName
		String busName=StringUtil.addSpace(busXml.getName(), 8);
		//BaseVoltage
		double BaseVol=busXml.getBaseVoltage().getValue();
		String BaseVolStr=StringUtil.format(BaseVol, 4, 0);
		//R0
		double R0=busXml.getShuntLoadZeroZ().getRe();
		String R0Str=StringUtil.format(R0, 7, 4);
		//X0
		double X0=busXml.getShuntLoadZeroZ().getIm();
		String X0Str=StringUtil.format(X0, 7, 4);
		
		seqStr.append(type).append(StringUtil.getSpace(2)).append(busName).append(BaseVolStr)
			.append(StringUtil.getSpace(5)).append(R0Str).append(X0Str);		
		//set type specific data;
		
		// add to the net
		bpaLfNetStr+=seqStr+"\n";
		return bpaLfNetStr;	
	}
	public String setDynamicMutualZeroData(String bpaLfNetStr, LineDStabXmlType line) {
		for(MutualZeroZXmlType mutualZero:line.getLineMutualZeroZ()){
			braXml=line;
			seqStr=new StringBuffer();
			setDynamicSequenceBaseData();
			//change"LO"to"LM"
			seqStr.replace(0, 2, "LM");
			//Two Spaces
			String TwoSpace=StringUtil.getSpace(2);
			//PAR1
			String CirId1=(braXml.getCircuitId()=="1")?" ":braXml.getCircuitId();
			//Mutual Line
			//From Bus name & base voltage
			DStabBusXmlType MLFromBus=(DStabBusXmlType)mutualZero.getBranchFromBus().getIdRef();
			String MLFBName=StringUtil.addSpace(MLFromBus.getName(),8);
			double MLFBVol=MLFromBus.getBaseVoltage().getValue();
			String MLFBVolStr=StringUtil.format(MLFBVol, 4, 0);
			//To Bus name & base voltage
			DStabBusXmlType MLToBus=(DStabBusXmlType)mutualZero.getBranchToBus().getIdRef();
			String MLTBName=StringUtil.addSpace(MLToBus.getName(),8);
			double MLTBVol=MLToBus.getBaseVoltage().getValue();
			String MLTBVolStr=StringUtil.format(MLTBVol, 4, 0);
			//PAR2
			String CirId2=(mutualZero.getBranchCirId()=="1")?" ":mutualZero.getBranchCirId();
			//RM
			double rm=mutualZero.getZM().getRe();
			String rmStr=StringUtil.format(rm, 7, 4);
			//XM
			double xm=mutualZero.getZM().getIm();
			String xmStr=StringUtil.format(xm, 7, 4);
			
			seqStr.append(TwoSpace).append(CirId1).append(TwoSpace).append(MLFBName).append(MLFBVolStr)
				.append(TwoSpace).append(MLTBName).append(MLTBVolStr).append(TwoSpace).append(CirId2)
				.append(TwoSpace).append(rmStr).append(xmStr).append("\n");
			
			try {
				LineDStabXmlType mutualBranch =parser.getDStabLine
					(MLFromBus.getId(),MLToBus.getId(),mutualZero.getBranchCirId());
				for(MutualZeroZXmlType mutualZ0Line2:mutualBranch.getLineMutualZeroZ()){
					if(mutualZ0Line2.getBranchFromBus().getIdRef()==braXml.getFromBus().getIdRef()
						&&mutualZ0Line2.getBranchToBus().getIdRef()==braXml.getToBus().getIdRef()
						&&mutualZ0Line2.getBranchCirId()==braXml.getCircuitId()){
						mutualBranch.getLineMutualZeroZ().remove(mutualZ0Line2);
						break;
					}
				}
			} catch (ODMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//set type specific data;
		
		// add to the net
		bpaLfNetStr+=seqStr;
		return bpaLfNetStr;
	}
	private static String getType() {
		String type="";
		if(braXml instanceof LineDStabXmlType) type="LO";
		else if(braXml instanceof XfrDStabXmlType) type="XO";
		return type;
	}
	public String stdout(){
		return seqStr.toString();
	}
}
