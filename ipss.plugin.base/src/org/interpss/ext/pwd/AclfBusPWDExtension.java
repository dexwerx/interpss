package org.interpss.ext.pwd;

import java.util.Hashtable;

/**
 *  AclfBus extension for representing PowerWorld custom fields
 * 
 *  {Gen_CustomString:2=DZONE_1, Gen_CustomString=Sub1_14.9_G1, Gen_CustomString:1=G1, SubStation=Sub1}
 * 
 * @author mzhou
 *
 */
public class AclfBusPWDExtension extends Hashtable<String,String> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * “SubstationName_EquipmentName”. EquipmentName = Gen_CustomString:1
	 * 
	 * @return
	 */
	public String getGenOutageId() {
		if (this.get("SubStation") != null && this.get("Gen_CustomString:1") != null)
			return this.get("SubStation") + "_" + this.get("Gen_CustomString:1");
		else 
			return null;
	}
	
	/**
	 * get Shunt equipment name
	 * 
	 * @return
	 */
	public String getShuntEquipName() {
		return this.get("Shunt_CustomString");
	}

	/**
	 * to string function
	 */
	public String toString() {
		String str = "AclfBusPWDExtension: " + super.toString();
		return str;
	}
}
