 /*
  * @(#)RealMatrixTest.java   
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
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package test.org.interpss;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.interpss.numeric.exp.IpssNumericException;
import org.junit.Test;

/**
 * @author mzhou
 *
 */
public class RealMatrixTest {
	@Test
	public void test1()  throws IpssNumericException {
		// Create a real matrix with two rows and three columns
		double[][] matrixData = { {1d,2d,3d}, {2d,5d,3d}};
		RealMatrix m = new Array2DRowRealMatrix(matrixData);

		// One more with three rows, two columns
		double[][] matrixData2 = { {1d,2d}, {2d,5d}, {1d, 7d}};
		RealMatrix n = new Array2DRowRealMatrix(matrixData2);

		// Note: The constructor copies  the input double[][] array.

		// Now multiply m by n
		RealMatrix p = m.multiply(n);
		System.out.println(p.getRowDimension());    // 2
		System.out.println(p.getColumnDimension()); // 2

		// Invert p, using LU decomposition
		RealMatrix pInverse = new LUDecompositionImpl(p).getSolver().getInverse();
		System.out.println(pInverse.toString()); 
		
  		//assertTrue(NumericUtil.equals(eqn.getXi(4), 2.8 ));
  		//assertTrue(NumericUtil.equals(eqn.getXi(5), 0.4 ));
   }	
}
