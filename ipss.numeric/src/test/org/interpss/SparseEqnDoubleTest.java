 /*
  * @(#) SparseEqnDoubleTest.java   
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

import static org.junit.Assert.assertTrue;

import org.interpss.numeric.NumericObjectFactory;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.SparseEqnDouble;
import org.interpss.numeric.sparse.impl.SparseEqnDoubleCommonMathImpl;
import org.interpss.numeric.util.NumericUtil;
import org.interpss.spring.NumericSpringCtx;
import org.junit.Test;

/**
 * @author mzhou
 *
 */
public class SparseEqnDoubleTest {
	@Test
	public void test1()  throws IpssNumericException {
		SparseEqnDouble eqn = new SparseEqnDoubleCommonMathImpl(6);
		setEqnData(eqn);
		
		eqn.solveEqn();
		//for (int i = 0; i < 6; i++)
		//	System.out.print(eqn.getXi(i) + ", ");
		// 1.0, 1.0, 0.2, 1.0000000000000002, 2.8000000000000003, 0.39999999999999997, 
		
  		assertTrue(NumericUtil.equals(eqn.getXi(4), 2.8 ));
  		assertTrue(NumericUtil.equals(eqn.getXi(5), 0.4 ));
   }	

	@Test
	public void test2()  throws IpssNumericException {
		SparseEqnDouble eqn = NumericSpringCtx.getSparseEqnDouble();
		eqn.setDimension(6);
		setEqnData(eqn);
		
		eqn.solveEqn();
		
  		assertTrue(NumericUtil.equals(eqn.getXi(4), 2.8 ));
  		assertTrue(NumericUtil.equals(eqn.getXi(5), 0.4 ));
   }	

	@Test
	public void test3() throws IpssNumericException {
		SparseEqnDouble eqn = NumericObjectFactory.createSparseEqnDouble(6);
		setEqnData(eqn);
		
		eqn.solveEqn();
		
  		assertTrue(NumericUtil.equals(eqn.getXi(4), 2.8 ));
  		assertTrue(NumericUtil.equals(eqn.getXi(5), 0.4 ));
   }	

	private void setEqnData(SparseEqnDouble eqn) {
		eqn.setAij(1.0,  0, 0);
		eqn.setAij(1.0,  1, 1);
		eqn.setAij(15.0, 2, 2 );
		eqn.setAij(-5.0, 2, 5);
		eqn.setAij(15.0, 3, 3 );
		eqn.setAij(-5.0, 3, 4);
		eqn.setAij(5.0,  4, 5);
		eqn.setAij(-5.0, 4, 2);
		eqn.setAij(1.0,  5, 3);
		
		for (int i = 0; i < 6; i++)
			eqn.setBi(1.0, i);
   }	
}
