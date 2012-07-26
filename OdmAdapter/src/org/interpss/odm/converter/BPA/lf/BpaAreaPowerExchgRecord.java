package org.interpss.odm.converter.BPA.lf;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ExchangeAreaXmlType;
import org.ieee.odm.schema.InterchangeXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NetAreaXmlType;
import org.ieee.odm.schema.NetZoneXmlType;

//import com.sun.xml.internal.ws.util.StringUtils;

import util.StringUtil;

public class BpaAreaPowerExchgRecord {
	
	private static String _netStr="";
	private static String areaStr="";
	private static double _baseMVA=100.0D;
	private static AclfModelParser _parser=null;
	
	public BpaAreaPowerExchgRecord(AclfModelParser parser){
		_parser=parser;
		//_netStr=bpaLfNetStr;
		_baseMVA=parser.getAclfNet().getBasePower().getValue();
	}
	
	// set area and area power exchange record
	public String setAreaRecord(String bpaLfNetStr){
		areaStr="";
		LoadflowNetXmlType xmlNet=_parser.getAclfNet();
		for(NetAreaXmlType area:xmlNet.getAreaList().getArea()){
			ExchangeAreaXmlType exchgArea=(ExchangeAreaXmlType) area;
			String areaName=area.getName();
			//_parser.getAclfNet().get
			LoadflowBusXmlType swingBus= (LoadflowBusXmlType) exchgArea.getSwingBusId().getIdRef();
			String swingBusName=swingBus.getName();
			double baseKV =swingBus.getBaseVoltage().getValue();
			String baseKVStr=(baseKV>100.0)?String.format("%4.0f", baseKV):""+baseKV;
			
			String exchangePower=StringUtil.getSpace(8);
			if(exchgArea.getDesiredExchangePower()!=null){
				double power=exchgArea.getDesiredExchangePower().getValue();
				exchangePower=(exchgArea.getDesiredExchangePower().getUnit()==ActivePowerUnitType.MW)?
				             String.format("% 8.0f",power):String.format("% 8.0f", power*_baseMVA);
			}
			
			String zoneStr="";
			String zoneStrPlus="";
			int zoneNum=1;
			if(area.getZone()!=null&&area.getZone().size()>0){
				for(NetZoneXmlType zone:area.getZone()){
					if(zoneNum<21)zoneStr+=StringUtil.addSpace(zone.getName(),2)+" ";
					else zoneStrPlus+=StringUtil.addSpace(zone.getName(),2)+" ";
					zoneNum++;
				}
			}
			areaName=StringUtil.addSpace(areaName, 10);
			swingBusName=StringUtil.addSpace(swingBusName, 8);
			exchangePower=StringUtil.addSpace(exchangePower, 8);
			areaStr+="AC "+areaName+swingBusName+baseKVStr+" "+exchangePower+" "+zoneStr+"\n";
			if(zoneNum>20) areaStr+="AC+"+areaName+StringUtil.getSpace(22)+zoneStrPlus+"\n";
		
		}
		return bpaLfNetStr+=areaStr;
		
	}
	public String setInterAreaExchangeRecord(String bpaLfNetStr) throws Exception{
		areaStr="";
		LoadflowNetXmlType xmlNet=_parser.getAclfNet();
		for(InterchangeXmlType interChg:xmlNet.getInterchangeList().getInterchange()){
			if(interChg.getAreaTransfer()!=null){
				String fAreaName=getAreaByNumber(xmlNet,interChg.getAreaTransfer().getFromArea()).getName();
				String tAreaName=getAreaByNumber(xmlNet,interChg.getAreaTransfer().getToArea()).getName();
				double power=interChg.getAreaTransfer().getAmountMW();
				String powerStr=String.format("% 8.0f",power);
				fAreaName=StringUtil.addSpace(fAreaName, 10);
				tAreaName=StringUtil.addSpace(tAreaName, 10);
				powerStr=StringUtil.addSpace(powerStr, 8);
				areaStr+="I"+StringUtil.getSpace(2)+fAreaName+" "+tAreaName+StringUtil.getSpace(2)+powerStr+"\n";
			}
			else throw new Exception("the Inter exchange area transfer type is not supported yet!");
		}
		return bpaLfNetStr+=areaStr;
	}
	private NetAreaXmlType getAreaByNumber(LoadflowNetXmlType xmlNet,int areaNum){
		NetAreaXmlType targetArea=null;
		for(NetAreaXmlType area:xmlNet.getAreaList().getArea()){
			if(area.getNumber()==areaNum) targetArea=area;
		}
		return targetArea;
		
	}

}
