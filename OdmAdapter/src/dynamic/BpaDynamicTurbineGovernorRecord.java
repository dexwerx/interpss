package dynamic;

import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.GovBPAGiGaTbCombinedModelXmlType;
import org.ieee.odm.schema.GovBPAGsTbCombinedModelXmlType;
import org.ieee.odm.schema.GovBPAHydroTurbineGHXmlType;
import org.ieee.odm.schema.GovHydroSteamGeneralModelXmlType;
import org.ieee.odm.schema.GovernorModelXmlType;
import org.ieee.odm.schema.SpeedGovBPAGSModelXmlType;
import org.ieee.odm.schema.SpeedGovBPAGiGaCombinedXmlType;
import org.ieee.odm.schema.SpeedGovBPARegGIModelXmlType;
import org.ieee.odm.schema.SpeedGovBPAServoGAModelXmlType;
import org.ieee.odm.schema.SteamTurbineBPATBModelXmlType;
import org.ieee.odm.schema.SteamTurbineNRXmlType;

import util.StringUtil;

public class BpaDynamicTurbineGovernorRecord {
	private static DStabBusXmlType busXml=null;
	private static DynamicGeneratorXmlType genXml=null;
	private static GovernorModelXmlType govXml=null;
	private static StringBuffer govStr=null;
	private static double baseMVA=100.0D;
	private static int ChnNum=0;
	private static DStabModelParser parser=null;
	
	public BpaDynamicTurbineGovernorRecord(DStabModelParser parser){
		this.govStr=new StringBuffer();
		this.parser=parser;
		this.baseMVA=parser.getDStabNet().getBasePower().getValue();		
	}
	public String setDynamicTurbineGovernorData(String bpaLfNetStr,DStabBusXmlType DStabBusXml,DynamicGeneratorXmlType generatorXml){
		busXml=DStabBusXml;
		ChnNum=StringUtil.getChineseCharNum(busXml.getName());
		genXml=generatorXml;
		govXml=genXml.getGovernor().getValue();
		govStr=new StringBuffer();
		setDynamicGovBaseData();
		setDynamicGovDSData();
		//set type specific data;
		
		// add to the net
		bpaLfNetStr+=govStr+"\n";
		return bpaLfNetStr;		
	}
	private static void setDynamicGovBaseData() {
		//get type
		String type=getType();
		String reviseCode=StringUtil.getSpace(1);
		// busName ,RatedVoltage
		String busName=StringUtil.addSpace(busXml.getName(),8);		
		double RatedV=genXml.getRatedVoltage().getValue();
		String RatedVStr=StringUtil.format(RatedV, 4, 0);
		//dynGenId 发电机识别码
		String dynGenId=StringUtil.getSpace(1);
		
		govStr.append(type).append(reviseCode).append(busName).append(RatedVStr).append(dynGenId);		
	}
	private static void setDynamicGovDSData() {
		if(getType()=="GG") setGGData();
		else if(getType()=="GH") setGHData();
		else if(getType()=="GS") setGSData();
		else if(getType()=="GA") setGAData();
		else throw new UnsupportedOperationException("input Governor type unsupported!");
	}
	private static void setGGData() {
		GovHydroSteamGeneralModelXmlType GG=(GovHydroSteamGeneralModelXmlType)govXml;
		//PMAX TODO
		double Pmax=GG.getPMAX();
		String PmaxStr=StringUtil.format(Pmax*genXml.getRatedPower().getValue(), 6, 1);
		//R
		double R=GG.getR();
		String RStr=StringUtil.format(R, 5, 3);
		//T1
		double T1=GG.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//T2
		double T2=GG.getT2().getValue();
		String T2Str=StringUtil.format(T2, 5, 3);
		//T3
		double T3=GG.getT3().getValue();
		String T3Str=StringUtil.format(T3, 5, 3);
		//T4
		double T4=GG.getT4().getValue();
		String T4Str=StringUtil.format(T4, 5, 3);
		//T5
		double T5=GG.getT5().getValue();
		String T5Str=StringUtil.format(T5, 5, 3);
		//F
		double F=GG.getF();
		String FStr=StringUtil.format(F, 5, 3);
		
		govStr.append(PmaxStr).append(RStr).append(T1Str).append(T2Str).append(T3Str)
			.append(T4Str).append(T5Str).append(FStr);
	}
	private static void setGHData() {
		GovBPAHydroTurbineGHXmlType GH=(GovBPAHydroTurbineGHXmlType)govXml;
		//PMAX TODO
		double Pmax=GH.getPMAX();
		String PmaxStr=StringUtil.format(Pmax*genXml.getRatedPower().getValue(), 6, 1);
		//R
		double R=GH.getR();
		String RStr=StringUtil.format(R, 5, 3);
		//TG
		double Tg=GH.getTG().getValue();
		String TgStr=StringUtil.format(Tg, 5, 3);
		//TP
		double Tp=GH.getTP().getValue();
		String TpStr=StringUtil.format(Tp, 5, 3);
		//Td
		double Td=GH.getTd().getValue();
		String TdStr=StringUtil.format(Td, 5, 3);
		//TW/2
		double Twhalf=GH.getTwHalf().getValue();
		String TwhalfStr=StringUtil.format(Twhalf, 5, 3);
		//VELCLOSE
		double Vclose=GH.getVClose();
		String VcloseStr=StringUtil.format(Vclose, 5, 3);
		//VELOPEN
		double Vopen=GH.getVOpen();
		String VopenStr=StringUtil.format(Vopen, 5, 3);
		//Dd
		double Dd=GH.getDd();
		String DdStr=StringUtil.format(Dd, 5, 3);
		//Epsilon
		double Epsilon=GH.getEpsilon();
		String EpsilonStr=StringUtil.format(Epsilon, 6, 5);
		
		govStr.append(PmaxStr).append(RStr).append(TgStr).append(TpStr).append(TdStr).append(TwhalfStr)
			.append(VcloseStr).append(VopenStr).append(DdStr).append(EpsilonStr);
	}
	private static void setGSData() {
		GovBPAGsTbCombinedModelXmlType GSTB=(GovBPAGsTbCombinedModelXmlType)govXml;
		SpeedGovBPAGSModelXmlType GS=(SpeedGovBPAGSModelXmlType)GSTB.getSpeedGov().getValue();
		//PMAX TODO
		double Pmax=GS.getPmax();
		String PmaxStr=StringUtil.format(Pmax*genXml.getRatedPower().getValue(), 6, 1);
		//PMIN
		double Pmin=GS.getPmin();
		String PminStr=StringUtil.format(Pmin*genXml.getRatedPower().getValue(), 6, 1);
		//R
		double R=GS.getR();
		String RStr=StringUtil.format(R, 5, 3);
		//T1
		double T1=GS.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//T2
		double T2=GS.getT2().getValue();
		String T2Str=StringUtil.format(T2, 5, 3);
		//T3
		double T3=GS.getT3().getValue();
		String T3Str=StringUtil.format(T3, 5, 3);
		//VELOPEN
		double Vopen=GS.getVELOPEN();
		String VopenStr=StringUtil.format(Vopen, 6, 1);
		//VELCLOSE
		double Vclose=GS.getVELCLOSE();
		String VcloseStr=StringUtil.format(Vclose, 6, 1);
		//Epsilon
		double Epsilon=GS.getEpsilon();
		String EpsilonStr=StringUtil.format(Epsilon, 6, 5);
		
		govStr.append(PmaxStr).append(PminStr).append(RStr).append(T1Str).append(T2Str).append(T3Str)
			.append(VopenStr).append(VcloseStr).append(EpsilonStr).append("\n");
		if(GSTB.getTurbine().getValue() instanceof SteamTurbineBPATBModelXmlType) setTBData();
		else if(GSTB.getTurbine().getValue() instanceof SteamTurbineNRXmlType) setTAData();
//		else throw new UnsupportedOperationException("input Turbine type unsupported!");		
	}
	private static void setGAData() {
		GovBPAGiGaTbCombinedModelXmlType GIGATB=(GovBPAGiGaTbCombinedModelXmlType)govXml;
		SpeedGovBPAGiGaCombinedXmlType GIGA=(SpeedGovBPAGiGaCombinedXmlType)GIGATB.getSpeedGov().getValue();
		SpeedGovBPAServoGAModelXmlType GA=(SpeedGovBPAServoGAModelXmlType)GIGA.getServo();
		//Pe
		double Pe=GA.getPe();
		String PeStr=StringUtil.format(Pe, 6, 2);
		//Tc
		double Tc=GA.getTc().getValue();
		String TcStr=StringUtil.format(Tc, 4, 2);
		//To
		double To=GA.getTo().getValue();
		String ToStr=StringUtil.format(To, 4, 2);
		//VELCLOSE
		double Vclose=GA.getVELCLOSE();
		String VcloseStr=StringUtil.format(Vclose, 4, 2);
		//VELOPEN
		double Vopen=GA.getVELOPEN();
		String VopenStr=StringUtil.format(Vopen, 4, 2);
		//PMAX TODO
		double Pmax=GA.getPmax();
		String PmaxStr=StringUtil.format(Pmax, 4, 2);
		//PMIN
		double Pmin=GA.getPmin();
		String PminStr=StringUtil.format(Pmin, 4, 2);
		//T1
		double T1=GA.getT1().getValue();
		String T1Str=StringUtil.format(T1, 4, 2);
		//KP
		double Kp=GA.getKp();
		String KpStr=StringUtil.format(Kp, 4, 2);
		//KD
		double Kd=GA.getKd();
		String KdStr=StringUtil.format(Kd, 4, 2);
		//KI
		double Ki=GA.getKi();
		String KiStr=StringUtil.format(Ki, 4, 2);
		//INTG_MAX
		double INTG_max=GA.getINTGMAX();
		if(INTG_max==9999) INTG_max=0;
		String INTG_maxStr=StringUtil.format(INTG_max, 4, 2);
		//INTG_MIN
		double INTG_min=GA.getINTGMIN();
		if(INTG_min==-9999) INTG_min=0;
		String INTG_minStr=StringUtil.format(INTG_min, 4, 2);
		//PID_MAX
		double PID_max=GA.getPIDMAX();
		if(PID_max==9999) PID_max=0;
		String PID_maxStr=StringUtil.format(PID_max, 4, 2);
		//INTG_MIN
		double PID_min=GA.getPIDMIN();
		if(PID_min==-9999) PID_min=0;
		String PID_minStr=StringUtil.format(PID_min, 4, 2);
		
		govStr.append(PeStr).append(TcStr).append(ToStr).append(VcloseStr).append(VopenStr).append(PmaxStr)
			.append(PminStr).append(T1Str).append(KpStr).append(KdStr).append(KiStr).append(INTG_maxStr)
			.append(INTG_minStr).append(PID_maxStr).append(PID_minStr).append("\n");
		//GI数据卡
		setGIData();
		
	}
	private static void setGIData() {
		SpeedGovBPAGiGaCombinedXmlType GIGA=(SpeedGovBPAGiGaCombinedXmlType)govXml.getSpeedGov().getValue();
		SpeedGovBPARegGIModelXmlType GI=(SpeedGovBPARegGIModelXmlType)GIGA.getRegulator();
		govStr.append("GI").append(govStr,2,16-ChnNum);
		//T1
		double T1=GI.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//Epsilon
		double Epsilon=GI.getEpsilon();
		String EpsilonStr=StringUtil.format(Epsilon, 6, 4);
		//K
		double K=GI.getK();
		String KStr=StringUtil.format(K, 5, 2);
		//LoadSwich
		String LoadSwichStr=(GI.isLoadSwichOff()==true)?"2":"1";
		//KP1
		double Kp1=GI.getKp1();
		String Kp1Str=StringUtil.format(Kp1, 5, 3);
		//KD1
		double Kd1=GI.getKd1();
		String Kd1Str=StringUtil.format(Kd1, 5, 3);
		//KI1
		double Ki1=GI.getKi1();
		String Ki1Str=StringUtil.format(Ki1, 5, 3);
		//INTG_MAX1
		double INTG_max1=GI.getINTGMAX1();
		if(INTG_max1==9999) INTG_max1=0;
		String INTG_max1Str=StringUtil.format(INTG_max1, 5, 3);
		//INTG_MIN1
		double INTG_min1=GI.getINTGMIN1();
		if(INTG_min1==-9999) INTG_min1=0;
		String INTG_min1Str=StringUtil.format(INTG_min1, 5, 3);
		//PID_MAX1
		double PID_max1=GI.getPIDMAX1();
		if(PID_max1==9999) PID_max1=0;
		String PID_max1Str=StringUtil.format(PID_max1, 5, 3);
		//PID_MIN1
		double PID_min1=GI.getPIDMIN1();
		if(PID_min1==-9999) PID_min1=0;
		String PID_min1Str=StringUtil.format(PID_min1, 5, 3);
		//LoadForwardSwitch
		String LoadForwardSwitchStr=(GI.isLoadForwardSwitchOff()==true)?"2":"1";
		//CON_MAX 
		double CON_max=GI.getConMax();
		if(CON_max==9999) CON_max=0;
		String CON_maxStr=StringUtil.format(CON_max, 5, 3);
		//CON_MIN
		double CON_min=GI.getConMin();
		if(CON_min==-9999) CON_min=0;
		String CON_minStr=StringUtil.format(CON_min, 5, 3);
		
		govStr.append(T1Str).append(EpsilonStr).append(KStr).append(LoadSwichStr).append(Kp1Str)
			.append(Kd1Str).append(Ki1Str).append(INTG_max1Str).append(INTG_min1Str).append(PID_max1Str)
			.append(PID_min1Str).append(LoadForwardSwitchStr).append(CON_maxStr).append(CON_minStr).append("\n");
		
		//GI+数据卡
		govStr.append("GI+").append(govStr,3,16-ChnNum);
		//PresserSwitch
		String PresserSwitchStr=(GI.isPresserSwitchOff()==true)?"2":"1";
		//KP2
		double Kp2=GI.getKp2();
		String Kp2Str=StringUtil.format(Kp2, 5, 3);
		//KD2
		double Kd2=GI.getKd2();
		String Kd2Str=StringUtil.format(Kd2, 5, 3);
		//KI2
		double Ki2=GI.getKi2();
		String Ki2Str=StringUtil.format(Ki2, 5, 3);
		//INTG_MAX2
		double INTG_max2=GI.getINTGMAX2();
		if(INTG_max2==9999) INTG_max2=0;
		String INTG_max2Str=StringUtil.format(INTG_max2, 5, 3);
		//INTG_MIN2
		double INTG_min2=GI.getINTGMIN2();
		if(INTG_min2==-9999) INTG_min2=0;
		String INTG_min2Str=StringUtil.format(INTG_min2, 5, 3);
		//PID_MAX2
		double PID_max2=GI.getPIDMAX2();
		if(PID_max2==9999) PID_max2=0;
		String PID_max2Str=StringUtil.format(PID_max2, 5, 3);
		//PID_MIN2
		double PID_min2=GI.getPIDMIN2();
		if(PID_min2==-9999) PID_min2=0;
		String PID_min2Str=StringUtil.format(PID_min2, 5, 3);
		
		govStr.append(PresserSwitchStr).append(Kp2Str).append(Kd2Str).append(Ki2Str)
			.append(INTG_max2Str).append(INTG_min2Str).append(PID_max2Str).append(PID_min2Str)
			.append(CON_maxStr).append(CON_minStr).append("\n");
		setTBData();
	}
	private static void setTAData() {
		SteamTurbineNRXmlType TA=(SteamTurbineNRXmlType)govXml.getTurbine().getValue();
		govStr.append("TA").append(govStr,2,16-ChnNum);
		//Tch
		double Tch=TA.getTCH().getValue();
		String TchStr=StringUtil.format(Tch, 5, 3);
		//K1
		double K1=TA.getK();
		String K1Str=StringUtil.format(K1, 5, 3);
		
		govStr.append(TchStr).append(K1Str);
	}
	private static void setTBData() {
		SteamTurbineBPATBModelXmlType TB=(SteamTurbineBPATBModelXmlType)govXml.getTurbine().getValue();
		govStr.append("TB").append(govStr,2,16-ChnNum);
		//Tch
		double Tch=TB.getTCH().getValue();
		String TchStr=StringUtil.format(Tch, 5, 3);
		//FHP
		double Fhp=TB.getFHP();
		String FhpStr=StringUtil.format(Fhp, 5, 3);
		//TRH
		double Trh=TB.getTRH().getValue();
		String TrhStr=StringUtil.format(Trh, 5, 3);
		//FIP
		double Fip=TB.getFIP();
		String FipStr=StringUtil.format(Fip, 5, 3);
		//TCO
		double Tco=TB.getTCO().getValue();
		String TcoStr=StringUtil.format(Tco, 5, 3);
		//FLP
		double Flp=TB.getFLP();
		String FlpStr=StringUtil.format(Flp, 5, 3);
		
		govStr.append(TchStr).append(FhpStr).append(StringUtil.getSpace(5)).append(TrhStr)
			.append(FipStr).append(StringUtil.getSpace(5)).append(TcoStr).append(FlpStr);
	}
	private static String getType() {
		String type="";
		if(govXml instanceof GovHydroSteamGeneralModelXmlType){
			type="GG";
		}
		else if(govXml instanceof GovBPAHydroTurbineGHXmlType){
			type="GH";
		}
		else if(govXml instanceof GovBPAGsTbCombinedModelXmlType){
			type="GS";
		}
		else if(govXml instanceof GovBPAGiGaTbCombinedModelXmlType){
			type="GA";
		}
		else throw new UnsupportedOperationException("input Governor type unsupported!");
		return type;
	}
	public String stdout(){
		return govStr.toString();
	}
}
