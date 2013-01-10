package org.interpss.model.bean.aclf;

public class AclfBusBean {
	public static enum GenCode {Swing, PV, PQ};
	public static enum LoadCode {ConstP};

	public String id, name, desc;
	public double baseVoltage;
	public String area, zone;
	
	public GenCode genCode;
	public LoadCode loadCode;
	public double v_msg, v_ang;
	public double p_gen, q_gen;
	public double p_load, q_load;
	
	/*
	1   1   1    1.6   0.8     1.0    0.0
	2   2   1    2     1       1.0    0.0
	3   3   1    3.7   1.3     1.0    0.0
	4   4   2    5     0.0     1.05   0.0
	5   5   3    0.0   0.0     1.05   0.0

	AclfInputUtilFunc.addLoadBusTo( net, "1", 13800, 1, 1, AclfLoadCode.CONST_P, 1.6, 0.8, UnitType.PU );
	AclfInputUtilFunc.addLoadBusTo( net, "2", 13800, 1, 1, AclfLoadCode.CONST_P, 2.0, 1.0, UnitType.PU );
	AclfInputUtilFunc.addLoadBusTo( net, "3", 13800, 2, 1, AclfLoadCode.CONST_P, 3.7, 1.3, UnitType.PU );
	AclfInputUtilFunc.addPVBusTo(   net, "4", 1000,  1, 1, 5.0, UnitType.PU, 1.05, UnitType.PU );
	AclfInputUtilFunc.addSwingBusTo(net, "5", 4000,  2, 1, 1.05, UnitType.PU, 0.0, UnitType.Deg );
	*/
}
