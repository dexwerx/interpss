package org.interpss.dstab.control.exc;

import com.interpss.dstab.mach.Machine;

public class ExcUtil {
	public static double getExciterBasedIfd(Machine mach){
		double xad=mach.getMachData().getXd()-mach.getMachData().getXl();
		return mach.calculateIfd(mach.getDStabBus())*xad;
	}

}
