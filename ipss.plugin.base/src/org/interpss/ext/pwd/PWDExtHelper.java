package org.interpss.ext.pwd;

import com.interpss.core.net.Bus;

/**
 * 
 * @author mzhou
 *
 */
public class PWDExtHelper {

	public static String getSubstationName(Bus bus) {
		AclfBusPWDExtension ext = (AclfBusPWDExtension)(bus.getExtensionObject());
		return ext.get("SubStation");
	}
}
