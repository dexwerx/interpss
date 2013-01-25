package org.interpss.datamodel.bean.aclf;

import java.util.List;

import org.interpss.datamodel.bean.BaseJSONBean;
import org.interpss.datamodel.bean.datatype.ComplexBean;

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
		base_v,					// bus base voltage
		v_mag=1.0,				// bus voltage magnitude 
		v_ang;					// bus voltage angle
	
	public String 
		area="1", 				// bus area number/id
		zone="1";				// bus zone number/id
	
	public GenCode 
		gen_code;				// bus generator code
	
	public LoadCode 
		load_code;				// bus load code
	
	public ComplexBean
		gen, 					// bus generation
		load; 					// bus load
	
	public AclfBusBean() {}
	
	public boolean validate(List<String> msgList) { 
		boolean noErr = true;
		return noErr; 
	}
}
