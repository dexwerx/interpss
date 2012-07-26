package org.interpss.odm.converter.BPA.dynamic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DynamicGeneratorXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;

import util.StringUtil;


public class ODMBPAAdapter2 {
	private static LoadflowNetXmlType netXml=null;
	private static String bpaStr="";
	private static DStabModelParser parser=null;
	
    public ODMBPAAdapter2(DStabModelParser DStabparser){
    	parser=DStabparser;
    	netXml=parser.getAclfNet();
    }
    public ODMBPAAdapter2(String xmlFile){
    	File file=new File(xmlFile);
    	parser = ODMObjectFactory.createDStabModelParser();
    	parser.parse(file);
        netXml=parser.getAclfNet();
    }
    public boolean ODM2BPA(String caseId,String projectName, String bpaFilePath){
    	boolean noError=true;
//    	double baseMVA=netXml.getBasePower().getValue();
   		try {
   			bpaStr+=".#define bsefile \""+caseId+".bse\""+"\n";
   			bpaStr+="CASE "+projectName+"\n";
   			bpaStr+=".End of head info data, start machine info data."+"\n";
	   		ArrayList<String> loadCards=new ArrayList<String>();
	   		String XRStr="";
	   		NetworkXmlType.BusList busList=parser.getDStabNet().getBusList();
			int size=busList.getBus().size();
			for(int i=0;i<size;i++){
				BusXmlType Bus = busList.getBus().get(i).getValue();
				DStabBusXmlType DStabBus=parser.getDStabBus(Bus.getId());
				if(DStabBus.getDynamicGen()!=null){
					for(DynamicGeneratorXmlType dynGen:DStabBus.getDynamicGen()){
						BpaDynamicGeneratorRecord bpaGenRecord=new BpaDynamicGeneratorRecord(parser);
						bpaStr=bpaGenRecord.setDynamicGeneratorData(bpaStr, DStabBus, dynGen);
						if(dynGen.getExciter()!=null){
							BpaDynamicExciterRecord bpaExcRecord=new BpaDynamicExciterRecord(parser);
							bpaStr=bpaExcRecord.setDynamicExciterData(bpaStr, DStabBus, dynGen);
						}
						if(dynGen.getStabilizer()!=null){
							BpaDynamicPSSRecord bpaPSSRecord=new BpaDynamicPSSRecord(parser);
							bpaStr=bpaPSSRecord.setDynamicPSSData(bpaStr, DStabBus, dynGen);
						}
						if(dynGen.getGovernor()!=null){
							BpaDynamicTurbineGovernorRecord bpaGovRecord=new BpaDynamicTurbineGovernorRecord(parser);
							bpaStr=bpaGovRecord.setDynamicTurbineGovernorData(bpaStr, DStabBus, dynGen);
						}
						bpaStr+="\n";
					}
				}
				if(DStabBus.getDynamicLoad()!=null){
					BpaDynamicLoadCharacteristicRecord bpaLoadRecord=new BpaDynamicLoadCharacteristicRecord(parser);
					bpaLoadRecord.setDynamicLoadCharacteristicData(bpaStr, DStabBus);
					loadCards.add(bpaLoadRecord.stdout());
				}
				if(DStabBus.getShuntLoadZeroZ()!=null ){
					BpaDynamicSequenceRecord bpaSeqRecord=new BpaDynamicSequenceRecord(parser);
					XRStr=bpaSeqRecord.setDynamicSequenceData(XRStr, DStabBus);
				}
		    }
			//LO、XO、XR cards
			bpaStr+=".End of machine info data, start zero sequence data."+"\n";
			NetworkXmlType.BranchList branchList=parser.getDStabNet().getBranchList();
			int branchSize=branchList.getBranch().size();
			String LOStr="",XOStr="";
			for(int i=0;i<branchSize;i++){
				BaseBranchXmlType Branch = branchList.getBranch().get(i).getValue();
				if(Branch instanceof LineDStabXmlType) {
					LineDStabXmlType line=(LineDStabXmlType)Branch;
					if(line.getZ0()!=null || line.getY0ShuntFromSide()!=null || line.getY0ShuntToSide()!=null){
						BpaDynamicSequenceRecord bpaSeqRecord=new BpaDynamicSequenceRecord(parser);
						LOStr=bpaSeqRecord.setDynamicSequenceData(LOStr, line);
					}
					if(line.getLineMutualZeroZ().size()!=0){
						BpaDynamicSequenceRecord bpaSeqRecord=new BpaDynamicSequenceRecord(parser);
						LOStr=bpaSeqRecord.setDynamicMutualZeroData(LOStr, line);
					}
				}
				else if(Branch instanceof XfrDStabXmlType) {
					XfrDStabXmlType xfr=(XfrDStabXmlType)Branch;
					if(xfr.getZ0()!=null){
						BpaDynamicSequenceRecord bpaSeqRecord=new BpaDynamicSequenceRecord(parser);
						XOStr=bpaSeqRecord.setDynamicSequenceData(XOStr, xfr);
					}
				}
		    }
			bpaStr+=LOStr+".End of LO cards, start XO cards."+"\n";
			bpaStr+=XOStr+".End of XO cards, start XR cards."+"\n";
			bpaStr+=XRStr+".End of XR cards, start static load model data cards"+"\n";
			
			//Static load model data cards
			for(int i=0;i<loadCards.size();i++){
				String temp=loadCards.get(i);
				for(int j=i+1;j<loadCards.size();j++){
					if(loadCards.get(j).equals(temp))
						loadCards.remove(j);
				}
			}
			//输出
			String areaLoad="",zoneLoad="",busLoad="";
			for(int i=0;i<loadCards.size();i++){
				String thisLoad=StringUtil.replaceChineseChar(loadCards.get(i));
				if(!thisLoad.substring(17, 27).trim().equals(""))
					areaLoad+=loadCards.get(i)+"\n";
				else if(!thisLoad.substring(15, 17).trim().equals(""))
					zoneLoad+=loadCards.get(i)+"\n";
				else if(!thisLoad.substring(3, 15).trim().equals(""))
					busLoad+=loadCards.get(i)+"\n";
			}
			bpaStr+=areaLoad+zoneLoad+busLoad;
//			//一个分区内具有相同静态负荷模型的，对分区(Zone)填负荷特性卡
//			for(int i=0;i<loadCards.size();i++){
//				String temp=loadCards.get(i).substring(15);
//				StringBuffer New=new StringBuffer();
//				int m=0;
//				for(int j=i+1;j<loadCards.size();j++){
//					if(loadCards.get(j).substring(15).equals(temp)){
//						m++;
//						loadCards.remove(j);
//					}
//				}
//				if(m!=0){
//					New=New.append(loadCards.get(i).substring(0, 2)).append(StringUtil.getSpace(14)).append(temp);
//					loadCards.remove(i);
//					loadCards.add(i, New.toString());
//				}
//			}
//			//一个区域内具有相同静态负荷模型的，对区域(Area)填负荷特性卡
//			for(int i=0;i<loadCards.size();i++){
//				String temp=loadCards.get(i).substring(17);
//				StringBuffer New=new StringBuffer();
//				int m=0;
//				for(int j=i+1;j<loadCards.size();j++){
//					if(loadCards.get(j).substring(17).equals(temp)){
//						m++;
//						loadCards.remove(j);
//					}
//				}
//				if(m!=0){
//					New=New.append(loadCards.get(i).substring(0, 2)).append(StringUtil.getSpace(16)).append(temp);
//					loadCards.remove(i);
//					loadCards.add(i, New.toString());
//				}
//			}
//			//输出
//			for(int i=loadCards.size()-1;i>=0;i--){
//				bpaStr+=loadCards.get(i)+"\n";
//			}
			saveBPAFile(bpaFilePath);	
		} catch (Exception e) {
			e.printStackTrace();
			noError = false;
		}	
		return noError;
    }
    // save result to the targeted file
    private static void saveBPAFile(String filePath){
    	File file=new File(filePath);
    	FileOutputStream out=null;
		try {
			out = new FileOutputStream(file);
			out.write(bpaStr.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public String stdout(){
   		return bpaStr;
    }
    
}