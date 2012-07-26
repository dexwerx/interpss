package org.interpss.odm.converter.BPA.dynamic;

import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.AbstractMachineXmlType;
import org.ieee.odm.schema.ClassicMachineXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.Eq1Ed1MachineXmlType;
import org.ieee.odm.schema.Eq11MachineXmlType;
import org.ieee.odm.schema.Eq11Ed11MachineXmlType;
import org.ieee.odm.schema.Eq1MachineXmlType;

import util.StringUtil;

public class BpaDynamicGeneratorRecord {
	private static DStabBusXmlType busXml=null;
	private static DynamicGeneratorXmlType genXml=null;
	private static AbstractMachineXmlType machXml=null;
	private static StringBuffer genStr=null;
	private static double baseMVA=100.0D;
	private static DStabModelParser parser=null;
	private static String type=null;
	
	public BpaDynamicGeneratorRecord(DStabModelParser DStabparser){
		parser=DStabparser;
		baseMVA=parser.getDStabNet().getBasePower().getValue();		
	}
	public String setDynamicGeneratorData(String bpaLfNetStr,DStabBusXmlType DStabBusXml,DynamicGeneratorXmlType generatorXml){
		busXml=DStabBusXml;
		genXml=generatorXml;
		machXml=(AbstractMachineXmlType)genXml.getMachineModel().getValue();
		genStr=new StringBuffer();
		type=getType();
		if(type=="M "){
			setDynamicGeneratorBaseData1();//M card
			setDynamicGeneratorDSData();
			type="MF";
		}
		if(type=="MF"||type=="MC"){
			setDynamicGeneratorBaseData2();//MC & MF
			setDynamicGeneratorDSData();
		}
		//set type specific data;
		
		// add to the net
		bpaLfNetStr+=genStr+"\n";
		return bpaLfNetStr;
		
	}
	private static void setDynamicGeneratorBaseData1(){//M card
		String reviseCode=StringUtil.getSpace(1);
		// busName ,RatedVoltage
		String busName=StringUtil.addSpace(busXml.getName(),8);
		double RatedV=genXml.getRatedVoltage().getValue();
		String RatedVStr=StringUtil.format(RatedV, 4, 0);
		//dynGenId 发电机识别码
		String dynGenId=StringUtil.getSpace(1);
		//MVARating 额定容量
		String MVARating=StringUtil.getSpace(5);
		//PF 电机功率因素
		String PF=StringUtil.getSpace(3);
		//TYPE 发电机类型
		String TYPE=StringUtil.getSpace(2);
		//OWNER 所有者
		String OWNER=StringUtil.getSpace(3);
		
		genStr.append(type).append(reviseCode).append(busName).append(RatedVStr).append(dynGenId).append(MVARating)
		      .append(" ").append(PF).append("     ").append(TYPE).append(" ").append(OWNER).append(" ");
	}
	private static void setDynamicGeneratorBaseData2(){//MC & MF
		String reviseCode=StringUtil.getSpace(1);
		// busName ,RatedVoltage
		String busName=StringUtil.addSpace(busXml.getName(),8);		
		double RatedV=genXml.getRatedVoltage().getValue();
		String RatedVStr=StringUtil.format(RatedV, 4, 0);
		//dynGenId 发电机识别码
		String dynGenId=StringUtil.getSpace(1);
		//MvaBase 基准容量
		double MvaBase=genXml.getRatedPower().getValue();
		String MvaBaseStr=(MvaBase==baseMVA)?StringUtil.getSpace(4):StringUtil.format(MvaBase, 4, 0);
		//EMWS
		String Emws;
		double h=machXml.getH();
		if(h==999999){
			Emws="999999";
		}else{
			Emws=StringUtil.format(h*MvaBase, 6, 0);
		}
		//pContri、qContri
		double pContri=genXml.getPContributionPercent();
		double qContri=genXml.getQContributionPercent();
		String pContriStr=StringUtil.format(pContri, 3, 0);
		String qContriStr=StringUtil.format(qContri, 3, 0);
		pContriStr=(pContriStr.equals("100"))?StringUtil.getSpace(3):pContriStr;
		qContriStr=(qContriStr.equals("100"))?StringUtil.getSpace(3):qContriStr;
				
		genStr.append(type).append(reviseCode).append(busName).append(RatedVStr).append(dynGenId)
				.append(Emws).append(pContriStr).append(qContriStr).append(MvaBaseStr);	
	}
	private static void setDynamicGeneratorDSData(){
		if(type=="MC")setMCData();
		else if(type=="MF")setMFData();
		else if(type=="M ")setMData();
		else throw new UnsupportedOperationException("input Generator type unsupported!");
	}
	private static void setMCData() {
		ClassicMachineXmlType MC=(ClassicMachineXmlType)machXml;
		//Ra 不填
		String ra=StringUtil.getSpace(4);
		//Xd1
		double xd1=MC.getXd1();
		String xd1Str=StringUtil.format(xd1, 5, 4);
		//space
		String space=StringUtil.getSpace(36);
		//D 阻尼系数
		double D=MC.getD()/2;
		String DStr=StringUtil.format(D, 3, 2);
		
		genStr.append(ra).append(xd1Str).append(space).append(DStr);
	}
	private static void setMFData() {
		//Xq1 Tq01  if is Eq1Ed1
		String xq1Str = StringUtil.getSpace(5);
		String tq01Str = StringUtil.getSpace(3);
		if(machXml instanceof Eq1Ed1MachineXmlType){
			Eq1Ed1MachineXmlType MF=(Eq1Ed1MachineXmlType)machXml;
			double xq1=MF.getXq1();
			double tq01=MF.getTq01().getValue();
			xq1Str=StringUtil.format(xq1, 5, 4);
			tq01Str=StringUtil.format(tq01, 3, 2);
		}
		Eq1MachineXmlType MF=(Eq1MachineXmlType)machXml;
		//Ra
		double ra=MF.getRa();
		String raStr=StringUtil.format(ra, 4, 4);
		//Xd1
		double xd1=MF.getXd1();
		String xd1Str=StringUtil.format(xd1, 5, 4);
		//Xd
		double xd=MF.getXd();
		String xdStr=StringUtil.format(xd, 5, 4);
		//Xq
		double xq=MF.getXq();
		String xqStr=StringUtil.format(xq, 5, 4);
		
		if(tq01Str.trim().isEmpty())	
			xq1Str=xqStr;//凸极机的Tq01=0、Xq1=Xq
		
		//Td01
		double td01=MF.getTd01().getValue();
		String td01Str=StringUtil.format(td01, 4, 2);
		//Xl
		double xl=MF.getXl();
		String xlStr=(xl==0.95*xd1)?StringUtil.getSpace(5):StringUtil.format(xl, 5, 4);
		//SE1 SE2
		double SE1=MF.getSeFmt1().getSe100();
		double SE2=MF.getSeFmt1().getSe120();
		String SE1Str=StringUtil.format(SE1/100, 5, 4);
		String SE2Str=StringUtil.format(SE2/100, 4, 3);
		//D 阻尼系数
		double D=MF.getD()/2;
		String DStr=StringUtil.format(D, 3, 2);
		
		genStr.append(raStr).append(xd1Str).append(xq1Str).append(xdStr).append(xqStr).append(td01Str).append(tq01Str)
				.append(xlStr).append(SE1Str).append(SE2Str).append(DStr);
		
	}
	private static void setMData() {
		double xd11,xq11,td011,tq011;
		String xd11Str,xq11Str,td011Str,tq011Str;
		xd11Str=xq11Str=td011Str=tq011Str=null;
		if(machXml instanceof Eq11Ed11MachineXmlType){
			Eq11Ed11MachineXmlType M = (Eq11Ed11MachineXmlType)machXml; 
			//Xd11
			xd11=M.getXd11();
			xd11Str=StringUtil.format(xd11, 5, 4);
			//Xd11
			xq11=M.getXq11();
			xq11Str=StringUtil.format(xq11, 5, 4);
			//Td011
			td011=M.getTd011().getValue();
			td011Str=StringUtil.format(td011, 4, 4);
			//Tq011
			tq011=M.getTq011().getValue();
			tq011Str=StringUtil.format(tq011, 4, 4);
		}else{
			Eq11MachineXmlType M = (Eq11MachineXmlType)machXml; 
			//Xd11
			xd11=M.getXd11();
			xd11Str=StringUtil.format(xd11, 5, 4);
			//Xd11
			xq11=M.getXq11();
			xq11Str=StringUtil.format(xq11, 5, 4);
			//Td011
			td011=M.getTd011().getValue();
			td011Str=StringUtil.format(td011, 4, 4);
			//Tq011
			tq011=M.getTq011().getValue();
			tq011Str=StringUtil.format(tq011, 4, 4);
		}		
		genStr.append(xd11Str).append(xq11Str).append(td011Str).append(tq011Str).append("\n");
	}
	private static String getType(){		
		String type="";
		if(machXml instanceof Eq11Ed11MachineXmlType||machXml instanceof Eq11MachineXmlType){
			type="M ";			
		}
		else if(machXml instanceof ClassicMachineXmlType){
			type="MC";
		}
		else if(machXml instanceof Eq1Ed1MachineXmlType||machXml instanceof Eq1MachineXmlType){
			type="MF";			
		}
		else throw new UnsupportedOperationException("input Generator type unsupported!");
		return type;
	}
	public String stdout(){
		return genStr.toString();
	}
}
