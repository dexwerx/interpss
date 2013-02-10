package org.interpss.datamodel.bean;

import java.util.List;

public class BaseBusBean extends BaseJSONBean{

	/**
	 * bus generator type code 
	 */
	public static enum GenCode {Swing, PV, PQ};
	
	/**
	 * bus load type code 
	 */
	public static enum LoadCode {ConstP, ConstI, ConstZ};

	public double
		base_v,				// bus base voltage						
		v_ang;					// bus voltage angle
	
	public String 
		area="1", 				// bus area number/id
		zone="1";				// bus zone number/id
	
	public GenCode 
		gen_code;				// bus generator code
	
	public LoadCode 
		load_code;				// bus load code	
		
	public BaseBusBean() {}
	
	public boolean validate(List<String> msgList) { 
		return true;
	}
	

}
