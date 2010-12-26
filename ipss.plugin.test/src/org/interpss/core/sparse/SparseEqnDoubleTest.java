 /*
  * @(#)SparseEqnDoubleTest.java   
  *
  * Copyright (C) 2006 www.interpss.org
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

package org.interpss.core.sparse;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.LUDecomposition;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.sparse.dep.SparseEqnDouble;
import com.interpss.core.sparse.dep.impl.SparseEqnDoubleImpl;

/**
 * @author mzhou
 *
 */
public class SparseEqnDoubleTest {
	@Test
	public void testProfWuSample1() throws InterpssException {
		/*
		a(1,1): 1.0

		a(2,2): 1.0

		a(3,3): 15.0
		a(3,6): -5.0

		a(4,4): 15.0
		a(4,5): -5.0

		a(5,5): 0.0
		a(5,6): 5.0
		a(5,3): -5.0

		a(6,6): 0.0
		a(6,4): 1.0
		 */
		RealMatrix m = new Array2DRowRealMatrix(6, 6);
		m.setEntry(0, 0, 1.0);
		m.setEntry(1, 1, 1.0 );
		m.setEntry(2, 2, 15.0 );
		m.setEntry(2, 5, -5.0);
		m.setEntry(3, 3, 15.0 );
		m.setEntry(3, 4, -5.0);
		m.setEntry(4, 5, 5.0);
		m.setEntry(4, 2, -5.0);
		m.setEntry(5, 3, 1.0);
		
		LUDecomposition lu = new LUDecompositionImpl(m);
		double[] result = lu.getSolver().solve(new double [] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0});
		//for (double d : result)
		//	System.out.print(d + ",");
		
// 1.0,1.0,0.2,1.0000000000000002,2.8000000000000003,0.39999999999999997
		assertTrue(Math.abs(result[4] - 2.8) < 0.00001);
   }	
	@Test
	public void testProfWuSample2() throws InterpssException {
		SparseEqnDouble eqn = new SparseEqnDoubleImpl(6);
		/*
		a(1,1): 1.0

		a(2,2): 1.0

		a(3,3): 15.0
		a(3,6): -5.0

		a(4,4): 15.0
		a(4,5): -5.0

		a(5,5): 0.0
		a(5,6): 5.0
		a(5,3): -5.0

		a(6,6): 0.0
		a(6,4): 1.0
		 */
		eqn.setAij( 1.0, 1, 1 );
		
		eqn.setAij( 1.0, 2, 2 );
		
		eqn.setAij( 15.0, 3, 3 );
		eqn.setAij( -5.0, 3, 6 );
		
		eqn.setAij( 15.0, 4, 4 );
		eqn.setAij( -5.0, 4, 5 );
		
		eqn.setAij( 0.0, 5, 5 );
		eqn.setAij( 5.0, 5, 6 );
		eqn.setAij( -5.0, 5, 3 );

		eqn.setAij( 0.0, 6, 6 );
		eqn.setAij( 1.0, 6, 4 );
		
		for (int i = 1; i <= 6; i++)
			eqn.setBi(1.0, i);
		
		eqn.luMatrixAndSolveEqn(1.0e-20);
		
		//System.out.println(eqn);
		assertTrue(Math.abs(eqn.getBi(5) - 2.8) < 0.00001);
   }	
}
