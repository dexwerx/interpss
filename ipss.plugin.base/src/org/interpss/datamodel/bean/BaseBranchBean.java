package org.interpss.datamodel.bean;


public class BaseBranchBean extends BaseJSONBean {
	/**
	 *  branch type code
	 */
	public static enum BranchCode {
		Line,    					// transmission line 
		Xfr,     					// transformer
		PsXfr    					// phase-shifting transformer
	};
	
	public String 
		f_id,         				// branch from side bus id
		t_id,         				// branch to side bus id
		cir_id = "1";  				// branch circuit id/number
	
	public BranchCode 
		bra_code = BranchCode.Line;  	// branch type code
	
	public BaseBranchBean() {}
}
