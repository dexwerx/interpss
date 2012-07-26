package org.interpss.odm.converter.BPA.dynamic;

import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicLoadModelSelectionXmlType;
import org.ieee.odm.schema.DynamicLoadXmlType;
import org.ieee.odm.schema.LoadCharacteristicLocationEnumType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NetAreaXmlType;
import org.ieee.odm.schema.NetZoneXmlType;

import util.StringUtil;

public class BpaDynamicLoadCharacteristicRecord {
	private static DStabBusXmlType busXml=null;
	private static StringBuffer loadStr=null;
	private static double baseMVA=100.0D;
	private static DStabModelParser parser=null;
	
	public BpaDynamicLoadCharacteristicRecord(DStabModelParser parser){
		this.loadStr=new StringBuffer();
		this.parser=parser;
		this.baseMVA=parser.getDStabNet().getBasePower().getValue();		
	}
	public void setDynamicLoadCharacteristicData(String bpaLfNetStr,DStabBusXmlType DStabBus){
		busXml=DStabBus;
		loadStr=new StringBuffer();
		setDynamicLoadCharacteristicData();
//		//set type specific data;
//		
//		// add to the net
//		bpaLfNetStr+=loadStr+"\n";
//		return bpaLfNetStr;		
	}
	private static void setDynamicLoadCharacteristicData() {
		DynamicLoadXmlType load = busXml.getDynamicLoad();
		DynamicLoadModelSelectionXmlType loadModel =load.getLoadModel();
//		//Bus Name ,Base Voltage
//		String busName=StringUtil.addSpace(busXml.getName(),8);
//		double baseVol=busXml.getBaseVoltage().getValue();
//		String baseVolStr=StringUtil.format(baseVol, 4, 0);
//		//ZoneName
//		String zoneName=StringUtil.addSpace(busXml.getZoneName(),2);
//		//AreaName
//		String areaName=StringUtil.getSpace(10);
//		LoadflowNetXmlType xmlNet=parser.getAclfNet();
//		if(xmlNet.getAreaList()!=null)
//			for(NetAreaXmlType area:xmlNet.getAreaList().getArea()){
//				for(NetZoneXmlType zone:area.getZoneList().getZone()){
//					if(busXml.getZoneName()==zone.getName()) areaName=StringUtil.addSpace(area.getName(),10);
//				}
//			}
//		String areaName=busXml.getAreaName()==null?"":busXml.getAreaName();
//		areaName=StringUtil.addSpace(areaName,10);
		//P1,Q1
		double P1=loadModel.getIEEEStaticLoad().getA1();
		double Q1=loadModel.getIEEEStaticLoad().getA4();
		String P1Str=StringUtil.format(P1, 5, 3);
		String Q1Str=StringUtil.format(Q1, 5, 3);
		//P2,Q2
		double P2=loadModel.getIEEEStaticLoad().getA2();
		double Q2=loadModel.getIEEEStaticLoad().getA5();
		String P2Str=StringUtil.format(P2, 5, 3);
		String Q2Str=StringUtil.format(Q2, 5, 3);
		//P3,Q3
		double P3=loadModel.getIEEEStaticLoad().getA3();
		double Q3=loadModel.getIEEEStaticLoad().getA6();
		String P3Str=StringUtil.format(P3, 5, 3);
		String Q3Str=StringUtil.format(Q3, 5, 3);
		//P4,Q4
		double P4=loadModel.getIEEEStaticLoad().getA9()==null?0:loadModel.getIEEEStaticLoad().getA9();
		double Q4=loadModel.getIEEEStaticLoad().getA10()==null?0:loadModel.getIEEEStaticLoad().getA10();
		String P4Str=StringUtil.format(P4, 5, 3);
		String Q4Str=StringUtil.format(Q4, 5, 3);
		//LDP,LDQ
		double Ldp=loadModel.getIEEEStaticLoad().getA7();
		double Ldq=loadModel.getIEEEStaticLoad().getA8();
		String LdpStr=StringUtil.format(Ldp, 5, 3);
		String LdqStr=StringUtil.format(Ldq, 5, 3);
		//type
		String type="LB";
		if(!P4Str.trim().isEmpty()||!Q4Str.trim().isEmpty()) type="LA";
		
		loadStr.append(type).append(StringUtil.getSpace(25)).append(P1Str).append(Q1Str)
			.append(P2Str).append(Q2Str).append(P3Str).append(Q3Str).append(P4Str)
			.append(Q4Str).append(LdpStr).append(LdqStr);

		//location(AT_AREA,AT_ZONE,AT_BUS)
		LoadCharacteristicLocationEnumType location=busXml.getDynamicLoad().getLocation();
		if(location==LoadCharacteristicLocationEnumType.AT_AREA){
			//AreaName
			String areaName=StringUtil.getSpace(10);
			LoadflowNetXmlType xmlNet=parser.getAclfNet();
			if(xmlNet.getAreaList()!=null)
				for(NetAreaXmlType area:xmlNet.getAreaList().getArea()){
					for(NetZoneXmlType zone:area.getZone()){
						if(busXml.getZoneName()==zone.getName()) 
							areaName=StringUtil.addSpace(area.getName(),10);
					}
				}
			loadStr.replace(17, 27, areaName);
		}else if(location==LoadCharacteristicLocationEnumType.AT_ZONE){
			//ZoneName
			String zoneName=StringUtil.addSpace(busXml.getZoneName(),2);
			loadStr.replace(15, 17, zoneName);
		}else if(location==LoadCharacteristicLocationEnumType.AT_BUS){
			//Bus Name ,Base Voltage
			String busName=StringUtil.addSpace(busXml.getName(),8);
			int ChnNum=StringUtil.getChineseCharNum(busXml.getName());
			double baseVol=busXml.getBaseVoltage().getValue();
			String baseVolStr=StringUtil.format(baseVol, 4, 0);
			loadStr.replace(3, 11-ChnNum, busName).replace(11-ChnNum, 15, baseVolStr);//15=15-ChnNum+ChnNum
		}
	}
	public String stdout() {
		return loadStr.toString();
	}
}
