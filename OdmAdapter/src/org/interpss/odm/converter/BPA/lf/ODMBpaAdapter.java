package org.interpss.odm.converter.BPA.lf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;


public class ODMBpaAdapter {
	 private static LoadflowNetXmlType netXml=null;
	 private static String bpaStr="";
	 private static AclfModelParser _parser=null;
     public ODMBpaAdapter(AclfModelParser parser){
    	 _parser=parser;
    	 netXml=_parser.getAclfNet();
     }
     public ODMBpaAdapter(String xmlFile){
    	 File file=new File(xmlFile);
 		 _parser= ODMObjectFactory.createDStabModelParser();
 		 _parser.parse(file);
         netXml=_parser.getAclfNet();
     }
     public static boolean ODM2BPA(String caseId,String projectName, String bpaFilePath){
        boolean noError=true;
        double baseMVA=netXml.getBasePower().getValue();
    	 try {
    		 //process head info
    		 BpaAclfNetworkRecord netRecord=new BpaAclfNetworkRecord(_parser);
			 bpaStr=netRecord.setNetHeadInfo(caseId, projectName, bpaStr);
    		 //process area data and interarea power exchange data,if any
    		 if(netXml.getAreaList()!=null){
    			BpaAreaPowerExchgRecord areaExchg=new BpaAreaPowerExchgRecord(_parser);
    			bpaStr=areaExchg.setAreaRecord(bpaStr);
    			bpaStr=areaExchg.setInterAreaExchangeRecord(bpaStr);
    		 }
    		 bpaStr+=".End of head info data, start bus data."+"\n";
        	 //process bus data
    		 
    		   BpaAclfBusRecord bpaBusRecord=new BpaAclfBusRecord(_parser);
				for (JAXBElement<? extends BusXmlType> bus : netXml.getBusList().getBus()) {
					LoadflowBusXmlType busRec = (LoadflowBusXmlType) bus.getValue();
					 
					bpaStr=bpaBusRecord.setAclfBusData(bpaStr, busRec);
				}
				
				bpaStr+=".End of bus data, start branch data."+"\n";
		      //process aclfBranch data
				
				 BpaAclfBranchRecord bpaBranchRecord=new BpaAclfBranchRecord(_parser);
				for (JAXBElement<? extends BaseBranchXmlType> b : netXml.getBranchList().getBranch()) {
					BaseBranchXmlType xmlBranch = b.getValue();
					bpaStr=bpaBranchRecord.setAclfBranchData(bpaStr, xmlBranch);
                   
                   //TODO work on 3-wing transformer
					/*
					if (xmlBranch instanceof PSXfr3WBranchXmlType || xmlBranch instanceof Xfr3WBranchXmlType)
						branch = CoreObjectFactory.createAclf3WXformer();
						*/
				}
				
			// add "(END)" to the end of the data
				bpaStr+="(END)";
			// save result to the targeted file	
			saveBPAFile(bpaFilePath);	
			} catch (Exception e) {
				e.printStackTrace();
				noError = false;
			}
			
			return noError;
	
     }
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
