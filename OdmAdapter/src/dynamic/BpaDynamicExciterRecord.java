package dynamic;

import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.ExcBPAECXmlType;
import org.ieee.odm.schema.ExcBPAEKXmlType;
import org.ieee.odm.schema.ExcBPAFJXmlType;
import org.ieee.odm.schema.ExcBPAFKXmlType;
import org.ieee.odm.schema.ExcBPAFQXmlType;
import org.ieee.odm.schema.ExcBPAFRXmlType;
import org.ieee.odm.schema.ExcBPAFSXmlType;
import org.ieee.odm.schema.ExcBPAFUXmlType;
import org.ieee.odm.schema.ExcBPAFVXmlType;
import org.ieee.odm.schema.ExcIEEE1968Type1XmlType;
import org.ieee.odm.schema.ExcIEEE1981TypeAC2XmlType;
import org.ieee.odm.schema.ExcIEEE1981TypeDC1XmlType;
import org.ieee.odm.schema.ExcSimpleTypeXmlType;
import org.ieee.odm.schema.ExciterModelXmlType;

import util.StringUtil;

public class BpaDynamicExciterRecord {
	private static DStabBusXmlType busXml=null;
	private static DynamicGeneratorXmlType genXml=null;
	private static ExciterModelXmlType excXml=null;
	private static StringBuffer excStr=null;
	private static double baseMVA=100.0D;
	private static int ChnNum=0;
	private static DStabModelParser parser=null;
	
	public BpaDynamicExciterRecord(DStabModelParser parser){
		this.excStr=new StringBuffer();
		this.parser=parser;
		this.baseMVA=parser.getDStabNet().getBasePower().getValue();		
	}
	public String setDynamicExciterData(String bpaLfNetStr,DStabBusXmlType DStabBusXml,DynamicGeneratorXmlType generatorXml){
		busXml=DStabBusXml;
		ChnNum=StringUtil.getChineseCharNum(busXml.getName());
		genXml=generatorXml;
		excXml=genXml.getExciter().getValue();
		excStr=new StringBuffer();
		setDynamicExciterBaseData();
		setDynamicExciterDSData();
		//set type specific data;
		
		// add to the net
		bpaLfNetStr+=excStr+"\n";
		return bpaLfNetStr;		
	}	
	private static void setDynamicExciterBaseData() {
		//get type
		String type=getType();
		String reviseCode=StringUtil.getSpace(1);
		// busName ,RatedVoltage
		String busName=StringUtil.addSpace(busXml.getName(),8);		
		double RatedV=genXml.getRatedVoltage().getValue();
		String RatedVStr=StringUtil.format(RatedV, 4, 0);
		//dynGenId 发电机识别码
		String dynGenId=StringUtil.getSpace(1);
		
		excStr.append(type).append(reviseCode).append(busName).append(RatedVStr).append(dynGenId);
	}
	private static void setDynamicExciterDSData() {
		if(getType()=="EA")setEAData();
		else if(getType()=="EC")setECData();
		else if(getType()=="EK")setEKData();
		else if(getType()=="FA")setFAData();
		else if(getType()=="FF")setFFData();
		else if(getType()=="FJ")setFJData();
		else if(getType()=="FK")setFKData();
		else if(getType()=="FQ")setFQData();
		else if(getType()=="FR")setFRData();
		else if(getType()=="FS")setFSData();
		else if(getType()=="FU")setFUData();
		else if(getType()=="FV")setFVData();
		else throw new UnsupportedOperationException("input Exciter type unsupported!");		
	}
	private static void setEAData() {
		ExcIEEE1968Type1XmlType EA=(ExcIEEE1968Type1XmlType)excXml;
		//TR
		double Tr=EA.getTR().getValue();
		String TrStr=StringUtil.format(Tr, 4, 3);
		//KA
		double Ka=EA.getKA();
		String KaStr=StringUtil.format(Ka, 5, 2);
		//TA
		double Ta=EA.getTA().getValue();
		String TaStr=StringUtil.format(Ta, 4, 2);
		//TA1
		String TA1=StringUtil.getSpace(4);
		//VRminMult  VRmax*multi=Vrmin
		double multi=EA.getVRMIN()/EA.getVRMAX();
		String multiStr=StringUtil.format(multi, 4, 2);
		//KE
		double Ke=EA.getKE();
		String KeStr=StringUtil.format(Ke, 4, 3);
		//TE
		double Te=EA.getTE().getValue();
		String TeStr=StringUtil.format(Te, 4, 3);
		//SE1 SE2
		double SE1=EA.getSE1();
		double SE2=EA.getSE2();
		String SE1Str=StringUtil.format(SE1, 4, 3);
		String SE2Str=StringUtil.format(SE2, 4, 3);
		//EFDMin  EFDMax
		double Efdmin=EA.getEFDMIN();
		double Efdmax=EA.getE1();
		String EfdminStr=StringUtil.format(Efdmin, 5, 3);
		String EfdmaxStr=StringUtil.format(Efdmax, 4, 3);
		//KF
		double Kf=EA.getKF();
		String KfStr=StringUtil.format(Kf, 4, 3);
		//TF
		double Tf=EA.getTF().getValue();
		String TfStr=StringUtil.format(Tf, 4, 3);
		
		excStr.append(TrStr).append(KaStr).append(TaStr).append(TA1).append(multiStr).append(KeStr).append(TeStr)
			.append(SE2Str).append(SE1Str).append(EfdminStr).append(EfdmaxStr).append(KfStr).append(TfStr);		
	}
	private static void setECData() {
		// TODO Auto-generated method stub
		
	}
	private static void setEKData() {
		// TODO Auto-generated method stub
		
	}
	private static void setFAData() {
		setExcSimpleData();
		ExcIEEE1981TypeDC1XmlType FA=(ExcIEEE1981TypeDC1XmlType)excXml;
		//TB
		double Tb=FA.getTB().getValue();
		String TbStr=StringUtil.format(Tb, 5, 3);
		//TC
		double Tc=FA.getTC().getValue();
		String TcStr=StringUtil.format(Tc, 5, 3);
		//KE
		double Ke=FA.getKE();
		String KeStr=StringUtil.format(Ke, 5, 3);
		//TE
		double Te=FA.getTE().getValue();
		String TeStr=StringUtil.format(Te, 4, 3);
		
		excStr.replace(41-ChnNum, 46-ChnNum, TbStr).replace(46-ChnNum, 51-ChnNum, TcStr).append(KeStr).append(TeStr).append("\n");
		
		//FZ数据卡
		excStr.append("FZ").append(excStr, 2, 16-ChnNum);
		//SE1
		double SE1=FA.getSE1();
		String SE1Str=StringUtil.format(SE1, 5, 3);
		//SE2
		double SE2=FA.getSE2();
		String SE2Str=StringUtil.format(SE2, 5, 3);
		//E1
		double E1=FA.getE1();
		String E1Str=StringUtil.format(E1, 5, 3);
//		//E2
//		double E2=FA.getE2();
//		String E2Str=StringUtil.format(E2, 5, 3);
		//KF
		double Kf=FA.getKF();
		String KfStr=StringUtil.format(Kf, 5, 3);
		//TF
		double Tf=FA.getTF().getValue();
		String TfStr=StringUtil.format(Tf, 5, 3);
		excStr.append(SE1Str).append(SE2Str).append(StringUtil.getSpace(5)).append(E1Str).append(KfStr).append(TfStr);
	}
	private static void setFFData() {
		setExcSimpleData();
		ExcIEEE1981TypeAC2XmlType FF=(ExcIEEE1981TypeAC2XmlType)excXml;
		//Vamax
		double Vamax=FF.getVAMAX();
		String VamaxStr=StringUtil.format(Vamax, 5, 3);
		//Vamin
		double Vamin=FF.getVAMIN();
		String VaminStr=StringUtil.format(Vamin, 5, 3);
		//TB
		double Tb=FF.getTB().getValue();
		String TbStr=StringUtil.format(Tb, 5, 3);
		//TC
		double Tc=FF.getTC().getValue();
		String TcStr=StringUtil.format(Tc, 5, 3);
		//KE
		double Ke=FF.getKE();
		String KeStr=StringUtil.format(Ke, 5, 3);
		//TE
		double Te=FF.getTE().getValue();
		String TeStr=StringUtil.format(Te, 4, 3);
		
		excStr.replace(31-ChnNum, 36-ChnNum, VamaxStr).replace(36-ChnNum, 41-ChnNum, VaminStr)
			.replace(41-ChnNum, 46-ChnNum, TbStr).replace(46-ChnNum, 51-ChnNum, TcStr).append(KeStr).append(TeStr);		
	}
	private static void setFJData() {//TODO 南网数据中，数据行中显示Vimax和Vimin有数据，但展开的数据卡中没有
		setExcSimpleData();
		ExcBPAFJXmlType FJ=(ExcBPAFJXmlType)excXml;
		//TB
		double Tb=FJ.getTB().getValue();
		String TbStr=StringUtil.format(Tb, 5, 3);
		//TC
		double Tc=FJ.getTC().getValue();
		String TcStr=StringUtil.format(Tc, 5, 3);
		
		excStr.replace(41-ChnNum, 46-ChnNum, TbStr).replace(46-ChnNum, 51-ChnNum, TcStr).append("\n");
		
		//FZ数据卡
		excStr.append("FZ").append(excStr, 2, 16-ChnNum);
		//EFDMAX
		double EFDmax=FJ.getEFDMAX();
		String EFDmaxStr=StringUtil.format(EFDmax, 5, 3);
		//EFDMIn
		double EFDmin=FJ.getEFDMIN();
		String EFDminStr=StringUtil.format(EFDmin, 5, 3);
		//KF
		double Kf=FJ.getKF();
		String KfStr=StringUtil.format(Kf, 5, 3);
		//TF
		double Tf=FJ.getTF().getValue();
		String TfStr=StringUtil.format(Tf, 5, 3);
		//KC
		double Kc=FJ.getKC();
		String KcStr=StringUtil.format(Kc, 5, 4);
		excStr.append(StringUtil.getSpace(10)).append(EFDminStr).append(EFDmaxStr).append(KfStr).append(TfStr).append(KcStr);
	}
	private static void setFKData() {//TODO 南网数据里面是有KE和TE的
		setExcSimpleData();
		ExcBPAFKXmlType FK=(ExcBPAFKXmlType)excXml;
		//VIMAX
		double Vimax=FK.getVIMAX();
		String VimaxStr=StringUtil.format(Vimax, 5, 3);
		//VIMIN
		double Vimin=FK.getVIMIN();
		String ViminStr=StringUtil.format(Vimin, 5, 3);
		//TB
		double Tb=FK.getTB().getValue();
		String TbStr=StringUtil.format(Tb, 5, 3);
		//TC
		double Tc=FK.getTC().getValue();
		String TcStr=StringUtil.format(Tc, 5, 3);
		
		excStr.replace(31-ChnNum, 36-ChnNum, VimaxStr).replace(36-ChnNum, 41-ChnNum, ViminStr)
			.replace(41-ChnNum, 46-ChnNum, TbStr).replace(46-ChnNum, 51-ChnNum, TcStr).append("\n");	
		
		//FZ数据卡
		excStr.append("FZ").append(excStr, 2, 16-ChnNum);
		//KF
		double Kf=FK.getKF();
		String KfStr=StringUtil.format(Kf, 5, 3);
		//TF
		double Tf=FK.getTF().getValue();
		String TfStr=StringUtil.format(Tf, 5, 3);
		//KC
		double Kc=FK.getKC();
		String KcStr=StringUtil.format(Kc, 5, 4);
		excStr.append(StringUtil.getSpace(20)).append(KfStr).append(TfStr).append(KcStr);
	}
	private static void setFQData() {
		setExcSimpleData();
		ExcBPAFQXmlType FQ=(ExcBPAFQXmlType)excXml;
		//RC
		double Rc=FQ.getLoadRc();
		String RcStr=StringUtil.format(Rc, 4, 3);
		//XC
		double Xc=FQ.getLoadXc();
		String XcStr=StringUtil.format(Xc, 4, 3);
		//K
		double K=FQ.getK();
		String KStr=StringUtil.format(K, 5, 3);
		//KV
		double Kv=FQ.getKV();//TODO 本身BPA到InterPSS的转换已经不对
		String KvStr=StringUtil.format(Kv, 3, 0);
		//T1
		double T1=FQ.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//T2
		double T2=FQ.getT2().getValue();
		String T2Str=StringUtil.format(T2, 5, 3);
		//T3
		double T3=FQ.getT3().getValue();
		String T3Str=StringUtil.format(T3, 5, 3);
		//T4
		double T4=FQ.getT4().getValue();
		String T4Str=StringUtil.format(T4, 5, 3);
		//KF
		double Kf=FQ.getKF();
		String KfStr=StringUtil.format(Kf, 5, 3);
		//TF
		double Tf=FQ.getTF().getValue();
		String TfStr=StringUtil.format(Tf, 4, 3);
		//KH
		double Kh=FQ.getKH();
		String KhStr=StringUtil.format(Kh, 4, 2);
		
		excStr.replace(16-ChnNum, 21-ChnNum, RcStr).replace(20-ChnNum, 25-ChnNum, XcStr)
			.replace(49-ChnNum, 49-ChnNum, StringUtil.getSpace(8)).replace(29-ChnNum, 34-ChnNum, KStr).replace(34-ChnNum, 37-ChnNum, KvStr)
			.replace(37-ChnNum, 42-ChnNum, T1Str).replace(42-ChnNum, 47-ChnNum, T2Str).replace(47-ChnNum, 52-ChnNum, T3Str)
			.replace(52-ChnNum, 57-ChnNum, T4Str).delete(67-ChnNum, 77).append(KfStr).append(TfStr).append(KhStr).append("\n");
		
		//F+数据卡
		excStr.append("F+").append(excStr, 2, 16-ChnNum);
		//VAMAX
		double Vamax=FQ.getVAMAX();
		String VamaxStr=StringUtil.format(Vamax, 5, 3);
		//VAMIN
		double Vamin=FQ.getVAMIN();
		String VaminStr=StringUtil.format(Vamin, 5, 3);
		//KB
		double Kb=FQ.getKB();
		String KbStr=StringUtil.format(Kb, 4, 2);
		//T5
		double T5=FQ.getT5().getValue();
		String T5Str=StringUtil.format(T5, 4, 2);
		//KE
		double Ke=FQ.getKE();
		String KeStr=StringUtil.format(Ke, 4, 2);
		//TE
		double Te=FQ.getTE().getValue();
		String TeStr=StringUtil.format(Te, 4, 2);
		//SE1
		double SE1=FQ.getSE1();
		String SE1Str=StringUtil.format(SE1, 5, 4);
		//SE2
		double SE2=FQ.getSE2();
		String SE2Str=StringUtil.format(SE2, 5, 4);
		//VRMAX
		double Vrmax=FQ.getVrmax();
		String VrmaxStr=StringUtil.format(Vrmax, 4, 2);
		//VRMIN
		double Vrmin=FQ.getVrmin();
		String VrminStr=StringUtil.format(Vrmin, 4, 2);
		//KC
		double Kc=FQ.getKC();
		String KcStr=StringUtil.format(Kc, 4, 2);
		//KD
		double Kd=FQ.getKD();
		String KdStr=StringUtil.format(Kd, 4, 2);
		//KL1
		double Kl1=FQ.getKL1();
		String Kl1Str=StringUtil.format(Kl1, 4, 2);
		//VL1R
		double Vl1r=FQ.getVL1R();
		String Vl1rStr=StringUtil.format(Vl1r, 4, 2);
		//EFDMAX
		double Efdmax=FQ.getEFDmax();
		String EfdmaxStr=StringUtil.format(Efdmax, 4, 2);
		
		excStr.append(VamaxStr).append(VaminStr).append(KbStr).append(T5Str).append(KeStr).append(TeStr)
			.append(SE1Str).append(SE2Str).append(VrmaxStr).append(VrminStr).append(KcStr).append(KdStr)
			.append(Kl1Str).append(Vl1rStr).append(EfdmaxStr);
	}
	private static void setFRData() {
		setExcSimpleData();
		ExcBPAFRXmlType FR=(ExcBPAFRXmlType)excXml;
		//RC
		double Rc=FR.getLoadRc();
		String RcStr=StringUtil.format(Rc, 4, 3);
		//XC
		double Xc=FR.getLoadXc();
		String XcStr=StringUtil.format(Xc, 4, 3);
		//K
		double K=FR.getK();
		String KStr=StringUtil.format(K, 5, 3);
		//KV
		double Kv=FR.getKV();//TODO 本身BPA到InterPSS的转换已经不对
		String KvStr=StringUtil.format(Kv, 3, 0);
		//T1
		double T1=FR.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//T2
		double T2=FR.getT2().getValue();
		String T2Str=StringUtil.format(T2, 5, 3);
		//T3
		double T3=FR.getT3().getValue();
		String T3Str=StringUtil.format(T3, 5, 3);
		//T4
		double T4=FR.getT4().getValue();
		String T4Str=StringUtil.format(T4, 5, 3);
		//KF
		double Kf=FR.getKF();
		String KfStr=StringUtil.format(Kf, 5, 3);
		//TF
		double Tf=FR.getTF().getValue();
		String TfStr=StringUtil.format(Tf, 4, 3);
		//KH
		double Kh=FR.getKH();
		String KhStr=StringUtil.format(Kh, 4, 2);
		
		excStr.replace(16-ChnNum, 21-ChnNum, RcStr).replace(20-ChnNum, 25-ChnNum, XcStr)
			.replace(49-ChnNum, 49-ChnNum, StringUtil.getSpace(8)).replace(29-ChnNum, 34-ChnNum, KStr).replace(34-ChnNum, 37-ChnNum, KvStr)
			.replace(37-ChnNum, 42-ChnNum, T1Str).replace(42-ChnNum, 47-ChnNum, T2Str).replace(47-ChnNum, 52-ChnNum, T3Str)
			.replace(52-ChnNum, 57-ChnNum, T4Str).delete(67-ChnNum, 77).append(KfStr).append(TfStr).append(KhStr).append("\n");		
		
		//F+数据卡
		excStr.append("F+").append(excStr, 2, 16-ChnNum);
		//VAMAX
		double Vamax=FR.getVAMAX();
		String VamaxStr=StringUtil.format(Vamax, 5, 3);
		//VAMIN
		double Vamin=FR.getVAMIN();
		String VaminStr=StringUtil.format(Vamin, 5, 3);
		//KB
		double Kb=FR.getKB();
		String KbStr=StringUtil.format(Kb, 4, 2);
		//T5
		double T5=FR.getT5().getValue();
		String T5Str=StringUtil.format(T5, 4, 2);
		//KE
		double Ke=FR.getKE();
		String KeStr=StringUtil.format(Ke, 4, 2);
		//TE
		double Te=FR.getTE().getValue();
		String TeStr=StringUtil.format(Te, 4, 2);
		//SE1
		double SE1=FR.getSE1();
		String SE1Str=StringUtil.format(SE1, 5, 4);
		//SE2
		double SE2=FR.getSE2();
		String SE2Str=StringUtil.format(SE2, 5, 4);
		//VRMAX
		double Vrmax=FR.getVrmax();
		String VrmaxStr=StringUtil.format(Vrmax, 4, 2);
		//VRMIN
		double Vrmin=FR.getVrmin();
		String VrminStr=StringUtil.format(Vrmin, 4, 2);
		//KC
		double Kc=FR.getKC();
		String KcStr=StringUtil.format(Kc, 4, 2);
		//KD
		double Kd=FR.getKD();
		String KdStr=StringUtil.format(Kd, 4, 2);
		//KL1
		double Kl1=FR.getKL1();
		String Kl1Str=StringUtil.format(Kl1, 4, 2);
		//VL1R
		double Vl1r=FR.getVL1R();
		String Vl1rStr=StringUtil.format(Vl1r, 4, 2);
		//EFDMAX
		double Efdmax=FR.getEFDmax();
		String EfdmaxStr=StringUtil.format(Efdmax, 4, 2);
		
		excStr.append(VamaxStr).append(VaminStr).append(KbStr).append(T5Str).append(KeStr).append(TeStr)
			.append(SE1Str).append(SE2Str).append(VrmaxStr).append(VrminStr).append(KcStr).append(KdStr)
			.append(Kl1Str).append(Vl1rStr).append(EfdmaxStr);
	}
	private static void setFSData() {
		setExcSimpleData();
		ExcBPAFSXmlType FS=(ExcBPAFSXmlType)excXml;
		//RC
		double Rc=FS.getLoadRc();
		String RcStr=StringUtil.format(Rc, 4, 3);
		//XC
		double Xc=FS.getLoadXc();
		String XcStr=StringUtil.format(Xc, 4, 3);
		//K
		double K=FS.getK();
		String KStr=StringUtil.format(K, 5, 3);
		//KV
		double Kv=FS.getKV();//TODO 本身BPA到InterPSS的转换已经不对
		String KvStr=StringUtil.format(Kv, 3, 0);
		//T1
		double T1=FS.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//T2
		double T2=FS.getT2().getValue();
		String T2Str=StringUtil.format(T2, 5, 3);
		//T3
		double T3=FS.getT3().getValue();
		String T3Str=StringUtil.format(T3, 5, 3);
		//T4
		double T4=FS.getT4().getValue();
		String T4Str=StringUtil.format(T4, 5, 3);
		//KF
		double Kf=FS.getKF();
		String KfStr=StringUtil.format(Kf, 5, 3);
		//TF
		double Tf=FS.getTF().getValue();
		String TfStr=StringUtil.format(Tf, 4, 3);
		//KH
		double Kh=FS.getKH();
		String KhStr=StringUtil.format(Kh, 4, 2);
		
		excStr.replace(16-ChnNum, 21-ChnNum, RcStr).replace(20-ChnNum, 25-ChnNum, XcStr)
			.replace(49-ChnNum, 49-ChnNum, StringUtil.getSpace(8)).replace(29-ChnNum, 34-ChnNum, KStr).replace(34-ChnNum, 37-ChnNum, KvStr)
			.replace(37-ChnNum, 42-ChnNum, T1Str).replace(42-ChnNum, 47-ChnNum, T2Str).replace(47-ChnNum, 52-ChnNum, T3Str)
			.replace(52-ChnNum, 57-ChnNum, T4Str).delete(67-ChnNum, 77).append(KfStr).append(TfStr).append(KhStr).append("\n");		
		
		//F+数据卡
		excStr.append("F+").append(excStr, 2, 16-ChnNum);
		//VAMAX
		double Vamax=FS.getVAMAX();
		String VamaxStr=StringUtil.format(Vamax, 5, 3);
		//VAMIN
		double Vamin=FS.getVAMIN();
		String VaminStr=StringUtil.format(Vamin, 5, 3);
		//KB
		double Kb=FS.getKB();
		String KbStr=StringUtil.format(Kb, 4, 2);
		//T5
		double T5=FS.getT5().getValue();
		String T5Str=StringUtil.format(T5, 4, 2);
		//KE
		double Ke=FS.getKE();
		String KeStr=StringUtil.format(Ke, 4, 2);
		//TE
		double Te=FS.getTE().getValue();
		String TeStr=StringUtil.format(Te, 4, 2);
		//SE1
		double SE1=FS.getSE1();
		String SE1Str=StringUtil.format(SE1, 5, 4);
		//SE2
		double SE2=FS.getSE2();
		String SE2Str=StringUtil.format(SE2, 5, 4);
		//VRMAX
		double Vrmax=FS.getVrmax();
		String VrmaxStr=StringUtil.format(Vrmax, 4, 2);
		//VRMIN
		double Vrmin=FS.getVrmin();
		String VrminStr=StringUtil.format(Vrmin, 4, 2);
		//KC
		double Kc=FS.getKC();
		String KcStr=StringUtil.format(Kc, 4, 2);
		//KD
		double Kd=FS.getKD();
		String KdStr=StringUtil.format(Kd, 4, 2);
		//KL1
		double Kl1=FS.getKL1();
		String Kl1Str=StringUtil.format(Kl1, 4, 2);
		//VL1R
		double Vl1r=FS.getVL1R();
		String Vl1rStr=StringUtil.format(Vl1r, 4, 2);
		//EFDMAX
		double Efdmax=FS.getEFDmax();
		String EfdmaxStr=StringUtil.format(Efdmax, 4, 2);
		
		excStr.append(VamaxStr).append(VaminStr).append(KbStr).append(T5Str).append(KeStr).append(TeStr)
			.append(SE1Str).append(SE2Str).append(VrmaxStr).append(VrminStr).append(KcStr).append(KdStr)
			.append(Kl1Str).append(Vl1rStr).append(EfdmaxStr);
	}
	private static void setFUData() {
		setExcSimpleData();
		ExcBPAFUXmlType FU=(ExcBPAFUXmlType)excXml;
		//RC
		double Rc=FU.getLoadRc();
		String RcStr=StringUtil.format(Rc, 4, 3);
		//XC
		double Xc=FU.getLoadXc();
		String XcStr=StringUtil.format(Xc, 4, 3);
		//K
		double K=FU.getK();
		String KStr=StringUtil.format(K, 5, 3);
		//KV
		double Kv=FU.getKV();//TODO 本身BPA到InterPSS的转换已经不对
		String KvStr=StringUtil.format(Kv, 3, 0);
		//T1
		double T1=FU.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//T2
		double T2=FU.getT2().getValue();
		String T2Str=StringUtil.format(T2, 5, 3);
		//T3
		double T3=FU.getT3().getValue();
		String T3Str=StringUtil.format(T3, 5, 3);
		//T4
		double T4=FU.getT4().getValue();
		String T4Str=StringUtil.format(T4, 5, 3);
		//KF
		double Kf=FU.getKF();
		String KFUtr=StringUtil.format(Kf, 5, 3);
		//TF
		double Tf=FU.getTF().getValue();
		String TFUtr=StringUtil.format(Tf, 4, 3);
		
		excStr.replace(16-ChnNum, 21-ChnNum, RcStr).replace(20-ChnNum, 25-ChnNum, XcStr)
			.replace(49-ChnNum, 49-ChnNum, StringUtil.getSpace(8)).replace(29-ChnNum, 34-ChnNum, KStr).replace(34-ChnNum, 37-ChnNum, KvStr)
			.replace(37-ChnNum, 42-ChnNum, T1Str).replace(42-ChnNum, 47-ChnNum, T2Str).replace(47-ChnNum, 52-ChnNum, T3Str)
			.replace(52-ChnNum, 57-ChnNum, T4Str).delete(67-ChnNum, 77).append(KFUtr).append(TFUtr).append("\n");		
		
		//F+数据卡
		excStr.append("F+").append(excStr, 2, 16-ChnNum);
		//VAMAX
		double Vamax=FU.getVAMAX();
		String VamaxStr=StringUtil.format(Vamax, 5, 3);
		//VAMIN
		double Vamin=FU.getVAMIN();
		String VaminStr=StringUtil.format(Vamin, 5, 3);
		//VRMAX
		double Vrmax=FU.getVrmax();
		String VrmaxStr=StringUtil.format(Vrmax, 4, 2);
		//VRMIN
		double Vrmin=FU.getVrmin();
		String VrminStr=StringUtil.format(Vrmin, 4, 2);
		//KC
		double Kc=FU.getKC();
		String KcStr=StringUtil.format(Kc, 4, 2);
		
		excStr.append(VamaxStr).append(VaminStr).append(StringUtil.getSpace(26))
			.append(VrmaxStr).append(VrminStr).append(KcStr);
	}
	private static void setFVData() {
		setExcSimpleData();
		ExcBPAFVXmlType FV=(ExcBPAFVXmlType)excXml;
		//RC
		double Rc=FV.getLoadRc();
		String RcStr=StringUtil.format(Rc, 4, 3);
		//XC
		double Xc=FV.getLoadXc();
		String XcStr=StringUtil.format(Xc, 4, 3);
		//K
		double K=FV.getK();
		String KStr=StringUtil.format(K, 5, 3);
		//KV
		double Kv=FV.getKV();//TODO 本身BPA到InterPSS的转换已经不对
		String KvStr=StringUtil.format(Kv, 3, 0);
		//T1
		double T1=FV.getT1().getValue();
		String T1Str=StringUtil.format(T1, 5, 3);
		//T2
		double T2=FV.getT2().getValue();
		String T2Str=StringUtil.format(T2, 5, 3);
		//T3
		double T3=FV.getT3().getValue();
		String T3Str=StringUtil.format(T3, 5, 3);
		//T4
		double T4=FV.getT4().getValue();
		String T4Str=StringUtil.format(T4, 5, 3);
		//KF
		double Kf=FV.getKF();
		String KFVtr=StringUtil.format(Kf, 5, 3);
		//TF
		double Tf=FV.getTF().getValue();
		String TFVtr=StringUtil.format(Tf, 4, 3);
		
		excStr.replace(16-ChnNum, 21-ChnNum, RcStr).replace(20-ChnNum, 25-ChnNum, XcStr)
			.replace(49-ChnNum, 49-ChnNum, StringUtil.getSpace(8)).replace(29-ChnNum, 34-ChnNum, KStr).replace(34-ChnNum, 37-ChnNum, KvStr)
			.replace(37-ChnNum, 42-ChnNum, T1Str).replace(42-ChnNum, 47-ChnNum, T2Str).replace(47-ChnNum, 52-ChnNum, T3Str)
			.replace(52-ChnNum, 57-ChnNum, T4Str).delete(67-ChnNum, 77).append(KFVtr).append(TFVtr).append("\n");		
		
		//F+数据卡
		excStr.append("F+").append(excStr, 2, 16-ChnNum);
		//VAMAX
		double Vamax=FV.getVAMAX();
		String VamaxStr=StringUtil.format(Vamax, 5, 3);
		//VAMIN
		double Vamin=FV.getVAMIN();
		String VaminStr=StringUtil.format(Vamin, 5, 3);
		//VRMAX
		double Vrmax=FV.getVrmax();
		String VrmaxStr=StringUtil.format(Vrmax, 4, 2);
		//VRMIN
		double Vrmin=FV.getVrmin();
		String VrminStr=StringUtil.format(Vrmin, 4, 2);
		//KC
		double Kc=FV.getKC();
		String KcStr=StringUtil.format(Kc, 4, 2);
		
		excStr.append(VamaxStr).append(VaminStr).append(StringUtil.getSpace(26))
			.append(VrmaxStr).append(VrminStr).append(KcStr);
	}
	private static void setExcSimpleData() {
		ExcSimpleTypeXmlType excSimple=(ExcSimpleTypeXmlType)excXml;
		//RC
		double Rc=excSimple.getLoadRc();
		String RcStr=StringUtil.format(Rc, 5, 4);
		//XC
		double Xc=excSimple.getLoadXc();
		String XcStr=StringUtil.format(Xc, 5, 4);
		//TR
		double Tr=excSimple.getTransTr().getValue();
		String TrStr=StringUtil.format(Tr, 5, 4);
		//Space	
		String space32to51=StringUtil.getSpace(20);
		//KA
		double Ka=excSimple.getKa();
		String KaStr=StringUtil.format(Ka, 5, 2);
		//TA
		double Ta=excSimple.getTa().getValue();
		String TaStr=StringUtil.format(Ta, 5, 3);
		//VRMAX
		double Vrmax=excSimple.getVrmax();
		String VrmaxStr=StringUtil.format(Vrmax, 5, 3);
		//VRMIN
		double Vrmin=excSimple.getVrmin();
		String VrminStr=StringUtil.format(Vrmin, 5, 3);
		
		excStr.append(RcStr).append(XcStr).append(TrStr).append(space32to51).append(KaStr)
			.append(TaStr).append(VrmaxStr).append(VrminStr);
	}
	private static String getType(){		
		String type="";
		if(excXml instanceof ExcIEEE1968Type1XmlType){
			type="EA";
		}
		else if(excXml instanceof ExcBPAECXmlType){
			type="EC";			
		}
		else if(excXml instanceof ExcBPAEKXmlType){
			type="EK";			
		}
		else if(excXml instanceof ExcIEEE1981TypeDC1XmlType){
			type="FA";			
		}
		else if(excXml instanceof ExcIEEE1981TypeAC2XmlType){
			type="FF";			
		}
		else if(excXml instanceof ExcBPAFJXmlType){
			type="FJ";			
		}
		else if(excXml instanceof ExcBPAFKXmlType){
			type="FK";			
		}
		else if(excXml instanceof ExcBPAFQXmlType){
			type="FQ";			
		}
		else if(excXml instanceof ExcBPAFRXmlType){
			type="FR";			
		}
		else if(excXml instanceof ExcBPAFSXmlType){
			type="FS";			
		}
		else if(excXml instanceof ExcBPAFUXmlType){
			type="FU";			
		}
		else if(excXml instanceof ExcBPAFVXmlType){
			type="FV";			
		}
		else throw new UnsupportedOperationException("input Exciter type unsupported!");
		return type;
	}
	public String stdout(){
		return excStr.toString();
	}
}
