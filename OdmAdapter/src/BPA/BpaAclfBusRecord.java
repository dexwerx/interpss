package BPA;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowGenDataXmlType;
import org.ieee.odm.schema.LoadflowLoadDataXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.YXmlType;

import util.StringUtil;
import util.UnitConverter;

public class BpaAclfBusRecord {
	private String netStr="";
	private LoadflowBusXmlType busXml=null;
	private StringBuffer busStr=null;
	private double baseMVA=100D;
	public BpaAclfBusRecord(AclfModelParser parser){

		this.baseMVA=parser.getAclfNet().getBasePower().getValue();
	}
	public String setAclfBusData(String bpaLfNetStr,LoadflowBusXmlType busXmlData){
		busXml=busXmlData;
		busStr=new StringBuffer();
		setAclfBusBaseData(busXmlData);
		setAclfLoadData(busXmlData);
		setShuntYData(busXmlData);
		setAclfGenData(busXmlData);
		
		if(getBusType(busXmlData).equals("BQ")||getBusType(busXmlData).equals("BS"))
			setVTheta(busXmlData);
		//add to the network str
		return bpaLfNetStr+=busStr.toString()+"\n";
	}
	private void setAclfBusBaseData(LoadflowBusXmlType busXmlData){
		//bus type
		busStr.append(getBusType(busXmlData));
		//bus name
		String busName=StringUtil.addSpace(busXmlData.getName(),8);
		//revise code
		String reviseCode=" ";
		//owner
		//TODO
		String  owner=StringUtil.addSpace("",3);
		//owner=StringUtil.addSpace(busXmlData.getOwnerList().getOwner().get(0).getName(),3);
		//baseKV F4.0
		double baseKV=busXmlData.getBaseVoltage().getValue();
		String baseKVStr=(baseKV>100.0)?String.format("%4.0f", baseKV):""+baseKV;
		//ZONE
		String zone=StringUtil.addSpace(busXmlData.getZoneName(),2);
		
		busStr.append(reviseCode).append(owner).append(busName).append(baseKVStr).append(zone);
		
	}
	private  void setAclfLoadData(LoadflowBusXmlType busXmlData){
		String loadPStr="";
		String loadQStr="";
		
		//LoadP LoadQ
		if(busXmlData.getLoadData()!=null){
			LoadflowBusXmlType.LoadData loadData=busXmlData.getLoadData();
			LoadflowLoadDataXmlType equivLoadData=loadData.getEquivLoad();
			double loadP=equivLoadData.getConstPLoad().getRe();
			double loadQ=equivLoadData.getConstPLoad().getIm();
			if(equivLoadData.getConstPLoad().getUnit()==ApparentPowerUnitType.PU){ 
				loadP=UnitConverter.convActivePowerUnit(loadP, ActivePowerUnitType.PU,ActivePowerUnitType.MW , baseMVA);
				loadQ=UnitConverter.convReactivePowerUnit(loadP, ReactivePowerUnitType.PU,ReactivePowerUnitType.MVAR , baseMVA);
			}
			
			loadPStr=(loadP>0.0&&loadP<1000.0)?String.format("%4.1f", loadP):
				(loadP>=1000.0)?String.format("%5.0f", loadP):String.format("% 4.0f", loadP);
			loadQStr=(loadQ>0.0&&loadQ<1000.0)?String.format("%4.1f", loadQ):
					(loadQ>=1000.0)?String.format("%5.0f", loadQ):String.format("% 4.0f", loadQ);
			
		}
		loadPStr=StringUtil.addSpace(loadPStr, 5);
		loadQStr=StringUtil.addSpace(loadQStr, 5);
		busStr.append(loadPStr).append(loadQStr);
		
		
	}
	private  void setAclfGenData(LoadflowBusXmlType busXmlData){
		String genPmaxStr="";
		String genPStr   ="";
		String genQmaxStr ="";
		String genQminStr =StringUtil.addSpace("", 5);
		
		//Pmax F4.0 ,Others F5.0
		if(busXmlData.getGenData()!=null){
			LoadflowBusXmlType.GenData genData=busXmlData.getGenData();
			LoadflowGenDataXmlType xmlEquivGenData = genData.getEquivGen();
		 if(xmlEquivGenData.getCode()!=LFGenCodeEnumType.NONE_GEN){
			  //pmax
			  if(xmlEquivGenData.getPLimit()!=null){
			  double pmax=xmlEquivGenData.getPLimit().getMax();
			    if(xmlEquivGenData.getPLimit().getUnit()==ActivePowerUnitType.PU){ 
				pmax=UnitConverter.convActivePowerUnit(pmax, ActivePowerUnitType.PU,ActivePowerUnitType.MW , baseMVA);
			    }
			  genPmaxStr=String.format("%4.0f", pmax);
			 }
			//genP
			if(xmlEquivGenData.getPower()!=null){
				double genP=xmlEquivGenData.getPower().getRe();
				genPStr=String.format("%5.0f", genP);
			}
			// genQmax, genQmin
			 if(xmlEquivGenData.getQLimit()!=null){
				  double genQmax=xmlEquivGenData.getQLimit().getMax();
				  double genQmin=xmlEquivGenData.getQLimit().getMin();
				    if(xmlEquivGenData.getQLimit().getUnit()==ReactivePowerUnitType.PU){ 
					genQmax=UnitConverter.convReactivePowerUnit(genQmax, ReactivePowerUnitType.PU,ReactivePowerUnitType.MVAR , baseMVA);
					genQmin=UnitConverter.convReactivePowerUnit(genQmin, ReactivePowerUnitType.PU,ReactivePowerUnitType.MVAR , baseMVA);
				    }
				    genQmaxStr=String.format("%5.0f", genQmax);
				    genQminStr=String.format("%5.0f", genQmin);
				 
			 }
		   }// end of "is Gen"
		}// end of "has gen data"
		genPmaxStr=StringUtil.addSpace(genPmaxStr, 4);
		genPStr   =StringUtil.addSpace(genPStr, 5);
		genQmaxStr =StringUtil.addSpace(genQmaxStr, 5);
		genQminStr =StringUtil.addSpace(genQminStr, 5);
		busStr.append(genPmaxStr).append(genPStr).append(genQmaxStr).append(genQminStr);
		
	}
	private void setShuntYData(LoadflowBusXmlType busXmlData){
		String shuntGStr="";
		String shuntBStr="";
		
		//shuntG,shuntB F4.0(+),F3.0(-)
		if(busXmlData.getShuntY()!=null){
			YXmlType shuntY=busXmlData.getShuntY();
			double shuntG=shuntY.getRe();
			double shuntB=shuntY.getIm();
			 if(busXmlData.getShuntY().getUnit()==YUnitType.PU){ 
				 shuntG=UnitConverter.convYUnit(shuntG, YUnitType.PU, ReactivePowerUnitType.MVAR, baseMVA);
				 shuntB=UnitConverter.convYUnit(shuntB, YUnitType.PU, ReactivePowerUnitType.MVAR, baseMVA);
			 }
			shuntGStr=(shuntG>0)?String.format("%4.0f", shuntG):String.format("% 3.0f", shuntG);
			shuntBStr=(shuntB>0)?String.format("%4.0f", shuntB):String.format("% 3.0f", shuntB);
		}
		shuntGStr=StringUtil.addSpace(shuntGStr, 4);
		shuntBStr=StringUtil.addSpace(shuntBStr, 4);
		busStr.append(shuntGStr).append(shuntBStr);
	}
	private  void setVTheta(LoadflowBusXmlType busXmlData){
		
		
		String vmagStr ="";
		String angleStr ="";
		
		//F4.3
		if(busXmlData.getGenData()!=null){
			LoadflowBusXmlType.GenData genData=busXmlData.getGenData();
			LoadflowGenDataXmlType xmlEquivGenData = genData.getEquivGen();
			double vmag=xmlEquivGenData.getDesiredVoltage().getValue();
			if(xmlEquivGenData.getDesiredVoltage().getUnit()==VoltageUnitType.KV)
				vmag=UnitConverter.convVoltUnit(vmag, VoltageUnitType.KV, VoltageUnitType.PU, busXmlData.getBaseVoltage().getValue());
			vmag*=1000.0;
			vmagStr=String.format("%4.0f", vmag);
			if(xmlEquivGenData.getDesiredAngle()!=null){
				double angle=xmlEquivGenData.getDesiredAngle().getValue();
				angleStr=String.format("%3.1f", angle);
			}
			// judging the length, and add space if is less than 4
		}
		vmagStr=StringUtil.addSpace(vmagStr, 4);
		angleStr=StringUtil.addSpace(angleStr, 4);
		busStr.append(vmagStr).append(angleStr);
		
		
	}
	private  void setRomoteControl(){
		//TODO
	}
	private String getBusType(LoadflowBusXmlType busXmlData){
		String busType="B ";
		if(busXmlData.getGenData()!=null){
			LoadflowBusXmlType.GenData genData=busXmlData.getGenData();
			LoadflowGenDataXmlType xmlEquivGenData = genData.getEquivGen();
			if(xmlEquivGenData.getCode()==LFGenCodeEnumType.PV)busType="BQ";
			else if (xmlEquivGenData.getCode()==LFGenCodeEnumType.SWING)busType="BS";
		}
		
		return busType;
		
	}
	public   String stdOut(){
		return busStr.toString();

		
	}


}
