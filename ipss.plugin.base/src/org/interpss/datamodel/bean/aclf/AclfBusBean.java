package org.interpss.datamodel.bean.aclf;

public class AclfBusBean {
	public static enum GenCode {Swing, PV, PQ};
	public static enum LoadCode {ConstP, ConstI, ConstZ};

	public String id, name, desc;
	public double baseVoltage;
	public String area="1", zone="1";
	
	public GenCode genCode;
	public LoadCode loadCode;
	public double v_msg=1.0, v_ang;
	public double p_gen, q_gen;
	public double p_load, q_load;
	
	public AclfBusBean() {}
}
