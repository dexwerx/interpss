package dynamic;

import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.ExcIEEE1968Type1XmlType;
import org.ieee.odm.schema.ExciterModelXmlType;
import org.ieee.odm.schema.PssBPADualInputXmlType;
import org.ieee.odm.schema.PssBpaSgTypeXmlType;
import org.ieee.odm.schema.PssBpaSpTypeXmlType;
import org.ieee.odm.schema.PssBpaSsTypeXmlType;
import org.ieee.odm.schema.StabilizerInputSignalEnumType;
import org.ieee.odm.schema.StabilizerModelXmlType;

import util.StringUtil;

public class BpaDynamicPSSRecord {
	private static DStabBusXmlType busXml=null;
	private static DynamicGeneratorXmlType genXml=null;
	private static StabilizerModelXmlType pssXml=null;
	private static StringBuffer pssStr=null;
	private static double baseMVA=100.0D;
	private static int ChnNum=0;
	private static DStabModelParser parser=null;
	
	public BpaDynamicPSSRecord(DStabModelParser parser){
		this.pssStr=new StringBuffer();
		this.parser=parser;
		this.baseMVA=parser.getDStabNet().getBasePower().getValue();		
	}
	public String setDynamicPSSData(String bpaLfNetStr,DStabBusXmlType DStabBusXml,DynamicGeneratorXmlType generatorXml){
		busXml=DStabBusXml;
		ChnNum=StringUtil.getChineseCharNum(busXml.getName());
		genXml=generatorXml;
		pssXml=genXml.getStabilizer().getValue();
		pssStr=new StringBuffer();
		setDynamicPSSBaseData();
		setDynamicPSSDSData();
		//set type specific data;
		
		// add to the net
		bpaLfNetStr+=pssStr+"\n";
		return bpaLfNetStr;		
	}
	private static void setDynamicPSSBaseData() {
		//get type
		String type=getType();
		String reviseCode=StringUtil.getSpace(1);
		// busName ,RatedVoltage
		String busName=StringUtil.addSpace(busXml.getName(),8);		
		double RatedV=genXml.getRatedVoltage().getValue();
		String RatedVStr=StringUtil.format(RatedV, 4, 0);
		//dynGenId 发电机识别码
		String dynGenId=StringUtil.getSpace(1);
		
		pssStr.append(type).append(reviseCode).append(busName).append(RatedVStr).append(dynGenId);
	}
	private void setDynamicPSSDSData() {
		if(getType()=="SS") setSSData();
		else if(getType()=="SP") setSPData();
		else if(getType()=="SG") setSGData();
		else if(getType()=="SI") setSIData();
		else throw new UnsupportedOperationException("input PSS type unsupported!");
	}
	private void setSSData() {
		PssBpaSsTypeXmlType SS=(PssBpaSsTypeXmlType)pssXml;
		//KQV
		double Kqv=SS.getKQV();
		String KqvStr=StringUtil.format(Kqv, 4, 3);
		//TQV
		double Tqv=SS.getTQV().getValue();
		String TqvStr=StringUtil.format(Tqv, 3, 3);
		//KQS
		double Kqs=SS.getKQS();
		String KqsStr=StringUtil.format(Kqs, 4, 3);
		//TQS
		double Tqs=SS.getTQS().getValue();
		String TqsStr=StringUtil.format(Tqs, 3, 3);
		//TQ
		double Tq=SS.getTQ().getValue();
		String TqStr=StringUtil.format(Tq, 4, 2);
		//TQ1
		double Tq1=SS.getTQ1().getValue();
		String Tq1Str=StringUtil.format(Tq1, 4, 3);
		//TQ11
		double T1q1=SS.getT1Q1().getValue();
		String T1q1Str=StringUtil.format(T1q1, 4, 3);
		//TQ2
		double Tq2=SS.getTQ2().getValue();
		String Tq2Str=StringUtil.format(Tq2, 4, 3);
		//TQ21
		double T1q2=SS.getT1Q2().getValue();
		String T1q2Str=StringUtil.format(T1q2, 4, 3);
		//TQ3
		double Tq3=SS.getTQ3().getValue();
		String Tq3Str=StringUtil.format(Tq3, 4, 3);
		//TQ31
		double T1q3=SS.getT1Q3().getValue();
		String T1q3Str=StringUtil.format(T1q3, 4, 3);
		//VSMAX
		double Vsmax=SS.getVSMAX();
		String VsmaxStr=StringUtil.format(Vsmax, 4, 3);
		//VCUTOFF
		double Vcutoff=SS.getVCUTOFF();
		String VcutoffStr=StringUtil.format(Vcutoff, 4, 3);
		//VSLOW
		double Vslow=0;
		if(SS.getVSMIN() != -Vsmax)  Vslow=-SS.getVSMIN();		
		String VslowStr=StringUtil.format(Vslow, 2, 2);
		
		pssStr.append(KqvStr).append(TqvStr).append(KqsStr).append(TqsStr).append(TqStr)
			.append(Tq1Str).append(T1q1Str).append(Tq2Str).append(T1q2Str).append(Tq3Str)
			.append(T1q3Str).append(VsmaxStr).append(VcutoffStr).append(VslowStr);
	}
	private void setSPData() {
		PssBpaSpTypeXmlType SP=(PssBpaSpTypeXmlType)pssXml;
		//KQV
		double Kqv=SP.getKQV();
		String KqvStr=StringUtil.format(Kqv, 4, 3);
		//TQV
		double Tqv=SP.getTQV().getValue();
		String TqvStr=StringUtil.format(Tqv, 3, 3);
		//KQS
		double Kqs=SP.getKQS();
		String KqsStr=StringUtil.format(Kqs, 4, 3);
		//TQS
		double Tqs=SP.getTQS().getValue();
		String TqsStr=StringUtil.format(Tqs, 3, 3);
		//TQ
		double Tq=SP.getTQ().getValue();
		String TqStr=StringUtil.format(Tq, 4, 2);
		//TQ1
		double Tq1=SP.getTQ1().getValue();
		String Tq1Str=StringUtil.format(Tq1, 4, 3);
		//TQ11
		double T1q1=SP.getT1Q1().getValue();
		String T1q1Str=StringUtil.format(T1q1, 4, 3);
		//TQ2
		double Tq2=SP.getTQ2().getValue();
		String Tq2Str=StringUtil.format(Tq2, 4, 3);
		//TQ21
		double T1q2=SP.getT1Q2().getValue();
		String T1q2Str=StringUtil.format(T1q2, 4, 3);
		//TQ3
		double Tq3=SP.getTQ3().getValue();
		String Tq3Str=StringUtil.format(Tq3, 4, 3);
		//TQ31
		double T1q3=SP.getT1Q3().getValue();
		String T1q3Str=StringUtil.format(T1q3, 4, 3);
		//VSMAX
		double Vsmax=SP.getVSMAX();
		String VsmaxStr=StringUtil.format(Vsmax, 4, 3);
		//VCUTOFF
		double Vcutoff=SP.getVCUTOFF();
		String VcutoffStr=StringUtil.format(Vcutoff, 4, 3);
		//VSLOW
		double Vslow=0;
		if(SP.getVSMIN() != -Vsmax)  Vslow=-SP.getVSMIN();		
		String VslowStr=StringUtil.format(Vslow, 2, 2);
		//KQ
		
		pssStr.append(KqvStr).append(TqvStr).append(KqsStr).append(TqsStr).append(TqStr)
			.append(Tq1Str).append(T1q1Str).append(Tq2Str).append(T1q2Str).append(Tq3Str)
			.append(T1q3Str).append(VsmaxStr).append(VcutoffStr).append(VslowStr);
	}
	private void setSGData() {
		PssBpaSgTypeXmlType SG=(PssBpaSgTypeXmlType)pssXml;
		//KQV
		double Kqv=SG.getKQV();
		String KqvStr=StringUtil.format(Kqv, 4, 3);
		//TQV
		double Tqv=SG.getTQV().getValue();
		String TqvStr=StringUtil.format(Tqv, 3, 3);
		//KQS
		double Kqs=SG.getKQS();
		String KqsStr=StringUtil.format(Kqs, 4, 3);
		//TQS
		double Tqs=SG.getTQS().getValue();
		String TqsStr=StringUtil.format(Tqs, 3, 3);
		//TQ
		double Tq=SG.getTQ().getValue();
		String TqStr=StringUtil.format(Tq, 4, 2);
		//TQ1
		double Tq1=SG.getTQ1().getValue();
		String Tq1Str=StringUtil.format(Tq1, 4, 3);
		//TQ11
		double T1q1=SG.getT1Q1().getValue();
		String T1q1Str=StringUtil.format(T1q1, 4, 3);
		//TQ2
		double Tq2=SG.getTQ2().getValue();
		String Tq2Str=StringUtil.format(Tq2, 4, 3);
		//TQ21
		double T1q2=SG.getT1Q2().getValue();
		String T1q2Str=StringUtil.format(T1q2, 4, 3);
		//TQ3
		double Tq3=SG.getTQ3().getValue();
		String Tq3Str=StringUtil.format(Tq3, 4, 3);
		//TQ31
		double T1q3=SG.getT1Q3().getValue();
		String T1q3Str=StringUtil.format(T1q3, 4, 3);
		//VSMAX
		double Vsmax=SG.getVSMAX();
		String VsmaxStr=StringUtil.format(Vsmax, 4, 3);
		//VCUTOFF
		double Vcutoff=SG.getVCUTOFF();
		String VcutoffStr=StringUtil.format(Vcutoff, 4, 3);
		//VSLOW
		double Vslow=0;
		if(SG.getVSMIN() != -Vsmax)  Vslow=-SG.getVSMIN();		
		String VslowStr=StringUtil.format(Vslow, 2, 2);
		//KQ
		
		pssStr.append(KqvStr).append(TqvStr).append(KqsStr).append(TqsStr).append(TqStr)
			.append(Tq1Str).append(T1q1Str).append(Tq2Str).append(T1q2Str).append(Tq3Str)
			.append(T1q3Str).append(VsmaxStr).append(VcutoffStr).append(VslowStr);
	}
	private void setSIData() {
		PssBPADualInputXmlType SI=(PssBPADualInputXmlType)pssXml;
		//TRW
		double Trw=SI.getTrw().getValue();
		String TrwStr=StringUtil.format(Trw, 4, 4);
		//T5
		double T5=SI.getT5().getValue();
		String T5Str=StringUtil.format(T5, 5, 3);
		//T6
		double T6=SI.getT6().getValue();
		String T6Str=StringUtil.format(T6, 5, 3);
		//T7
		double T7=SI.getT7().getValue();
		String T7Str=StringUtil.format(T7, 5, 3);
		//KR
		double Kr=SI.getKr();
		String KrStr=StringUtil.format(Kr, 6, 4);
		//TRP
		double Trp=SI.getTrp().getValue();
		String TrpStr=StringUtil.format(Trp, 4, 4);
		//TW
		double Tw=SI.getTW().getValue();
		String TwStr=StringUtil.format(Tw, 5, 3);
		//TW1
		double Tw1=SI.getTW1().getValue();
		String Tw1Str=StringUtil.format(Tw1, 5, 3);
		//TW2
		double Tw2=SI.getTW2().getValue();
		String Tw2Str=StringUtil.format(Tw2, 5, 3);
		//KS
		double Ks=SI.getKS();
		String KsStr=StringUtil.format(Ks, 4, 2);
		//T9
		double T9=SI.getT9().getValue();
		String T9Str=StringUtil.format(T9, 5, 3);
		//T10
		double T10=SI.getT10().getValue();
		String T10Str=StringUtil.format(T10, 5, 3);
		//T12
		double T12=SI.getT12().getValue();
		String T12Str=StringUtil.format(T12, 5, 3);
		//INP input signal:0 for speed deviation(delta_w) and generator accelerating power(delta_Pg), 
		//1 for only delta_w, 2 for only delta_pg
		double Inp=0;//最少有一个输入信号
		if(SI.getSecondInputSignal()==null) Inp=1;
		if(SI.getFirstInputSignal()==null)	Inp=2;
		String InpStr=StringUtil.format(Inp, 1, 1);
		
		pssStr.append(TrwStr).append(T5Str).append(T6Str).append(T7Str).append(KrStr)
			.append(TrpStr).append(TwStr).append(Tw1Str).append(Tw2Str).append(KsStr)
			.append(T9Str).append(T10Str).append(T12Str).append(InpStr).append("\n");
		
		//SI+数据卡
		pssStr.append("SI+").append(pssStr, 3, 16-ChnNum);
		//KP
		double Kp=SI.getKp();
		String KpStr=StringUtil.format(Kp, 5, 3);
		//T1
		double T1=SI.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//T2
		double T2=SI.getT2().getValue();
		String T2Str=StringUtil.format(T2, 5, 3);
		//T13
		double T13=SI.getT13().getValue();
		String T13Str=StringUtil.format(T13, 5, 3);
		//T14
		double T14=SI.getT14().getValue();
		String T14Str=StringUtil.format(T14, 5, 3);
		//T3
		double T3=SI.getT3().getValue();
		String T3Str=StringUtil.format(T3, 5, 3);
		//T4
		double T4=SI.getT4().getValue();
		String T4Str=StringUtil.format(T4, 5, 3);
		//VSMAX
		double Vsmax=SI.getVSMAX();
		String VsmaxStr=StringUtil.format(Vsmax, 6, 4);
		//VSMIN
		double Vsmin=SI.getVSMIN();
		String VsminStr=StringUtil.format(Vsmin, 6, 4);
		//KrBaseMVA
		double KrBaseMVA=SI.getKrBaseMVA();
		String KrBaseMVAStr=StringUtil.format(KrBaseMVA, 4, 0);
		
		pssStr.append(KpStr).append(T1Str).append(T2Str).append(T13Str).append(T14Str).append(T3Str)
		.append(T4Str).append(VsmaxStr).append(VsminStr).append(StringUtil.getSpace(13)).append(KrBaseMVAStr);
	}
	private static String getType() {
		String type="";
		if(pssXml instanceof PssBpaSsTypeXmlType){
			type="SS";
		}
		else if(pssXml instanceof PssBpaSpTypeXmlType){
			type="SP";
		}
		else if(pssXml instanceof PssBpaSgTypeXmlType){
			type="SG";
		}
		else if(pssXml instanceof PssBPADualInputXmlType){
			type="SI";
		}
		else throw new UnsupportedOperationException("input PSS type unsupported!");
		return type;
	}
	public String stdout() {
		// TODO Auto-generated method stub
		return pssStr.toString();
	}
}

