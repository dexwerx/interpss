package org.ieee.odm.adapter.pwd.impl;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter.NVPair;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfDataSetter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AdjustmentModeEnumType;
import org.ieee.odm.schema.AngleAdjustmentXmlType;
import org.ieee.odm.schema.AngleUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.FactorUnitType;
import org.ieee.odm.schema.IDRefRecordXmlType;
import org.ieee.odm.schema.LimitXmlType;
import org.ieee.odm.schema.LineBranchEnumType;
import org.ieee.odm.schema.LineBranchInfoXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.MvarFlowAdjustmentDataXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.TapAdjustBusLocationEnumType;
import org.ieee.odm.schema.TapAdjustmentEnumType;
import org.ieee.odm.schema.TapAdjustmentXmlType;
import org.ieee.odm.schema.TransformerInfoXmlType;
import org.ieee.odm.schema.VoltageAdjustmentDataXmlType;
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
  * 09/08/2012 add Phase Xfr 
  * 
  */
public class BranchDataProcessor extends BaseDataProcessor  {
	private enum XfrCtrlTargetType{Midddle_Of_Range,MaxMin};
	private enum XfrType{Fixed, LTC, Mvar,Phase};
	
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
		       mvaRating1=9999,mvaRating2=9999,mvaRating3=9999;//mvar rating

		/*
		 * ONLY for specific application
		 */
		String typeToken="CustomString"; //type
		String idToken="CustomString:1"; //branch Id
		
		//System.out.println("processing branch#"+branchDataStr);
		PWDHelper.parseDataFields(branchDataStr, inputNvPairs);
		
		for(PowerWorldAdapter.NVPair nv:inputNvPairs){
			if (nv.name.equals("LineXfmr"))
				isXfmr=nv.value.equalsIgnoreCase("YES")?true:false;
			//both LineXfmr or BranchDeviceType could be used to define branch type
			else if (nv.name.equals("BranchDeviceType"))
				isXfmr=nv.value.equalsIgnoreCase("Transformer")?true:false;
		}
		if(isXfmr==true){
			process2WXfrData(inputNvPairs);
		}
        else {
			try {
				for (PowerWorldAdapter.NVPair nv : inputNvPairs) {
					// TODO branch id, NE-ISO use "customString:1" as the
					// corresponding argument;
					if (nv.name.equals("BusNum"))
						fromBusNum = Long.valueOf(nv.value); // mandatory field

					else if (nv.name.equals("BusNum:1"))
						toBusNum = Long.valueOf(nv.value); // mandatory field

					else if (nv.name.equals("LineCircuit"))
						circuitId = nv.value;

					else if (nv.name.equals("LineStatus"))
						closed = nv.value.equalsIgnoreCase("Closed") ? true
								: false;

					// LineR:1, LineX:1, LineG:1, LineC:1, 
					// TODO The suffix of ¡°£º1¡± is used for Transformer
					// definition,means those data values are based on
					// transformer MVA base
					else if (nv.name.equals("LineR")
							|| nv.name.equals("LineR:1"))
						r = Double.valueOf(nv.value);

					else if (nv.name.equals("LineX")
							|| nv.name.equals("LineX:1"))
						x = Double.valueOf(nv.value);
					else if (nv.name.equals("LineC")
							|| nv.name.equals("LineC:1"))
						b = Double.valueOf(nv.value);
					else if (nv.name.equals("LineG")
							|| nv.name.equals("LineG:1"))
						g = Double.valueOf(nv.value);
					else if (nv.name.equals("LineAMVA"))
						mvaRating1 = Double.valueOf(nv.value); // line limit
																// rating

					else if (nv.name.equals("LineAMVA:1")
							|| nv.name.equals("LineBMVA"))
						mvaRating2 = Double.valueOf(nv.value);
					else if (nv.name.equals("LineAMVA:2")
							|| nv.name.equals("LineCMVA"))
						mvaRating3 = Double.valueOf(nv.value);

					else if (nv.name.equals("LineShuntMW"))
						fBusShuntMW = Double.valueOf(nv.value);

					else if (nv.name.equals("LineShuntMW:1"))
						tBusShuntMW = Double.valueOf(nv.value);

					else if (nv.name.equals("LineShuntMVR"))
						fBusShuntMvar = Double.valueOf(nv.value);

					else if (nv.name.equals("LineShuntMVR:1"))
						tBusShuntMvar = Double.valueOf(nv.value);

					else if (nv.name.equals(typeToken))
						type = nv.value;
					else if (nv.name.equals(idToken))
						branchId = nv.value;
				}// END OF FOR INTERATION

				fromBusId = parser.BusIdPreFix + fromBusNum;
				toBusId = parser.BusIdPreFix + toBusNum;

				// create a branch record
				BranchXmlType branch = parser.createLineBranch(fromBusId,
						toBusId, circuitId);

				LineBranchInfoXmlType LineInfo = new LineBranchInfoXmlType();
				LineInfo.setType(type.equalsIgnoreCase("line") ? LineBranchEnumType.OVERHEAD_LINE
						: (type.equalsIgnoreCase("breaker") ? LineBranchEnumType.BREAKER
								: LineBranchEnumType.OTHER));
				((LineBranchXmlType) branch).setLineInfo(LineInfo);

				/*
				 * common setting for branch type
				 */

				branch.setOffLine(!closed);
				branch.setZ(BaseDataSetter.createZValue(r, x, ZUnitType.PU));
				// processing lint shunt at from bus
				if (fBusShuntMW != 0 || fBusShuntMvar != 0) {
					LoadflowBusXmlType fromBus = parser.getAclfBus(fromBusId);
					AclfDataSetter.addBusShuntY(fromBus, fBusShuntMW,
							fBusShuntMvar, YUnitType.MVAR);
				}
				// processing lint shunt at to bus
				if (tBusShuntMW != 0 || tBusShuntMvar != 0) {
					LoadflowBusXmlType toBus = parser.getAclfBus(toBusId);
					AclfDataSetter.addBusShuntY(toBus, tBusShuntMW,
							tBusShuntMvar, YUnitType.MVAR);
				}

				// branch is Line type

				LineBranchXmlType line = (LineBranchXmlType) branch;
				if (g != 0 || b != 0)
					line.setTotalShuntY(BaseDataSetter.createYValue(g, b,
							YUnitType.PU));

				// set rating limit
				branch.setRatingLimit(odmObjFactory
						.createBranchRatingLimitXmlType());

				AclfDataSetter.setBranchRatingLimitData(
						branch.getRatingLimit(), mvaRating1, mvaRating2,
						mvaRating3, ApparentPowerUnitType.MVA);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}// END OF PROCESSING BRANCH
	}
    /**
     * It assumed that the basic loadflow data,such as R,X,TapRatio,etc., 
     * for Transformer has been processed before this proceesing
     * 
     */
	public void processXFormerControlData(String xfomerDataStr) {
	
		/*
		 * DATA (TRANSFORMER,
		 * [BusNum,BusNum:1,LineCircuit,LineXFType,XFAuto,XFRegMin
		 * ,XFRegMax,XFTapMin, XFTapMax,XFStep,XFTableNum,XFRegBus
		 * XFFixedTap,XFFixedTap:1,XFNominalKV,XFNominalKV:1,XFMVABase])
		 */
		long fromBusNum = -1, toBusNum = -1;
		String fromBusId, toBusId, circuitId = "1";
		int tableNum = 0;
		double xfrTapMin = 0, xfrTapMax = 0, xfrTapStep = 0, xfrRegMin = 0, xfrRegMax = 0;
		double fromFixedTap=1.0, toFixedTap=1.0;// original tap position used to define xfr Z and turn ratio
		double xfrFromSideNominalKV=0.0,xfrToSideNominalKV=0.0;
		double xfmrBaseMva = 0;
		boolean isXFAutoControl=false;
		long regBusNum=-1;
		String regBusId="";
		XfrCtrlTargetType regTargetType=null;
		XfrType xfrType=null;
		
		PWDHelper.parseDataFields(xfomerDataStr, inputNvPairs);
		try {
			for (PowerWorldAdapter.NVPair nv : inputNvPairs) {
				if (nv.name.equals("BusNum"))
					fromBusNum = Long.valueOf(nv.value); // mandatory field

				else if (nv.name.equals("BusNum:1"))
					toBusNum = Long.valueOf(nv.value); // mandatory field

				else if (nv.name.equals("LineCircuit"))
					circuitId = nv.value;

				else if (nv.name.equals("XFRegMin"))
					xfrRegMin = Double.valueOf(nv.value);
				else if (nv.name.equals("XFRegMax"))
					xfrRegMax = Double.valueOf(nv.value);
				else if (nv.name.equals("XFTapMin"))
					xfrTapMin = Double.valueOf(nv.value);
				else if (nv.name.equals("XFTapMax"))
					xfrTapMax = Double.valueOf(nv.value);
				else if (nv.name.equals("XFStep"))
					xfrTapStep = Double.valueOf(nv.value);
				else if (nv.name.equals("XFMVABase"))
					xfmrBaseMva = Double.valueOf(nv.value);
				else if (nv.name.equals("XFRegBus"))
					regBusNum=Long.valueOf(nv.value); 
				else if (nv.name.equals("XFTableNum"))
					tableNum = Integer.valueOf(nv.value);
			    else if (nv.name.equals("XFRegTargetType"))
			    	regTargetType=nv.value.startsWith("Middle")?XfrCtrlTargetType.Midddle_Of_Range
			    			       :XfrCtrlTargetType.MaxMin;
			    else if (nv.name.equals("XFAuto"))
			    	isXFAutoControl=nv.value.trim().equalsIgnoreCase("No")?false:true;
			    	
			    else if (nv.name.equals("XFRegBus"))
					regBusNum=Long.valueOf(nv.value); 
			    else if(nv.name.equals("LineXFType"))
			    	xfrType=nv.value.trim().equalsIgnoreCase("Fixed")?XfrType.Fixed:
			    		          nv.value.trim().equalsIgnoreCase("LTC")?XfrType.LTC:
			    		          nv.value.trim().equalsIgnoreCase("Mvar")?XfrType.Mvar:XfrType.Phase;
			    else if (nv.name.equals("XFFixedTap"))
			    	fromFixedTap=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFFixedTap:1"))
			    	toFixedTap=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFNominalKV"))
			    	xfrFromSideNominalKV=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFNominalKV:1"))
			    	xfrToSideNominalKV=Double.valueOf(nv.value);
				
			}
			/*
			*/
			fromBusId = parser.BusIdPreFix + fromBusNum;
			toBusId = parser.BusIdPreFix + toBusNum;
			regBusId= parser.BusIdPreFix + regBusNum;

			XfrBranchXmlType xfr = parser.getXfrBranch(fromBusId, toBusId, circuitId);
			
			if(xfr.getFromTurnRatio()!=null){
				xfr.setFromTurnRatio(AclfDataSetter.createTurnRatioPU(
						xfr.getFromTurnRatio().getValue()*fromFixedTap));
			}
			xfr.setToTurnRatio(AclfDataSetter.createTurnRatioPU(toFixedTap));
			
			if(xfr instanceof PSXfrBranchXmlType){
				PSXfrBranchXmlType psXfr=(PSXfrBranchXmlType) xfr;
				if(xfrRegMin!=0|| xfrRegMax!=0){
					setXfrPhaseControlData(isXFAutoControl, xfrRegMin, xfrRegMax,
							xfrTapMax, xfrTapMin, regTargetType, psXfr);
				}
			}
			else{
				if(xfrRegMin!=0|| xfrRegMax!=0)
			        setTapControlData(isXFAutoControl, xfrRegMin, xfrRegMax,
					xfrTapMax, xfrTapMin, xfrTapStep, regBusId,
					regTargetType, xfrType, xfr);
			}
			// TODO set type and regulation info;
			TransformerInfoXmlType xfmrInfo = new TransformerInfoXmlType();
			xfr.setXfrInfo(xfmrInfo);
			xfmrInfo.setZTableNumber(tableNum);
			
			if (xfmrBaseMva != 0.0) {
				xfr.setXfrInfo(odmObjFactory.createTransformerInfoXmlType());
				TransformerInfoXmlType xfrInfo = xfr.getXfrInfo();
				xfrInfo.setDataOnSystemBase(false);
				xfrInfo.setRatedPower(BaseDataSetter.createApparentPower(xfmrBaseMva, ApparentPowerUnitType.MVA));
				if (xfrFromSideNominalKV!=0.0)xfrInfo.setFromRatedVoltage(BaseDataSetter.createVoltageValue(xfrFromSideNominalKV, VoltageUnitType.KV));
				if (xfrToSideNominalKV!=0.0)xfrInfo.setToRatedVoltage(BaseDataSetter.createVoltageValue(xfrToSideNominalKV, VoltageUnitType.KV));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void process2WXfrData(List<NVPair> xfrDataList){
		long fromBusNum=-1,toBusNum=-1;
		String fromBusId, toBusId,circuitId="1";
		String type="";
		String xfrId="";
		boolean closed=true,isXFAutoControl=false;
		double r=0,x=0,b=0,g=0, bMag=0,gMag=0, // all per unit value on system base;
		       fBusShuntMW=0,fBusShuntMvar=0,tBusShuntMW=0,tBusShuntMvar=0, //shunt Mw and Mvar at two ends;
		       mvaRating1=9999,mvaRating2=9999,mvaRating3=9999,//mvar rating
		       lineTap=1.0, toTurnRatio=1.0;//tap ratio
		double phaseAngle=0.0;
		double xfrRegMin=0, xfrRegMax=0;
		double xfrTapMax=0, xfrTapMin=0;
		double xfrStep=0;
		double xfrMvaBase = 0.0, xfrFromSideNominalKV = 0.0, xfrToSideNominalKV=0.0;
		long regBusNum=-1;//ONLY use for LTC type Transformer
		String regBusId="";
		XfrCtrlTargetType regTargetType=null;
		XfrType xfrType=null;
		//The following is ONLY for specific user-defined format.
		String typeToken="CustomString"; //type
		String idToken="CustomString:1"; //branch Id

		try{
			for(PowerWorldAdapter.NVPair nv:inputNvPairs){
				//TODO branch id, NE-ISO use "customString:1" as the corresponding argument;	
				if (nv.name.equals("BusNum"))
					fromBusNum=Long.valueOf(nv.value); //mandatory field
				
				else if (nv.name.equals("BusNum:1"))
					toBusNum=Long.valueOf(nv.value); //mandatory field
				
				else if (nv.name.equals("LineCircuit"))
					circuitId=nv.value;
				
				else if (nv.name.equals("LineStatus"))
					closed=nv.value.equalsIgnoreCase("Closed")?true:false;
			    
				// LineR:1, LineX:1, LineG:1, LineC:1, XFStep:1, XFTapMax:1, XFTapMin:1, LineTap:1
				//TODO The suffix of ¡°£º1¡± is used for Transformer definition,means those data values are based on transformer MVA base 
			    else if (nv.name.equals("LineR") || nv.name.equals("LineR:1"))
					r=Double.valueOf(nv.value);
			    
			    else if (nv.name.equals("LineX") || nv.name.equals("LineX:1"))
					x=Double.valueOf(nv.value);
			    else if (nv.name.equals("LineC") || nv.name.equals("LineC:1"))
					b=Double.valueOf(nv.value);
			    else if (nv.name.equals("XfrmerMagnetizingB"))
			    	bMag=nv.value.isEmpty()?0:Double.valueOf(nv.value);
			    else if (nv.name.equals("LineG") || nv.name.equals("LineG:1"))
					g=Double.valueOf(nv.value);
			    else if (nv.name.equals("XfrmerMagnetizingG"))
			        gMag=nv.value.isEmpty()?0:Double.valueOf(nv.value);
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
			    
			    else if (nv.name.equals("LineTap") || nv.name.equals("LineTap:1") || nv.name.equals("XFFixedTap"))
					lineTap=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFFixedTap:1"))
			    	toTurnRatio=Double.valueOf(nv.value);
				
			    else if (nv.name.equals("LinePhase"))
			    	phaseAngle=Double.valueOf(nv.value);
				//TODO the following is for Transformer Type Branch
			    else if (nv.name.equals("XFRegMin"))
			    	xfrRegMin=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFRegMax"))
			    	xfrRegMax=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFTapMax")||nv.name.equals("XFTapMax:1"))
			    	xfrTapMax=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFTapMin")||nv.name.equals("XFTapMin:1"))
			    	xfrTapMin=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFStep")||nv.name.equals("XFStep:1"))
			    	xfrStep=Double.valueOf(nv.value);
			    else if(nv.name.equals(typeToken))
			    	type=nv.value;
			    else if(nv.name.equals(idToken))
			    	xfrId=nv.value;
				
			    else if (nv.name.equals("XFMVABase"))
			    	xfrMvaBase=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFNominalKV"))
			    	xfrFromSideNominalKV=Double.valueOf(nv.value);
			    else if (nv.name.equals("XFNominalKV:1"))
			    	xfrToSideNominalKV=Double.valueOf(nv.value);
				
			    else if (nv.name.equals("XFRegTargetType"))
			    	regTargetType=nv.value.startsWith("Middle")?XfrCtrlTargetType.Midddle_Of_Range
			    			       :XfrCtrlTargetType.MaxMin;
			    else if (nv.name.equals("XFAuto"))
			    	isXFAutoControl=nv.value.trim().equalsIgnoreCase("No")?false:true;
			    	
			    else if (nv.name.equals("XFRegBus"))
					regBusNum=Long.valueOf(nv.value); 
			    else if(nv.name.equals("LineXFType"))
			    	xfrType=nv.value.trim().equalsIgnoreCase("Fixed")?XfrType.Fixed:
			    		          nv.value.trim().equalsIgnoreCase("LTC")?XfrType.LTC:
			    		          nv.value.trim().equalsIgnoreCase("Mvar")?XfrType.Mvar:XfrType.Phase;
				
			}
			
			if(gMag==0&&g!=0)gMag=g;
			if(bMag==0&&b!=0)bMag=b;
		    fromBusId=AbstractModelParser.BusIdPreFix+fromBusNum;
		    toBusId=AbstractModelParser.BusIdPreFix+toBusNum;
		    if(regBusNum>0)regBusId=AbstractModelParser.BusIdPreFix+regBusNum;
		    
		    // create a branch record
		    XfrBranchXmlType xfr=null;
		    
		    //phase shifting transformer or traditional transformer
		    xfr=phaseAngle!=0?parser.createPSXfrBranch(fromBusId, toBusId, circuitId)
		    
		    			               :parser.createXfrBranch(fromBusId, toBusId, circuitId);;
		    
			/*
			 * common setting for Transformer branch
			 */
		    
			xfr.setOffLine(!closed);
			xfr.setZ(BaseDataSetter.createZValue(r, x, ZUnitType.PU));
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

			if (phaseAngle == 0) {// transformer type, since it is rare for a PSXfr to have LinePhase=0; 
					
					AclfDataSetter.createXformerData(xfr, r, x, ZUnitType.PU,
							lineTap, toTurnRatio, gMag, bMag, YUnitType.PU);
					if(xfrRegMin!=0||xfrRegMax!=0)
                    setTapControlData(isXFAutoControl, xfrRegMin, xfrRegMax,
							xfrTapMax, xfrTapMin, xfrStep, regBusId,
							regTargetType, xfrType, xfr);
					
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
					PSXfrBranchXmlType psXfr = (PSXfrBranchXmlType) xfr;
					AclfDataSetter.createPhaseShiftXfrData(psXfr, r, x,
							ZUnitType.PU, lineTap, toTurnRatio, phaseAngle, 0,
							AngleUnitType.DEG, gMag, bMag, YUnitType.PU);
					if(xfrRegMin!=0||xfrRegMax!=0)
					setXfrPhaseControlData(isXFAutoControl, xfrRegMin,
							xfrRegMax, xfrTapMax, xfrTapMin, regTargetType,
							psXfr);
					

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
					xfr.setXfrInfo(odmObjFactory.createTransformerInfoXmlType());
					TransformerInfoXmlType xfrInfo = xfr.getXfrInfo();
					xfrInfo.setDataOnSystemBase(false);
					xfrInfo.setRatedPower(BaseDataSetter.createApparentPower(xfrMvaBase, ApparentPowerUnitType.MVA));
					xfrInfo.setFromRatedVoltage(BaseDataSetter.createVoltageValue(xfrFromSideNominalKV, VoltageUnitType.KV));
					xfrInfo.setToRatedVoltage(BaseDataSetter.createVoltageValue(xfrToSideNominalKV, VoltageUnitType.KV));
				}

			//set rating limit
			xfr.setRatingLimit(odmObjFactory.createBranchRatingLimitXmlType());
			
			AclfDataSetter.setBranchRatingLimitData(xfr.getRatingLimit(),
					mvaRating1, mvaRating2, mvaRating3, ApparentPowerUnitType.MVA);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	private void setXfrPhaseControlData(boolean isXFAutoControl,
			double xfrRegMin, double xfrRegMax, double xfrTapMax,
			double xfrTapMin, XfrCtrlTargetType regTargetType,
			PSXfrBranchXmlType psXfr) {
		// angle adjustment
		AngleAdjustmentXmlType angAdj = new AngleAdjustmentXmlType();
		psXfr.setAngleAdjustment(angAdj);
		//set control status, active if offLine=false;
		angAdj.setOffLine(!isXFAutoControl);
		angAdj.setAngleAdjOnFromSide(true);
		
		angAdj.setAngleLimit(odmObjFactory.createAngleLimitXmlType());
		/*
		 * It is assumed here that the {xfrTapMax, xfrTapMin} are also 
		 * used to represent Angle adjustment Limit;
		 */
		BaseDataSetter.setLimit(angAdj.getAngleLimit(), xfrTapMax, xfrTapMin);
		angAdj.getAngleLimit().setUnit(AngleUnitType.DEG);
		
		/*
		 * assume the desired value is measured at the from side, since the 
		 * value is not specified in PWD file
		 */
		angAdj.setDesiredMeasuredOnFromSide(true);
		
		if(regTargetType!=null){
			if(regTargetType==XfrCtrlTargetType.Midddle_Of_Range){
				angAdj.setDesiredValue((xfrRegMax+xfrRegMin)/2);
				angAdj.setDesiredActivePowerUnit(ActivePowerUnitType.MW);
				angAdj.setMode(AdjustmentModeEnumType.VALUE_ADJUSTMENT);
			}
			else {
				angAdj.setRange(new LimitXmlType());
				angAdj.setDesiredActivePowerUnit(ActivePowerUnitType.MW);
				BaseDataSetter.setLimit(angAdj.getRange(), xfrRegMax,
						xfrRegMin);
				angAdj.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
			}
			
		}
		
		angAdj.setDesiredMeasuredOnFromSide(true);
	}

	private void setTapControlData(boolean isXFAutoControl, double xfrRegMin,
			double xfrRegMax, double xfrTapMax, double xfrTapMin,
			double xfrStep, String regBusId, XfrCtrlTargetType regTargetType,
			XfrType xfrType, XfrBranchXmlType xfr) {
		//tap control
		TapAdjustmentXmlType tapAdj = odmObjFactory.createTapAdjustmentXmlType();
		xfr.setTapAdjustment(tapAdj);
		tapAdj.setOffLine(!isXFAutoControl);
		tapAdj.setTapLimit(BaseDataSetter.createTapLimit(xfrTapMax, xfrTapMin));
		tapAdj.getTapLimit().setUnit(FactorUnitType.PU);
		tapAdj.setTapAdjStepSize(xfrStep);
		tapAdj.setTapAdjOnFromSide(true);//PWD standard transformer model tap setting: fromTap:1.0
		
		if(xfrType!=null){
			if(xfrType==XfrType.LTC){
				tapAdj.setAdjustmentType(TapAdjustmentEnumType.VOLTAGE);
		  		VoltageAdjustmentDataXmlType vAdjData = odmObjFactory.createVoltageAdjustmentDataXmlType();
		  		tapAdj.setVoltageAdjData(vAdjData);
		  		//common setting
		  		vAdjData.setDesiredVoltageUnit(VoltageUnitType.PU);
		  		
		  		 if(regTargetType!=null){
		            	if(regTargetType==XfrCtrlTargetType.Midddle_Of_Range){
		            		vAdjData.setMode(AdjustmentModeEnumType.VALUE_ADJUSTMENT);
			          		vAdjData.setDesiredValue((xfrRegMax+xfrRegMin)/2);				
		            	}
		            	else{
		            		vAdjData.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
		    		  		vAdjData.setRange(odmObjFactory.createLimitXmlType());
		    		  		BaseDataSetter.setLimit(vAdjData.getRange(), xfrRegMax,
		    						xfrRegMin);
		            	}
		  		 }
		  		
		  		IDRefRecordXmlType refBus = parser.createBusRef(regBusId);
		  		if (refBus != null) {
		  			vAdjData.setAdjVoltageBus(refBus);
		  			if (regBusId.equals(BaseJaxbHelper.getRecId(xfr.getFromBus())))
		  				vAdjData.setAdjBusLocation(TapAdjustBusLocationEnumType.FROM_BUS);
		  			else if (regBusId.equals(BaseJaxbHelper.getRecId(xfr.getToBus())))
		  				vAdjData.setAdjBusLocation(TapAdjustBusLocationEnumType.TO_BUS);
		  			else {
		  				ODMLogger.getLogger().warning("Cannot decide xfr tap control bus location: " + xfr.getId());
		  				tapAdj.setOffLine(true);
		  			}
		  		}
		  		else
		  			// when the bus to be voltage controlled cannot be found in the
		  			// network, the control is turned off
		  			tapAdj.setOffLine(true);
			}
			else if(xfrType==XfrType.Mvar){
				tapAdj.setAdjustmentType(TapAdjustmentEnumType.M_VAR_FLOW);
			    MvarFlowAdjustmentDataXmlType mvarAdjData = odmObjFactory.createMvarFlowAdjustmentDataXmlType();
		  		tapAdj.setMvarFlowAdjData(mvarAdjData);
		  		//common setting
		  		mvarAdjData.setRange(new LimitXmlType());
		  		BaseDataSetter.setLimit(mvarAdjData.getRange(), xfrRegMax,
						xfrRegMin);
		  		mvarAdjData.setDesiredMvarFlowUnit(ReactivePowerUnitType.MVAR);
		  		
		  		 if(regTargetType!=null){
		  			 
		            	if(regTargetType==XfrCtrlTargetType.Midddle_Of_Range){
		            		mvarAdjData.setMode(AdjustmentModeEnumType.VALUE_ADJUSTMENT);
			          		mvarAdjData.setDesiredValue((xfrRegMax+xfrRegMin)/2);				
			          		
		            	}
		            	else{
		            		mvarAdjData.setMode(AdjustmentModeEnumType.RANGE_ADJUSTMENT);
		            	}
		            	
		  		 }
			}
		}//END OF TAP CONTROL SETTING
	}
	
	
	public void process3WXFomerData(String triWXformerDataStr){
		/*
		 * the 3-winding transformers are treated as 3 2-winding transformers
		 *  with an additional star bus added to the network;
		 */
		
		
	}
}
