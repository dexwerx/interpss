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

package org.interpss;

import org.apache.commons.math.complex.Complex;
import org.interpss.numeric.datatype.Complex3x1;

import com.interpss.common.datatype.Constants;

public class TestUtilFunc {
	/* config used classpath based 
	public static String Simu_SpringConfigXmlFile = "c:/eclipse/InterpssDev/ipss/properties/springConfig/test/simuContext.xml";
	public static String Plugin_SpringConfigXmlFile = "c:/eclipse/InterpssDev/ipss/properties/springConfig/test/pluginContext.xml";
	*/
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean equal(final double x, final double y) {
		return Math.abs(x - y) < Constants.SmallDoubleNumber;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean equal(final Complex x, Complex y) {
		return Math.abs(x.getReal() - y.getReal()) < Constants.SmallDoubleNumber
				&& Math.abs(x.getImaginary() - y.getImaginary()) < Constants.SmallDoubleNumber;
	}

	public static boolean compare(Complex3x1 iPU_012, double zeroRe,
			double zeroIm, double oneRe, double oneIm, double twoRe,
			double twoIm) {
		if (!(equal(iPU_012.a_0.getReal(), zeroRe)
				&& equal(iPU_012.a_0.getImaginary(), zeroIm)
				&& equal(iPU_012.b_1.getReal(), oneRe)
				&& equal(iPU_012.b_1.getImaginary(), oneIm)
				&& equal(iPU_012.c_2.getReal(), twoRe) && equal(iPU_012.c_2
				.getImaginary(), twoIm))) {
			System.out.println("iPU_012 = " + iPU_012);
		}
		return equal(iPU_012.a_0.getReal(), zeroRe)
				&& equal(iPU_012.a_0.getImaginary(), zeroIm)
				&& equal(iPU_012.b_1.getReal(), oneRe)
				&& equal(iPU_012.b_1.getImaginary(), oneIm)
				&& equal(iPU_012.c_2.getReal(), twoRe)
				&& equal(iPU_012.c_2.getImaginary(), twoIm);
	}
}
