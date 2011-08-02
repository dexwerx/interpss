package BPA;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.CurrentUnitType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.ZUnitType;

import util.StringUtil;
import util.UnitConverter;

 /**
  * AclfBranch includes Aclf Line and Transformer
  * 1. BranchBase: set  the base data for all type,e.g. bus name, baseKV
  * 2. BranchLFData: set X, R, B/2 and G/2
  * 3. type-specific data: turn ratio for transformer and phase-angle for pxfr
  * 
  * 
  * Note: only 2-wing-type is supported in BPA, for the 3w type,
  *       a transformation is needed by adding a virtual node
  *  
  * @author Tony Huang
  *
  */

public class BpaAclfBranchRecord {
	private static BaseBranchXmlType _braXml=null;
	private static StringBuffer _branchStr=null;
	private static double _baseMVA=100.0D;
	private static AclfModelParser _parser=null;
	public BpaAclfBranchRecord(AclfModelParser parser){
		this._branchStr=new StringBuffer();
		this._parser=parser;
		this._baseMVA=parser.getAclfNet().getBasePower().getValue();
		
	}
	public String setAclfBranchData(String bpaLfNetStr,BaseBranchXmlType branchXml){
		_braXml=branchXml;
		_branchStr=new StringBuffer();
		setAclfBranchBaseData();
		setAclfBranchLFData();
		//set type specific data;
		
		// add to the net
		bpaLfNetStr+=_branchStr+"\n";
		return bpaLfNetStr;
		
	}
	
	private static void setAclfBranchBaseData(){
		//get type
		String type=getType();
		String reviseCode=StringUtil.getSpace(1);
		//TODO
		String owner=StringUtil.getSpace(3);
		// busName1 ,baseKV1
		LoadflowBusXmlType fbus= (LoadflowBusXmlType) _braXml.getFromBus().getIdRef();
		String bus1_Name=fbus.getName();
		
		double baseKV1=fbus.getBaseVoltage().getValue();
		String baseKV1Str=(baseKV1>100.0)?String.format("%4.0f", baseKV1):""+baseKV1;
		
		// busName2 ,baseKV2
		LoadflowBusXmlType tbus= (LoadflowBusXmlType) _braXml.getToBus().getIdRef();
		String bus2_Name=tbus.getName();
		
		double baseKV2=tbus.getBaseVoltage().getValue();
		String baseKV2Str=(baseKV2>100.0)?String.format("%4.0f", baseKV2):""+baseKV2;
		
		String meter=StringUtil.getSpace(1);
		
		String branchId=_braXml.getCircuitId();
		String section=StringUtil.getSpace(1);
		
		bus1_Name=StringUtil.addSpace(bus1_Name, 8);
		baseKV1Str=StringUtil.addSpace(baseKV1Str, 4);
		bus2_Name=StringUtil.addSpace(bus2_Name, 8);
		baseKV2Str=StringUtil.addSpace(baseKV2Str, 4);
		
		
		_branchStr.append(type).append(reviseCode).append(owner).append(bus1_Name)
		           .append(baseKV1Str).append(meter).append(bus2_Name).append(baseKV2Str)
		           .append(branchId).append(section);
		
	}

	private static void setAclfBranchLFData(){
		if(_braXml instanceof LineBranchXmlType)setLineLFData();
		else if(_braXml instanceof  XfrBranchXmlType)setXfrLFData();
		else if(_braXml instanceof  PSXfrBranchXmlType)setPSXfrLFData();
		else throw new UnsupportedOperationException("input Branch type unsupported!");
	}
	private static String getType(){
		
		String type="";
		if(_braXml instanceof LineBranchXmlType){
			type="L ";
		}
		else if(_braXml instanceof XfrBranchXmlType){
			type="T ";
			
		}
		else if(_braXml instanceof PSXfrBranchXmlType){
			type="TP";
		}
		else throw new UnsupportedOperationException("input Branch type unsupported!");
		return type;
	}
	
	private static void setLineLFData(){
		LineBranchXmlType line=(LineBranchXmlType) _braXml;
		String rateAmpStr=StringUtil.getSpace(4);
		if(line.getRatingLimit().getCurrent()!=null){
		double rateAmp=line.getRatingLimit().getCurrent().getValue();//assume it is in Ample unit now
	    if(line.getRatingLimit().getCurrent().getUnit()==CurrentUnitType.KA)
	    	rateAmp=UnitConverter.convCurrentUnit(rateAmp, CurrentUnitType.KA,  CurrentUnitType.AMP);
	      rateAmpStr=String.format("%4.0f", rateAmp);
		}
	    String lineNum=" ";
	    double rpu=line.getZ().getRe();
	    double xpu=line.getZ().getIm();
	    
	    if(line.getZ().getUnit()!=ZUnitType.PU){// conv to PU is needed
	    	LoadflowBusXmlType bus =(LoadflowBusXmlType) line.getFromBus().getIdRef();
	    	double baseKV=bus.getBaseVoltage().getValue();
	    	rpu=UnitConverter.convZUnit(rpu, line.getZ().getUnit(), ZUnitType.PU, 
	    		baseKV, _baseMVA);
	    	xpu=UnitConverter.convZUnit(xpu, line.getZ().getUnit(), ZUnitType.PU, 
		    		baseKV, _baseMVA);
	    	
	    }
	    // String length;  in BPA Rmin=0.0001pu 
	    String rpuStr=(rpu>=0)?String.format("%6.4f", rpu):(rpu<-1.0)?String.format("% 5.3f", rpu):
	    	"-"+String.format("%5.4f", rpu).substring(String.format("%5.4f", rpu).indexOf("."));
	    String xpuStr=(xpu>=0)?String.format("%6.4f", xpu):(xpu<-1.0)?String.format("% 5.3f", xpu):
	    	"-"+String.format("%5.4f", xpu).substring(String.format("%5.4f", xpu).indexOf("."));
	    
	    double gHalf=line.getTotalShuntY().getRe()/2;
	    double bHalf=line.getTotalShuntY().getIm()/2;
	    String gHalfStr=(gHalf>=0)?String.format("%6.4f", gHalf):String.format("% 5.0f", gHalf*1.0e5);
	    String bHalfStr=(bHalf>=0)?String.format("%6.4f", bHalf):String.format("% 5.0f", bHalf*1.0e5);
	    
	    rpuStr=(rpu==0.0)?StringUtil.getSpace(6):StringUtil.addSpace(rpuStr, 6);
	    xpuStr=StringUtil.addSpace(xpuStr, 6);
	    gHalfStr=(gHalf==0.0)?StringUtil.getSpace(6):StringUtil.addSpace(gHalfStr, 6);
	    bHalfStr=StringUtil.addSpace(bHalfStr, 6);
	    
	    
	    _branchStr.append(rateAmpStr).append(lineNum).append(rpuStr)
	               .append(xpuStr).append(gHalfStr).append(bHalfStr);

	
	}
	private static void setXfrLFData(){
		XfrBranchXmlType xfr=(XfrBranchXmlType) _braXml;
		String rateMVAStr=StringUtil.getSpace(4);
		
		if(xfr.getXfrInfo().getRatedPower()!=null){
		double rateMVA=xfr.getXfrInfo().getRatedPower().getValue();//assume it is in Ample unit now
	    if(xfr.getXfrInfo().getRatedPower().getUnit()==ApparentPowerUnitType.PU)
	    	rateMVA*=_baseMVA;
	      rateMVAStr=String.format("%4.0f", rateMVA);
		}
	    String xfrNum=" ";
	    double rpu=xfr.getZ().getRe(); //assume it to be in PU unit;
	    double xpu=xfr.getZ().getIm();
	    

	    // String length;  in BPA Rmin=0.0001pu 
	    String rpuStr=(rpu>=0)?String.format("%6.4f", rpu):(rpu<-1.0)?String.format("% 5.3f", rpu):
	    	"-"+String.format("%5.4f", rpu).substring(String.format("%5.4f", rpu).indexOf("."));
	    String xpuStr=(xpu>=0)?String.format("%6.4f", xpu):(xpu<-1.0)?String.format("% 5.3f", xpu):
	    	"-"+String.format("%5.4f", xpu).substring(String.format("%5.4f", xpu).indexOf("."));
	    String gStr="";
	    String bStr="";
	    if(xfr.getMagnitizingY()!=null){
	        double g=xfr.getMagnitizingY().getRe();
	        double b=xfr.getMagnitizingY().getIm();
	        gStr=(g>=0)?String.format("%6.4f", g):String.format("% 5.0f", g*1.0e5);
	        bStr=(b>=0)?String.format("%6.4f", b):String.format("% 5.0f", b*1.0e5);
	    }

	    double fromTap=xfr.getFromTurnRatio().getValue()*xfr.getXfrInfo().getFromRatedVoltage().getValue();
	    double toTap=xfr.getToTurnRatio().getValue()*xfr.getXfrInfo().getToRatedVoltage().getValue();
	    
	    String fromTapStr=(fromTap<100.0)?String.format("%5.2f", fromTap):String.format("%5.0f", fromTap*100);
	    String toTapStr  =(toTap<100.0)?String.format("%5.2f", toTap):String.format("%5.0f", toTap*100);
	    rpuStr=(rpu==0.0)?StringUtil.getSpace(6):StringUtil.addSpace(rpuStr, 6);
	    xpuStr=StringUtil.addSpace(xpuStr, 6);
	    gStr=StringUtil.addSpace(gStr, 6);
	    bStr=StringUtil.addSpace(bStr, 6);
	    fromTapStr = StringUtil.addSpace(fromTapStr, 5);
	    toTapStr   = StringUtil.addSpace(toTapStr, 5);
	    
	    
	    _branchStr.append(rateMVAStr).append(xfrNum).append(rpuStr)
	               .append(xpuStr).append(gStr).append(bStr).append(fromTapStr).append(toTapStr);
	}
	private static void setPSXfrLFData(){
		
	}
	private static void setHVDCLFData(){
		
	}
	public static String stdout(){
		return _branchStr.toString();
	}

}
