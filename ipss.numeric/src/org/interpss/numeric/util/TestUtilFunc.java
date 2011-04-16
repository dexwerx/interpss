/*
 * @(#)TestUtilFunc.java   
 *
 * Copyright (C) 2006 www.interpss.com
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
 * @Date 07/15/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.util;

import org.interpss.numeric.datatype.Complex3x1;

public class TestUtilFunc {
	public static boolean compare(Complex3x1 iPU_012, double zeroRe,
			double zeroIm, double oneRe, double oneIm, double twoRe,
			double twoIm) {
		if (!(NumericUtil.equals(iPU_012.a_0.getReal(), zeroRe)
				&& NumericUtil.equals(iPU_012.a_0.getImaginary(), zeroIm)
				&& NumericUtil.equals(iPU_012.b_1.getReal(), oneRe)
				&& NumericUtil.equals(iPU_012.b_1.getImaginary(), oneIm)
				&& NumericUtil.equals(iPU_012.c_2.getReal(), twoRe) && NumericUtil.equals(iPU_012.c_2
				.getImaginary(), twoIm))) {
			System.out.println("iPU_012 = " + iPU_012);
		}
		return NumericUtil.equals(iPU_012.a_0.getReal(), zeroRe)
				&& NumericUtil.equals(iPU_012.a_0.getImaginary(), zeroIm)
				&& NumericUtil.equals(iPU_012.b_1.getReal(), oneRe)
				&& NumericUtil.equals(iPU_012.b_1.getImaginary(), oneIm)
				&& NumericUtil.equals(iPU_012.c_2.getReal(), twoRe)
				&& NumericUtil.equals(iPU_012.c_2.getImaginary(), twoIm);
	}
}
