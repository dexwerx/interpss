/*
 * @(#)ExciterObjectFactory.java   
 *
 * Copyright (C) 2008-2010 www.interpss.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @Author Mike Zhou
 * @Version 1.0
 * @Date 08/15/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.dstab.control.exc;

import org.interpss.dstab.control.exc.ieee.y1968.type1.Ieee1968Type1Exciter;
import org.interpss.dstab.control.exc.ieee.y1968.type1s.Ieee1968Type1sExciter;
import org.interpss.dstab.control.exc.ieee.y1968.type2.Ieee1968Type2Exciter;
import org.interpss.dstab.control.exc.ieee.y1968.type3.Ieee1968Type3Exciter;
import org.interpss.dstab.control.exc.ieee.y1968.type4.Ieee1968Type4Exciter;
import org.interpss.dstab.control.exc.ieee.y1981.dc1.IEEE1981DC1Exciter;
import org.interpss.dstab.control.exc.simple.SimpleExciter;
import org.interpss.dstab.control.exc.bpa.ea.EAExciter;
import org.interpss.dstab.control.exc.bpa.fa.FAExciter;
import org.interpss.dstab.control.exc.bpa.fj.FJExciter;
import org.interpss.dstab.control.exc.bpa.fk.FKExciter;
import org.interpss.dstab.control.exc.bpa.fvkv0.FVkv0Exciter;
import org.interpss.dstab.control.exc.bpa.fvkv1.FVkv1Exciter;
import com.interpss.dstab.mach.Machine;

public class ExciterObjectFactory {
	public static SimpleExciter createSimpleExciter(String id, String name, Machine machine) {
		SimpleExciter exc = new SimpleExciter(id, name, "InterPSS");
		exc.setMachine(machine); 
		return exc;
  	}
	public static Ieee1968Type1Exciter createIeee1968Type1Exciter(String id, String name, Machine machine) {
		Ieee1968Type1Exciter exc = new Ieee1968Type1Exciter(id, name, "InterPSS");
		exc.setMachine(machine); 
		return exc;
  	}
	public static Ieee1968Type1sExciter createIeee1968Type1sExciter(String id, String name, Machine machine) {
		Ieee1968Type1sExciter exc = new Ieee1968Type1sExciter(id, name, "InterPSS");
		exc.setMachine(machine); 
		return exc;
  	}
	public static Ieee1968Type2Exciter createIeee1968Type2Exciter(String id, String name, Machine machine) {
		Ieee1968Type2Exciter exc = new Ieee1968Type2Exciter(id, name, "InterPSS");
		exc.setMachine(machine); 
		return exc;
  	}
	public static Ieee1968Type3Exciter createIeee1968Type3Exciter(String id, String name, Machine machine) {
		Ieee1968Type3Exciter exc = new Ieee1968Type3Exciter(id, name, "InterPSS");
		exc.setMachine(machine); 
		return exc;
  	}
	public static Ieee1968Type4Exciter createIeee1968Type4Exciter(String id, String name, Machine machine) {
		Ieee1968Type4Exciter exc = new Ieee1968Type4Exciter(id, name, "InterPSS");
		exc.setMachine(machine); 
		return exc;
  	}
	/*
	 * IEEE 1981 Exciter set
	 */
	public static IEEE1981DC1Exciter createIeee1981DC1Exciter(String id, String name, Machine machine) {
		IEEE1981DC1Exciter  exc = new IEEE1981DC1Exciter (id, name, "InterPSS");
		exc.setMachine(machine); 
		return exc;
  	}
	
	/*
	 * IEEE 1992 Exciter set
	 */
	/*
	 * BPA Exciter set
	 */
	public static  EAExciter createBPAEATypeExciter(String id, String name, Machine machine) {
		EAExciter exc = new EAExciter(id, name, "BPA");
		exc.setMachine(machine); 
		return exc;
  	}
	public static  FAExciter createBPAFATypeExciter(String id, String name, Machine machine) {
		FAExciter exc = new FAExciter(id, name, "BPA");
		exc.setMachine(machine); 
		return exc;
  	}
	public static  FJExciter createBPAFJTypeExciter(String id, String name, Machine machine) {
		FJExciter exc = new FJExciter(id, name, "BPA");
		exc.setMachine(machine); 
		return exc;
  	}
	public static  FKExciter createBPAFKTypeExciter(String id, String name, Machine machine) {
		FKExciter exc = new FKExciter(id, name, "BPA");
		exc.setMachine(machine); 
		return exc;
  	}
	public static  FVkv0Exciter createBPAFVKv0TypeExciter(String id, String name, Machine machine) {
		FVkv0Exciter exc = new FVkv0Exciter(id, name, "BPA");
		exc.setMachine(machine); 
		return exc;
  	}
	
	public static  FVkv1Exciter createBPAFVKv1TypeExciter(String id, String name, Machine machine) {
		FVkv1Exciter exc = new FVkv1Exciter(id, name, "BPA");
		exc.setMachine(machine); 
		return exc;
  	}
	
}
