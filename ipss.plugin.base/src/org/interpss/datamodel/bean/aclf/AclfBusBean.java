package org.interpss.datamodel.bean.aclf;

import org.interpss.datamodel.bean.BaseJSONBean;

public class AclfBusBean  extends BaseJSONBean {
	/**
	 * bus generator type code 
	 */
	public static enum GenCode {Swing, PV, PQ};
	
	/**
	 * bus load type code 
	 */
	public static enum LoadCode {ConstP, ConstI, ConstZ};

	public double
		base_v;					// bus base voltage
	
	public String 
		area="1", 				// bus area number/id
		zone="1";				// bus zone number/id
	
	public GenCode 
		gen_code;				// bus generator code
	
	public LoadCode 
		load_code;				// bus load code
	
	public double 
		v_mag=1.0,				// bus voltage magnitude 
		v_ang,					// bus voltage angle
		p_gen, 					// bus generator real power
		q_gen,					// bus generator reactive power
		p_load, 				// bus load real power
		q_load;					// bus load reactive power
	
	public AclfBusBean() {}
}
