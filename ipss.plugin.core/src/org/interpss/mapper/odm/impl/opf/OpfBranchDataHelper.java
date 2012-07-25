package org.interpss.mapper.odm.impl.opf;

import static org.interpss.mapper.odm.ODMUnitHelper.ToActivePowerUnit;
import org.ieee.odm.schema.ActivePowerRatingXmlType;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ApparentPowerUnitType;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.CurrentUnitType;
import org.ieee.odm.schema.CurrentXmlType;
import org.interpss.numeric.datatype.Unit.UnitType;

import com.interpss.common.datatype.UnitHelper;
import com.interpss.common.util.IpssLogger;
import com.interpss.opf.OpfBranch;

public class OpfBranchDataHelper {
	
	private OpfBranch opfBranch = null;
	private BaseBranchXmlType braXml = null;
	
	/**
	 * constructor
	 * 
	 * @param opfBranch
	 * @param opfBranch
	 */
	public OpfBranchDataHelper(OpfBranch opfBranch, BaseBranchXmlType braXml) {
		this.opfBranch = opfBranch;
		this.braXml = braXml;
	}
	
	public void setMwRating(){
		BranchXmlType branchXml = (BranchXmlType)braXml;
		ActivePowerRatingXmlType mwLim = branchXml.getRatingLimit().getMw();
		ActivePowerUnitType unit=  mwLim.getUnit();
		UnitType ipssUnit = ToActivePowerUnit.f(unit);		
		double baseKVA = opfBranch.getNetwork().getBaseKva();
		
		if(mwLim.getRating1()!=null){
			double mw1 = mwLim.getRating1();
			double mwLimInPu = UnitHelper.pConversion(mw1, baseKVA, ipssUnit, UnitType.PU);
			opfBranch.setRatingMw1(mwLimInPu);
		}
		if(mwLim.getRating2()!=null){
			double mw2 = mwLim.getRating2();
			double mwLimInPu = UnitHelper.pConversion(mw2, baseKVA, ipssUnit, UnitType.PU);
			opfBranch.setRatingMw2(mwLimInPu);
		}
		if(mwLim.getRating3()!=null){
			double mw3 = mwLim.getRating3();
			double mwLimInPu = UnitHelper.pConversion(mw3, baseKVA, ipssUnit, UnitType.PU);
			opfBranch.setRatingMw3(mwLimInPu);
		}			

		
	}
	

}
