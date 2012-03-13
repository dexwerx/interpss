package BPA;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.AclfGenDataXmlType;
import org.ieee.odm.schema.AclfLoadDataXmlType;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenXmlType;
import org.ieee.odm.schema.LoadflowLoadXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.YXmlType;

import util.StringUtil;
import util.UnitConverter;

public class BpaAclfBusRecord {
	private LoadflowBusXmlType busXml=null;
	private StringBuffer busStr=null;
	private double baseMVA=100D;
	
	public BpaAclfBusRecord(AclfModelParser parser){
		this.baseMVA=parser.getAclfNet().getBasePower().getValue();
	}
	public String setAclfBusData(String bpaLfNetStr,LoadflowBusXmlType busXmlData){
		busXml=busXmlData;
		busStr=new StringBuffer();
		setAclfBusBaseData();
		setAclfLoadData();
		setShuntYData();
		setAclfGenData();
		
		if(getBusType().equals("BQ")||getBusType().equals("BS"))
			setVTheta();
		//add to the network str
		return bpaLfNetStr+=busStr.toString()+"\n";
	}
	private void setAclfBusBaseData(){
		//bus type
		busStr.append(getBusType());
		//bus name
		String busName=StringUtil.addSpace(busXml.getName(),8);
		//revise code
		String reviseCode=StringUtil.getSpace(1);
		//owner
		//TODO
		String  owner=StringUtil.getSpace(3);
		//owner=StringUtil.addSpace(busXmlData.getOwnerList().getOwner().get(0).getName(),3);
		//baseKV F4.0
		double baseKV=busXml.getBaseVoltage().getValue();
		String baseKVStr=StringUtil.format(baseKV, 4, 0);
		//ZONE
		String zone=StringUtil.addSpace(busXml.getZoneName(),2);
		
		busStr.append(reviseCode).append(owner).append(busName).append(baseKVStr).append(zone);		
	}
	private  void setAclfLoadData(){
		String loadPStr=StringUtil.getSpace(5);
		String loadQStr=StringUtil.getSpace(5);
		
		//LoadP LoadQ
		if(busXml.getLoadData()!=null){
			AclfLoadDataXmlType loadData=busXml.getLoadData();
			LoadflowLoadXmlType equivLoadData=loadData.getEquivLoad();
			double loadP=equivLoadData.getConstPLoad().getRe();
			double loadQ=equivLoadData.getConstPLoad().getIm();
			if(equivLoadData.getConstPLoad().getUnit()==ApparentPowerUnitType.PU){ 
				loadP=UnitConverter.convActivePowerUnit(loadP, ActivePowerUnitType.PU,ActivePowerUnitType.MW , baseMVA);
				loadQ=UnitConverter.convReactivePowerUnit(loadQ, ReactivePowerUnitType.PU,ReactivePowerUnitType.MVAR , baseMVA);
			}			
			loadPStr=StringUtil.format(loadP, 5, 0);
			loadQStr=StringUtil.format(loadQ, 5, 0);			
		}
		busStr.append(loadPStr).append(loadQStr);		
	}
	private  void setAclfGenData(){
		String genPmaxStr=StringUtil.getSpace(4);
		String genPStr   =StringUtil.getSpace(5);
		String genQmaxStr =StringUtil.getSpace(5);
		String genQminStr =StringUtil.getSpace(5);
		
		//Pmax F4.0 ,Others F5.0
		if(busXml.getGenData()!=null){
			AclfGenDataXmlType genData=busXml.getGenData();
			LoadflowGenXmlType xmlEquivGenData = genData.getEquivGen();
			if(xmlEquivGenData.getCode()!=LFGenCodeEnumType.NONE_GEN){
				//pmax
				if(xmlEquivGenData.getPLimit()!=null){
					double pmax=xmlEquivGenData.getPLimit().getMax();
					if(xmlEquivGenData.getPLimit().getUnit()==ActivePowerUnitType.PU){ 
					   	pmax=UnitConverter.convActivePowerUnit(pmax, ActivePowerUnitType.PU,ActivePowerUnitType.MW , baseMVA);
					}
					genPmaxStr=StringUtil.format(pmax, 4, 0);
				}
				//genP
				if(xmlEquivGenData.getPower()!=null){
					double genP=xmlEquivGenData.getPower().getRe();
					genPStr=StringUtil.format(genP, 5, 0);
				}
				// genQmax, genQmin
				if(xmlEquivGenData.getQLimit()!=null){
					double genQmax=xmlEquivGenData.getQLimit().getMax();
					double genQmin=xmlEquivGenData.getQLimit().getMin();
					if(xmlEquivGenData.getQLimit().getUnit()==ReactivePowerUnitType.PU){ 
						genQmax=UnitConverter.convReactivePowerUnit(genQmax, ReactivePowerUnitType.PU,ReactivePowerUnitType.MVAR , baseMVA);
						genQmin=UnitConverter.convReactivePowerUnit(genQmin, ReactivePowerUnitType.PU,ReactivePowerUnitType.MVAR , baseMVA);
					}
					genQmaxStr=StringUtil.format(genQmax, 5, 0);
					genQminStr=StringUtil.format(genQmin, 5, 0);				 
				}
			}// end of "is Gen"
		}// end of "has gen data"
		busStr.append(genPmaxStr).append(genPStr).append(genQmaxStr).append(genQminStr);		
	}
	private void setShuntYData(){
		String shuntGStr=StringUtil.getSpace(4);
		String shuntBStr=StringUtil.getSpace(4);
		
		//shuntG,shuntB F4.0(+),F3.0(-)
		if(busXml.getShuntY()!=null){
			YXmlType shuntY=busXml.getShuntY();
			double shuntG=shuntY.getRe();
			double shuntB=shuntY.getIm();
			if(busXml.getShuntY().getUnit()==YUnitType.PU){ 
				shuntG=UnitConverter.convYUnit(shuntG, YUnitType.PU, ReactivePowerUnitType.MVAR, baseMVA);
				shuntB=UnitConverter.convYUnit(shuntB, YUnitType.PU, ReactivePowerUnitType.MVAR, baseMVA);
			}
			shuntGStr=StringUtil.format(shuntG, 4, 0);
			shuntBStr=StringUtil.format(shuntB, 4, 0);
		}
		busStr.append(shuntGStr).append(shuntBStr);
	}
	private  void setVTheta(){		
		String vmagStr =StringUtil.getSpace(4);
		String angleStr =StringUtil.getSpace(4);
		
		//F4.3
		if(busXml.getGenData()!=null){
			AclfGenDataXmlType genData=busXml.getGenData();
			LoadflowGenXmlType xmlEquivGenData = genData.getEquivGen();
			double vmag=xmlEquivGenData.getDesiredVoltage().getValue();
			if(xmlEquivGenData.getDesiredVoltage().getUnit()==VoltageUnitType.KV)
				vmag=UnitConverter.convVoltUnit(vmag, VoltageUnitType.KV, VoltageUnitType.PU, busXml.getBaseVoltage().getValue());
			vmagStr=StringUtil.format(vmag, 4, 3);
			if(xmlEquivGenData.getDesiredAngle()!=null){
				double angle=xmlEquivGenData.getDesiredAngle().getValue();
				angleStr=StringUtil.format(angle, 4, 1);
			}
		}
		busStr.append(vmagStr).append(angleStr);
	}
	private  void setRomoteControl(){
		//TODO
	}
	private String getBusType(){
		String busType="B ";
		if(busXml.getGenData()!=null){
			AclfGenDataXmlType genData=busXml.getGenData();
			LoadflowGenXmlType xmlEquivGenData = genData.getEquivGen();
			if(xmlEquivGenData.getCode()==LFGenCodeEnumType.PV)busType="BQ";
			else if (xmlEquivGenData.getCode()==LFGenCodeEnumType.SWING)busType="BS";
		}		
		return busType;
	}
	public String stdOut(){
		return busStr.toString();		
	}
}
