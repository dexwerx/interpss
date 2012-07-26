 /*
  * @(#) ComplexFuncTest.java   
  *
 *  Copyright (C) 2006-2011 www.interpss.org
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
  * @Date 07/15/2012
  * 
  *   Revision History
  *   ================
  *
  */

package test.org.interpss;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.datatype.ComplexFunc;
import org.junit.Test;

public class ComplexFuncTest {
	@Test
	public void test() {
		assertTrue( ComplexFunc.sameValue(new Complex(1.0,0.0), Complex.ONE));
    }
}
