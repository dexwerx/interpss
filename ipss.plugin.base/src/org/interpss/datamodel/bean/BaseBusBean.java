package org.interpss.datamodel.bean;

import java.util.List;

import org.interpss.datamodel.bean.datatype.ComplexBean;

public class BaseBusBean extends BaseJSONBean{

	/**
	 * bus generator type code 
	 */
	public static enum GenCode {Swing, PV, PQ};
	
	/**
	 * bus load type code 
	 */
	public static enum LoadCode {ConstP, ConstI, ConstZ};
	
	public long 
	    number;    
	
	public double
		base_v,				// bus base voltage
		v_mag=1.0,              // bus voltage in pu		
		v_ang = 0.0;					// bus voltage angle
		
	public ComplexBean
	     gen, 					// bus generation
	    load, 					// bus load
	    shunt;
	
	public long 
		area =1, 				// bus area number/id
		zone =1;				// bus zone number/id
	
	public GenCode 
		gen_code;				// bus generator code
	
	public LoadCode 
		load_code;				// bus load code	
		
	public BaseBusBean() {}
	
	public boolean validate(List<String> msgList) { 
		return true;
	}
	

}
