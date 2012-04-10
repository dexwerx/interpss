package org.ieee.odm.adapter.pwd.impl;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.TapAdjustmentXmlType;
import org.ieee.odm.schema.TransformerInfoXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;
 /**
  * PowerWorld-TO-ODM Adapter based on power world v16 data definition
  * 
  * @version 0.1  04/03/2012
  * @author Tony Huang
  * 
  */
public class BranchDataProcessor {
	public static void processBranchData(String branchDataStr, List<String> argumentFileds, AclfModelParser parser){
		/*
		 * DATA (BRANCH, [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG,LineAMVA,LineBMVA,
              LineCMVA,LineShuntMW,LineShuntMW:1,LineShuntMVR,LineShuntMVR:1,LineTap,
              LinePhase,SeriesCapStatus])
		 * 
		 * BusNum-># of fromBus
		 * BusNum:1-># of toBus
		 * LineC->shuntB(Per unit susceptance (B) of branch on the system base), 50% at each end; 
		 * LineG->Per unit conductance (G) of branch on the system base
		 * LineXfmr: the flag indicating whether the branch is a Line or transformer;
		 */
		//TODO exact meanings of LineAMVA,LineBMVA,LineCMVA
		
		long fromBusNum=-1,toBusNum=-1;
		String fromBusId, toBusId,circuitId="1";
		boolean closed=true, isXfmr=false;
		double r=0,x=0,b=0,g=0, // all per unit value on system base;
		       fBusShuntMW=0,fBusShuntMvar=0,tBusShuntMW=0,tBusShuntMvar=0, //shunt Mw and Mvar at two ends;
		       mvaRatingA=9999,mvaRatingB=9999,mvaRatingC=9999,
		       lineTap=1.0;//mvaRatingB,mvaRatingC,
		//System.out.println("processing branch#"+branchDataStr);
		String[] branchData=PWDHelper.getDataFields(branchDataStr, argumentFileds);
		int i=-1;
		try{
		//TODO branch id, NE-ISO use "customString:1" as the corresponding argument;	
		i=argumentFileds.indexOf("BusNum") ;
		fromBusNum=Long.valueOf(branchData[i]); //mandatory field
		
		i=argumentFileds.indexOf("BusNum:1") ;
		toBusNum=Long.valueOf(branchData[i]); //mandatory field
		
		i=argumentFileds.indexOf("LineCircuit"); if(i!=-1)circuitId=branchData[i];
		
		i=argumentFileds.indexOf("LineXfmr"); if(i!=-1)isXfmr=branchData[i].equalsIgnoreCase("YES")?true:false;
		//both LineXfmr or BranchDeviceType could be used to define branch type
		i=argumentFileds.indexOf("BranchDeviceType");if(i!=-1)isXfmr=branchData[i].equalsIgnoreCase("Transformer")?true:false;
		
		i=argumentFileds.indexOf("LineStatus"); 
	    if(i!=-1)closed=branchData[i].equalsIgnoreCase("Closed")?true:false;
	    
	    i=argumentFileds.indexOf("LineR"); if(i!=-1)r=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineX"); if(i!=-1)x=Double.valueOf(branchData[i]);
	    i=argumentFileds.indexOf("LineC"); if(i!=-1)b=Double.valueOf(branchData[i]);
	    i=argumentFileds.indexOf("LineG"); if(i!=-1)g=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineAMVA"); 
	    if(i!=-1)mvaRatingA=Double.valueOf(branchData[i]); // line limit rating
	    
	    i=argumentFileds.indexOf("LineBMVA"); //same as LineAMVA:1?
	    if(i!=-1)mvaRatingB=Double.valueOf(branchData[i]);
	    i=argumentFileds.indexOf("LineCMVA"); //same as LineAMVA:2?
	    if(i!=-1)mvaRatingC=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineShuntMW"); 
	    if(i!=-1)fBusShuntMW=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineShuntMW:1"); 
	    if(i!=-1)tBusShuntMW=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineShuntMVR"); 
	    if(i!=-1)fBusShuntMvar=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineShuntMVR:1"); 
	    if(i!=-1)tBusShuntMvar=Double.valueOf(branchData[i]);
	    
	    i=argumentFileds.indexOf("LineTap"); 
	    if(i!=-1)lineTap=Double.valueOf(branchData[i]); 
	    
	    fromBusId=parser.BusIdPreFix+fromBusNum;
	    toBusId=parser.BusIdPreFix+toBusNum;
	    
	    // create a branch record
	    BranchXmlType branch=null;
	    
	    //parser.getAclfBus(fromBusId).getBaseVoltage().getValue()!=parser.getAclfBus(toBusId).getBaseVoltage().getValue()
    	
	    if(!isXfmr)branch = parser.createLineBranch(fromBusId, toBusId, circuitId);
	    else branch=parser.createXfrBranch(fromBusId, toBusId, circuitId);
	    
		/*
		 * common setting for branch type
		 */
		branch.setOffLine(!closed);
		branch.setZ(BaseDataSetter.createZValue(r, x, ZUnitType.PU));
		//processing lint shunt at from bus 
		if(fBusShuntMW!=0||fBusShuntMvar!=0){
			LoadflowBusXmlType fromBus=parser.getAclfBus(fromBusId);
			AclfDataSetter.addBusShuntY(fromBus, fBusShuntMW, fBusShuntMvar, YUnitType.MVAR);
		}
		//processing lint shunt at to bus 
		if(tBusShuntMW!=0||tBusShuntMvar!=0){
			LoadflowBusXmlType toBus=parser.getAclfBus(toBusId);
			AclfDataSetter.addBusShuntY(toBus, tBusShuntMW, tBusShuntMvar, YUnitType.MVAR);
		}
		
		//branch is Line type
		if(branch instanceof LineBranchXmlType){
			if(g!=0||b!=0)((LineBranchXmlType) branch).setTotalShuntY(
					BaseDataSetter.createYValue(g, b, YUnitType.PU));
		}
		//branch is Transformer type
		else if(branch instanceof XfrBranchXmlType){
			if(g!=0||b!=0)((XfrBranchXmlType) branch).setMagnitizingY(
					BaseDataSetter.createYValue(g, b, YUnitType.PU));
			((XfrBranchXmlType) branch).setFromTurnRatio(
					BaseDataSetter.createTurnRatioPU(lineTap));
			//TODO define toTurnRatio, set 1.0 by default
			((XfrBranchXmlType) branch).setToTurnRatio(
					BaseDataSetter.createTurnRatioPU(1.0));
			//what is the difference between transformer tap and turn ratio;
			
			//TODO NE-ISO use Branch type to define transformer
			
		}
		
		//set rating limit
		branch.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
		
		AclfDataSetter.setBranchRatingLimitData(branch.getRatingLimit(),
				mvaRatingA, mvaRatingB, mvaRatingC, ApparentPowerUnitType.MVA);
	    
		}catch(Exception e){
			e.printStackTrace();
		}
		
	    
	    
		
		
	}
	
	public static void processXFormerData(String xfomerDataStr, List<String> argumentFileds, AclfModelParser parser){
		/*
		DATA (TRANSFORMER, [BusNum,BusNum:1,LineCircuit,LineXFType,XFAuto,XFRegMin,XFRegMax,XFTapMin,
		                    XFTapMax,XFStep,XFTableNum,XFRegBus])
		*/
		long fromBusNum=-1,toBusNum=-1;
		String fromBusId, toBusId,circuitId="1";
		int tableNum=0;
		double xTapMin=0,xTapMax=0,xTapStep=0,xRegMin=0,xRegMax=0;
		double xfmrBaseMva=0;
		String[] xfmrData=PWDHelper.getDataFields(xfomerDataStr, argumentFileds);
		int i=-1;
		try{
		i=argumentFileds.indexOf("BusNum") ;
		fromBusNum=Long.valueOf(xfmrData[i]); //mandatory field
		
		i=argumentFileds.indexOf("BusNum:1") ;
		toBusNum=Long.valueOf(xfmrData[i]); //mandatory field
		
		i=argumentFileds.indexOf("LineCircuit"); if(i!=-1)circuitId=xfmrData[i];
		
		i=argumentFileds.indexOf("XFRegMin"); if(i!=-1)xRegMin=Double.valueOf(xfmrData[i]);
		i=argumentFileds.indexOf("XFRegMax"); if(i!=-1)xRegMax=Double.valueOf(xfmrData[i]);
		i=argumentFileds.indexOf("XFTapMin"); if(i!=-1)xTapMin=Double.valueOf(xfmrData[i]);
		i=argumentFileds.indexOf("XFTapMax"); if(i!=-1)xTapMax=Double.valueOf(xfmrData[i]);
		i=argumentFileds.indexOf("XFTapStep"); if(i!=-1)xTapStep=Double.valueOf(xfmrData[i]);
		
		i=argumentFileds.indexOf("XFMVABase"); if(i!=-1)xfmrBaseMva=Double.valueOf(xfmrData[i]);
		
		i=argumentFileds.indexOf("XFTableNum"); if(i!=-1)tableNum=Integer.valueOf(xfmrData[i]);
		
		fromBusId=parser.BusIdPreFix+fromBusNum;
		toBusId=parser.BusIdPreFix+toBusNum;
		
		XfrBranchXmlType xfr=parser.getXfrBranch(fromBusId, toBusId, circuitId);
		//TODO set type and regulation info;
		TransformerInfoXmlType xfmrInfo=new TransformerInfoXmlType();
		xfr.setXfrInfo(xfmrInfo);
		xfmrInfo.setZTableNumber(tableNum);
		if(xfmrBaseMva!=0)
		xfmrInfo.setRatedPower(BaseDataSetter.createApparentPower(xfmrBaseMva, ApparentPowerUnitType.MVA));
		
		
		TapAdjustmentXmlType tapAdj=new TapAdjustmentXmlType();
		xfr.setTapAdjustment(tapAdj);
		if(xTapMax!=0)tapAdj.setTapLimit(BaseDataSetter.createTapLimit(xTapMax, xTapMin));
		if(xTapStep!=0)tapAdj.setTapAdjStepSize(xTapStep);
		//TODO what is regulated value for?
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void process3WXFomerData(String triWXformerDataStr, List<String> argumentFileds, LoadflowNetXmlType baseCaseNet){
		/*
		 * the 3-winding transformers are treated as 3 2-winding transformers
		 *  with an additional star bus added to the network;
		 */
		
		
	}
}
