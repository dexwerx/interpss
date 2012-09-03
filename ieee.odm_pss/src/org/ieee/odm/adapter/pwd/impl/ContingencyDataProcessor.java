package org.ieee.odm.adapter.pwd.impl;

import java.util.List;

import org.ieee.odm.adapter.pwd.PWDAdapterForContingency.ContingencyType;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter.NVPair;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.model.modify.NetModificationHelper;
import org.ieee.odm.schema.BranchChangeRecSetXmlType;
import org.ieee.odm.schema.BranchChangeRecXmlType;
import org.ieee.odm.schema.NetModificationXmlType;

public class ContingencyDataProcessor extends BaseDataProcessor{
	
	private NetModificationXmlType netModList=null;
	private NetModificationHelper helper=null;
	private BranchChangeRecSetXmlType branchTypeCtg=null;
	boolean skipCtg=false;
	boolean isCTGSubDataSection=false;
	public ContingencyDataProcessor(List<PowerWorldAdapter.NVPair> nvPairs,AclfModelParser parser) {
		super(nvPairs, parser);
		
		// initialization
		
		// create empty base network 
		parser.getAclfNet();
		
	    helper = new NetModificationHelper(parser);		
		// create a container for bus/branch-change type contingency records
		netModList = helper.createNetModificationList();
	}
	
	public void processContingencyData(String ctgStr){
      
		
		
		/*
		 * DATA (CONTINGENCY,[CTGLabel,CTGSkip,CTGProc,CTGSolved,LoadMW,CustomString]) 
		   { 
		   "005A" "NO " "NO" "NO" 0.0 "88005A-1-2/8804A/88006A-1/1780" 
		    <SUBDATA CTGElement>
	       "BRANCH 7514 7512 1 OPEN" "" CHECK //WOODMONT CB 29Y-1T-2
		 * "BRANCH 7750 7752 1 OPEN" "" CHECK //DEVON_T CB 16P-1T-2
		 * "BRANCH 7750 7756 1 OPEN" "" CHECK //DEVON_T CB 16P-4T-2
		 * "BRANCH 7536 7535 1 OPEN" "" CHECK //MILVON CB 6850A
		 * "BRANCH 7522 7524 1 OPEN" "" CHECK //WOODMONT CB 6840A 
		 * </SUBDATA>
		 */
		
		// First need to check whether or not the data input is CTGElement Data
		if(ctgStr.trim().startsWith("<SUBDATA")){
			isCTGSubDataSection=true;
			return;
		}
		else if(ctgStr.trim().startsWith("</SUBDATA>")){
			isCTGSubDataSection=false;
			return;
		}
		//
		if(!isCTGSubDataSection){
			
			PWDHelper.parseDataFields(ctgStr, inputNvPairs);
			//process basic contingency definition data, including Table, ModelCriteria
			processBasicCTGData();
		}
		else processCTGElementData(ctgStr);
		
	}
	private void processBasicCTGData() {
		String ctgId="";
		String ctgInfo="";
		
		// create a branch change set object to represent a contingency
		for(NVPair nv:inputNvPairs){
			if(nv.name.equals("CTGSkip")){
				skipCtg=nv.value.trim().equalsIgnoreCase("NO")?false:true;
			}
			else if(nv.name.equals("CTGLabel")) ctgId=nv.value;
			else if(nv.name.equals("CustomString")) ctgInfo=nv.value; //Only for this project
		}
		
		if(!skipCtg){
		   branchTypeCtg = helper.createBranchChangeRecSetXmlType(netModList);	
	       branchTypeCtg.setId(ctgId);
	       branchTypeCtg.setDesc(ctgInfo);
		}
		
	}

	private void processCTGElementData(String str) {
		String action="";
		String comment="";
		String id="";
		String status="";
		//TODO process the following CTGElement Data if skipCtg=false
		if(!skipCtg){
	    //first process the comment
		int cmtIndex=str.indexOf("//");
		
		if(cmtIndex!=-1){
			comment=str.substring(cmtIndex+1);
			str=str.substring(0, cmtIndex);
		}
		
		//parse CTGElement Data
		/*
		 * return format[Action,ModelCriteria,status], since the comment has been subtracted;
		 */
		String[] ctgElement=PWDHelper.parseDataFields(str);
		action=ctgElement[0];
		
		//get Branch info, format[branchId, status]
		String[] braInfo=getNetModelChangeInfo(action,ContingencyType.BRANCH);
		
		// for each contingency, one to many branch change could be defined
		BranchChangeRecXmlType branchChange = helper.createBranchChangeRecXmlType(branchTypeCtg);
		branchChange.setBranchId(braInfo[0]);
		branchChange.setOffLine(braInfo[1].equalsIgnoreCase("OPEN"));
		branchChange.setFromBusId(braInfo[2]);
		branchChange.setToBusId(braInfo[3]);
		branchChange.setCircuitId(braInfo[4]);
		
		//comment
		if(comment.length()>1)branchChange.setDesc(comment);
		
		}

		
	}

	private void isCTGElementData(String str, boolean isCTGSubDataSection) {
		if(str.trim().startsWith("<SUBDATA")){
			isCTGSubDataSection=true;
			return;
		}
		else if(str.trim().startsWith("</SUBDATA>")){
			isCTGSubDataSection=false;
			return;
		}
	}
	
	
	private String[] getNetModelChangeInfo(String actionStr,ContingencyType type) {
		String[] info=null;
		if(actionStr!=null&&!actionStr.trim().isEmpty()){
		   if(type==ContingencyType.BRANCH){
			   
			   String[] temp=actionStr.split(" ");
			   if(!temp[0].equals("BRANCH")){
				   ODMLogger.getLogger().severe("Input data is not BRANCH type contingency!");
			       return null;
			   }
			   long fromBusNum=Long.valueOf(temp[1]);
			   long toBusNum=Long.valueOf(temp[2]);
			   String cirId=temp[3];
			   String fromId=AclfModelParser.BusIdPreFix+fromBusNum;
			   String toId=AclfModelParser.BusIdPreFix+toBusNum;
			   String branchId=ModelStringUtil.formBranchId(fromId, toId,cirId );
			   String status=temp[4];
			   
			   info=new String[5];
			   info[0]=branchId;
			   info[1]=status;
			   info[2]=fromId;
			   info[3]=toId;
			   info[4]=cirId;
		   } else
			try {
				throw new Exception("The Contingency Type #"+type+" is NOT supported yet!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return info;
		
	}
	
	



}
