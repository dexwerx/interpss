package org.interpss.datamodel.bean.aclf;

import org.interpss.datamodel.bean.BaseBranchBean;

public class AclfBranchBean extends BaseBranchBean {
	public double 
		r,              	// branch resistance
		x, 					// branch reactance
		b, 					// branch shunt B
		f_ratio=1.0, 		// xfr branch from side turn ratio
		t_ratio=1.0,		// xfr branch to side turn ratio
		f_ang=0.0, 			// PsXfr from side shifting angle
		t_ang=0.0;			// PsXfr to side shifting angle
	
	public AclfBranchBean() {}
}
