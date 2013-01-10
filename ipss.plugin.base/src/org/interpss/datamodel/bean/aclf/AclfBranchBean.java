package org.interpss.datamodel.bean.aclf;

public class AclfBranchBean {
	public static enum BranchCode {Line, Xfr, PsXfr};
	
	public String id, name, desc;
	public String fromId, toId, cirId = "1";
	
	public BranchCode branchCode = BranchCode.Line;
	public double r, x, b, fromTurnRatio=1.0, toTurnRatio=1.0;
	
	public AclfBranchBean() {}
}
