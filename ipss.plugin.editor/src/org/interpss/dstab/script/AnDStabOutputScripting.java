/*
 * @(#)AnOutputScripting.java   
 *
 * Copyright (C) 2007 www.interpss.org
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
 * @Date 02/05/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.dstab.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnDStabOutputScripting {
	// filename for output
	String filename() default "c:/tmp/ipssout.csv";

	// variable list 
	String[] varList();
	/*
	 *  Sample       display name           bus id     dispaly variable 
		varList = { "str.Angle(Mach@0001),  0001,      mach.angle",
			    	"str.Speed(Mach@0001),  0001,      mach.speed",
			    	...
	 */
}
