package org.ieee.odm.adapter.pwd.impl;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.List;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.schema.AdjustmentModeEnumType;
import org.ieee.odm.schema.AngleAdjustmentXmlType;
import org.ieee.odm.schema.AngleLimitXmlType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LineBranchEnumType;
import org.ieee.odm.schema.LineBranchInfoXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.ObjectFactory;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.TapAdjustmentXmlType;
import org.ieee.odm.schema.TransformerInfoXmlType;
import org.ieee.odm.schema.VoltageUnitType;
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
		boolean closed=true, isXfmr=false, isPhXfmr=false;
		double r=0,x=0,b=0,g=0, // all per unit value on system base;
		       fBusShuntMW=0,fBusShuntMvar=0,tBusShuntMW=0,tBusShuntMvar=0, //shunt Mw and Mvar at two ends;
		       mvaRating1=9999,mvaRating2=9999,mvaRating3=9999,//mvar rating
		       lineTap=1.0, toTurnRatio=1.0;//tap ratio
		double phaseAngle=0.0;
		double xfrRegMin=0, xfrRegMax=0;
		double xfrMvaBase = 0.0, xfrFromSideNominalKV = 0.0, xfrToSideNominalKV=0.0;
		
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
			    
				// LineR:1, LineX:1, LineG:1, LineC:1, XFStep:1, XFTapMax:1, XFTapMin:1, LineTap:1
				
			    else if (nv.name.equals("LineR") || nv.name.equals("LineR:1"))
					r=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineX") || nv.name.equals("LineX:1"))
					x=Double.valueOf(nv.value);
			    else if (nv.name.equals("LineC") || nv.name.equals("LineC:1"))
					b=Double.valueOf(nv.value);
			    else if (nv.name.equals("LineG") || nv.name.equals("LineG:1"))
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
			    
			    else if (nv.name.equals("LineTap") || nv.name.equals("LineTap:1"))
					lineTap=Double.valueOf(nv.value);
				
			    else if (nv.name.equals("LinePhase"))
			    	phaseAngle=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFRegMin"))
			    	xfrRegMin=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFRegMax"))
			    	xfrRegMax=Double.valueOf(nv.value);
			    else if(nv.name.equals(typeToken))
			    	type=nv.value;
			    else if(nv.name.equals(idToken))
			    	branchId=nv.value;
				
			    else if (nv.name.equals("XFMVABase"))
			    	xfrMvaBase=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFNominalKV"))
			    	xfrFromSideNominalKV=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFNominalKV:1"))
			    	xfrToSideNominalKV=Double.valueOf(nv.value);
			}
			
		    fromBusId=parser.BusIdPreFix+fromBusNum;
		    toBusId=parser.BusIdPreFix+toBusNum;
		    
		    // create a branch record
		    BranchXmlType branch=null;
		    
		    //TODO temp solution to turn transmission line with different base voltage to a transformer branch
/*
DATA (BRANCH, [CustomString, CustomString:1,     CustomString:2, BusNum, BusNum:1, LineCircuit, LineXfmr, LineR,   LineX,   LineG,   LineC,   
               "Transformer" "BLISVILL_115_PS01" "PS01"          7765    7763      1            "Yes"     0.000023 0.001250 0.000000 0.000000 

LineAMVA, LineAMVA:1, LineAMVA:2, LineMonEle, LSName, LineXFType, LineStatus, LineTap, XFRegTargetType,           LinePhase, 
318.0     318.0       318.0       "YES"       "115"   "Phase"     "Closed"    1        "Middle of Reg Range"      -13.125    

XFAuto,   XFRegBus, XFRegMin,   XFRegMax,  XFTapMin, XFTapMax, XFStep, XFTableNum])            
"No"      0         -22.083725  -2.083725  -60.0     60.0      1.875   30 

    - LineXfmr = "Yes" xformer branch
    - LinePhase != 0, PShafting xformer 
 */
		    
		    /* Not needed, since InterPSS object model will handle the issue
		    if(parser.getAclfBus(fromBusId).getBaseVoltage().getValue()
		    		!=parser.getAclfBus(toBusId).getBaseVoltage().getValue())
		    	isXfmr=true;
	    	*/
		    
		    if(!isXfmr){
		    	branch = parser.createLineBranch(fromBusId, toBusId, circuitId);
		    	LineBranchInfoXmlType LineInfo=new LineBranchInfoXmlType();
		    	LineInfo.setType(type.equalsIgnoreCase("line")? LineBranchEnumType.OVERHEAD_LINE :
		    		(type.equalsIgnoreCase("breaker")? LineBranchEnumType.BREAKER : LineBranchEnumType.OTHER));
		    	((LineBranchXmlType) branch).setLineInfo(LineInfo);
		    }
		    else{
		    	//phase shifting transformer
		    	 branch=phaseAngle!=0?parser.createPSXfrBranch(fromBusId, toBusId, circuitId)
		    	//traditional transformer
		    			               :parser.createXfrBranch(fromBusId, toBusId, circuitId);;
		    	
		    	
		    }
		    
		    
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
				LineBranchXmlType line= (LineBranchXmlType) branch;
				if(g!=0||b!=0)line.setTotalShuntY(
						BaseDataSetter.createYValue(g, b, YUnitType.PU));
			}
			//branch is Transformer type
			else if (branch instanceof XfrBranchXmlType) {

				if (phaseAngle == 0) {// transformer
					XfrBranchXmlType xfr = (XfrBranchXmlType) branch;
					AclfDataSetter.createXformerData(xfr, r, x, ZUnitType.PU,
							lineTap, toTurnRatio, g, b, YUnitType.PU);

					BusXmlType fromBusRec = parser.getBus(fromBusId);
					BusXmlType toBusRec = parser.getBus(toBusId);
					if (fromBusRec != null && toBusRec != null) {
						AclfDataSetter.setXfrRatingData(xfr, fromBusRec
								.getBaseVoltage().getValue(), toBusRec
								.getBaseVoltage().getValue(), fromBusRec
								.getBaseVoltage().getUnit());
					} else {
						ODMLogger.getLogger().severe(
								"Error: fromBusRecord and/or toBusRecord cannot be found, fromId, toId: "
										+ fromBusId + ", " + toBusId);
					}
				} else {// Phase shifting transformer
					PSXfrBranchXmlType psXfr = (PSXfrBranchXmlType) branch;
					AclfDataSetter.createPhaseShiftXfrData(psXfr, r, x,
							ZUnitType.PU, lineTap, toTurnRatio, phaseAngle, 0,
							AngleUnitType.DEG, g, b, YUnitType.PU);

					// angle adjustment
					AngleAdjustmentXmlType angAdj = new AngleAdjustmentXmlType();
					psXfr.setAngleAdjustment(angAdj);
					angAdj.setAngleLimit(new AngleLimitXmlType());
					BaseDataSetter.setLimit(angAdj.getAngleLimit(), xfrRegMax,
							xfrRegMin);

					angAdj.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
					angAdj.setDesiredMeasuredOnFromSide(true);

					// xfr rating
					BusXmlType fromBusRec = parser.getBus(fromBusId);
					BusXmlType toBusRec = parser.getBus(toBusId);
					if (fromBusRec != null && toBusRec != null) {
						AclfDataSetter.setXfrRatingData(psXfr, fromBusRec
								.getBaseVoltage().getValue(), toBusRec
								.getBaseVoltage().getValue(), fromBusRec
								.getBaseVoltage().getUnit());
					} else {
						ODMLogger.getLogger().severe(
								"Error: fromBusRecord and/or toBusRecord cannot be found, fromId, toId: "
										+ fromBusId + ", " + toBusId);
					}
				}
				
				if (xfrMvaBase != 0.0) {
					XfrBranchXmlType xfr = (XfrBranchXmlType) branch;
					xfr.setXfrInfo(odmObjFactory.createTransformerInfoXmlType());
					TransformerInfoXmlType xfrInfo = xfr.getXfrInfo();
					xfrInfo.setDataOnSystemBase(false);
					xfrInfo.setRatedPower(BaseDataSetter.createApparentPower(xfrMvaBase, ApparentPowerUnitType.MVA));
					xfrInfo.setFromRatedVoltage(BaseDataSetter.createVoltageValue(xfrFromSideNominalKV, VoltageUnitType.KV));
					xfrInfo.setToRatedVoltage(BaseDataSetter.createVoltageValue(xfrToSideNominalKV, VoltageUnitType.KV));
				}
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
