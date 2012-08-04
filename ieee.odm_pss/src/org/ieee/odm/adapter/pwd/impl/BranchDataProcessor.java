package org.ieee.odm.adapter.pwd.impl;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.LineBranchEnumType;
import org.ieee.odm.schema.LineBranchInfoXmlType;
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
  * @version 0.2  
  * @author Tony Huang
  * 
  * ====revision history===
  * 
  * 
  */
public class BranchDataProcessor extends BaseDataProcessor  {
	public BranchDataProcessor(List<PowerWorldAdapter.NVPair> nvPairs, AclfModelParser parser) {
		super(nvPairs, parser);
	}
	
	public void processBranchData(String branchDataStr){
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
		String type="";
		String branchId="";
		boolean closed=true, isXfmr=false;
		double r=0,x=0,b=0,g=0, // all per unit value on system base;
		       fBusShuntMW=0,fBusShuntMvar=0,tBusShuntMW=0,tBusShuntMvar=0, //shunt Mw and Mvar at two ends;
		       mvaRating1=9999,mvaRating2=9999,mvaRating3=9999,
		       lineTap=1.0, toTurnRatio=1.0;//mvaRatingB,mvaRatingC,
		/*
		 * ONLY for specific application
		 */
		String typeToken="CustomString"; //type
		String idToken="CustomString:1"; //branch Id
		
		//System.out.println("processing branch#"+branchDataStr);
		PWDHelper.parseDataFields(branchDataStr, inputNvPairs);
		try{
			for(PowerWorldAdapter.NVPair nv:inputNvPairs){
				//TODO branch id, NE-ISO use "customString:1" as the corresponding argument;	
				if (nv.name.equals("BusNum"))
					fromBusNum=Long.valueOf(nv.value); //mandatory field
				
				else if (nv.name.equals("BusNum:1"))
					toBusNum=Long.valueOf(nv.value); //mandatory field
				
				else if (nv.name.equals("LineCircuit"))
					circuitId=nv.value;
				
				else if (nv.name.equals("LineXfmr"))
					isXfmr=nv.value.equalsIgnoreCase("YES")?true:false;
				//both LineXfmr or BranchDeviceType could be used to define branch type
				else if (nv.name.equals("BranchDeviceType"))
					isXfmr=nv.value.equalsIgnoreCase("Transformer")?true:false;
				
				else if (nv.name.equals("LineStatus"))
					closed=nv.value.equalsIgnoreCase("Closed")?true:false;
			    
			    else if (nv.name.equals("LineR"))
					r=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineX"))
					x=Double.valueOf(nv.value);
			    else if (nv.name.equals("LineC"))
					b=Double.valueOf(nv.value);
			    else if (nv.name.equals("LineG"))
					g=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineAMVA"))
					mvaRating1=Double.valueOf(nv.value); // line limit rating
			    
			    else if (nv.name.equals("LineAMVA:1")||nv.name.equals("LineBMVA"))
					mvaRating2=Double.valueOf(nv.value);
			    else if (nv.name.equals("LineAMVA:2")||nv.name.equals("LineCMVA"))
					mvaRating3=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineShuntMW"))
					fBusShuntMW=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineShuntMW:1"))
					tBusShuntMW=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineShuntMVR"))
					fBusShuntMvar=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineShuntMVR:1"))
					tBusShuntMvar=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineTap"))
					lineTap=Double.valueOf(nv.value); 
				
			    else if(nv.name.equals(typeToken))
			    	type=nv.value;
			    else if(nv.name.equals(idToken))
			    	branchId=nv.value;
			    
			}
			
		    fromBusId=parser.BusIdPreFix+fromBusNum;
		    toBusId=parser.BusIdPreFix+toBusNum;
		    
		    // create a branch record
		    BranchXmlType branch=null;
		    
		    //TODO temp solution to turn transmission line with different base voltage to a transformer branch
		    
		    if(parser.getAclfBus(fromBusId).getBaseVoltage().getValue()
		    		!=parser.getAclfBus(toBusId).getBaseVoltage().getValue())
		    	isXfmr=true;
	    	
		    if(!isXfmr){
		    	branch = parser.createLineBranch(fromBusId, toBusId, circuitId);
		    	//TODO add branch type, as line or breaker
		    	LineBranchInfoXmlType LineInfo=new LineBranchInfoXmlType();
		    	LineInfo.setType(type.equalsIgnoreCase("line")?LineBranchEnumType.OVERHEAD_LINE:LineBranchEnumType.BREAKER);
		    	((LineBranchXmlType) branch).setLineInfo(LineInfo);
		    }
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
						BaseDataSetter.createTurnRatioPU(toTurnRatio));
				//what is the difference between transformer tap and turn ratio;
				
				
				
			}
			
			//set rating limit
			branch.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
			
			AclfDataSetter.setBranchRatingLimitData(branch.getRatingLimit(),
					mvaRating1, mvaRating2, mvaRating3, ApparentPowerUnitType.MVA);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void processXFormerData(String xfomerDataStr){
		/*
		DATA (TRANSFORMER, [BusNum,BusNum:1,LineCircuit,LineXFType,XFAuto,XFRegMin,XFRegMax,XFTapMin,
		                    XFTapMax,XFStep,XFTableNum,XFRegBus])
		*/
		long fromBusNum=-1,toBusNum=-1;
		String fromBusId, toBusId,circuitId="1";
		int tableNum=0;
		double xTapMin=0,xTapMax=0,xTapStep=0,xRegMin=0,xRegMax=0;
		double xfmrBaseMva=0;
		PWDHelper.parseDataFields(xfomerDataStr, inputNvPairs);
		try{
			for(PowerWorldAdapter.NVPair nv:inputNvPairs){
				if (nv.name.equals("BusNum"))
					fromBusNum=Long.valueOf(nv.value); //mandatory field
				
				else if (nv.name.equals("BusNum:1"))
					toBusNum=Long.valueOf(nv.value); 	//mandatory field
				
				else if (nv.name.equals("LineCircuit"))
					circuitId=nv.value;
				
				else if (nv.name.equals("XFRegMin"))
					xRegMin=Double.valueOf(nv.value);
				else if (nv.name.equals("XFRegMax"))
					xRegMax=Double.valueOf(nv.value);
				else if (nv.name.equals("XFTapMin"))
					xTapMin=Double.valueOf(nv.value);
				else if (nv.name.equals("XFTapMax"))
					xTapMax=Double.valueOf(nv.value);
				else if (nv.name.equals("XFTapStep"))
					xTapStep=Double.valueOf(nv.value);
				
				else if (nv.name.equals("XFMVABase"))
					xfmrBaseMva=Double.valueOf(nv.value);
				
				else if (nv.name.equals("XFTableNum"))
					tableNum=Integer.valueOf(nv.value);
			}
			/*
			*/
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
	
	public void process3WXFomerData(String triWXformerDataStr){
		/*
		 * the 3-winding transformers are treated as 3 2-winding transformers
		 *  with an additional star bus added to the network;
		 */
		
		
	}
}
