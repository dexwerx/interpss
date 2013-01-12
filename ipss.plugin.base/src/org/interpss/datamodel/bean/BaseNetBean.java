package org.interpss.datamodel.bean;

import org.interpss.numeric.datatype.Unit.UnitType;

public class BaseNetBean extends BaseJSONBean {
	public double 
		base_kva;      						// network base kva 

	/**
	 * units 
	 */
	public UnitType
		unit_ang  = UnitType.Deg,          	// angle unit for voltage, PsXfr shifting angle
		unit_bus_v = UnitType.PU, 			// bus voltage unit
		unit_bus_p = UnitType.PU,			// bus power (gen/load) unit
		unit_branch_z = UnitType.PU,		// branch impedance unit
		unit_branch_cur = UnitType.Amp,     // branch current unit
		unit_branch_b = UnitType.PU;        // branch shunt Y unit
	
	public BaseNetBean() {}
}
