package org.interpss.datamodel.bean.aclf;

import org.interpss.datamodel.bean.BaseBranchBean;
import org.interpss.datamodel.bean.datatype.BranchValueBean;
import org.interpss.datamodel.bean.datatype.ComplexBean;

public class AclfBranchBean extends BaseBranchBean {
	public ComplexBean 
		z,					// branch z
		shunt_y;     		// branch total shunt B

	public BranchValueBean 
		ratio,				// xfr branch turn ratio
	 	ang;				// PsXfr shifting angle
	
	public AclfBranchBean() {}
}
