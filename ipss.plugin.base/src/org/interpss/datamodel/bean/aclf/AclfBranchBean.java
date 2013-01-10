package org.interpss.datamodel.bean.aclf;

import org.interpss.datamodel.bean.BaseJSONBean;

public class AclfBranchBean extends BaseJSONBean {
	public static enum BranchCode {Line, Xfr, PsXfr};
	
	public String 
		f_id, 
		t_id, 
		cir_id = "1";
	
	public BranchCode 
		code = BranchCode.Line;
	
	public double 
		r, 
		x, 
		b, 
		f_ratio=1.0, 
		t_ratio=1.0,
		f_ang=0.0, 
		t_ang=0.0;
	
	public AclfBranchBean() {}
}
