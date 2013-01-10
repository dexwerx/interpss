package org.interpss.model.bean.aclf;

public class AclfBranchBean {
	public static enum BranchCode {Line, Xfr};
	
	public String id, name, desc;
	public String fromId, toId;
	
	public BranchCode branchCode;
	public double r, x, b, fromTurnRatio, toTurnRatio;
	
	/*
	1   1   2    0.04    0.25    -0.25
	2   1   3    0.1     0.35     0.0
	3   2   3    0.08    0.3     -0.25
	4   4   2    0.0     0.015    1.05
	5   5   3    0.0     0.03     1.05

	AclfInputUtilFunc.addLineBranchTo(net, "1", "2", .04, .25, UnitType.PU, .25, UnitType.PU);
	AclfInputUtilFunc.addLineBranchTo(net, "1", "3", .1,  .35, UnitType.PU, .0,  UnitType.PU);
	AclfInputUtilFunc.addLineBranchTo(net, "2", "3", .08, .3,  UnitType.PU, .25, UnitType.PU);
	AclfInputUtilFunc.addXfrBranchTo( net, "4", "2", .0,  .015,UnitType.PU, 1.0, 1.05 , UnitType.PU);
	AclfInputUtilFunc.addXfrBranchTo( net, "5", "3", .0,  .03, UnitType.PU, 1.0, 1.05, UnitType.PU);	 */


}
