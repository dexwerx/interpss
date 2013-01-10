package org.interpss.datamodel.bean.aclf;

import org.interpss.datamodel.bean.BaseJSONBean;

public class AclfBusBean  extends BaseJSONBean {
	public static enum GenCode {Swing, PV, PQ};
	public static enum LoadCode {ConstP, ConstI, ConstZ};

	public double
		base_v;
	
	public String 
		area="1", 
		zone="1";
	
	public GenCode 
		gen_code;
	
	public LoadCode 
		load_code;
	
	public double 
		v_msg=1.0, 
		v_ang;
	
	public double 
		p_gen, 
		q_gen;
	
	public double 
		p_load, 
		q_load;
	
	public AclfBusBean() {}
}
