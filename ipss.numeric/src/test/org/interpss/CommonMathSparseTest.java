 /*
  * @(#) CommonMathSparseTest.java   
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

import org.apache.commons.math.Field;
import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.fraction.FractionField;
import org.apache.commons.math.linear.FieldLUDecompositionImpl;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.OpenMapRealMatrix;
import org.apache.commons.math.linear.SparseFieldMatrix;
import org.apache.commons.math.linear.SparseRealMatrix;
import org.interpss.numeric.util.NumericUtil;
import org.junit.Test;

/**
 * @author mzhou
 *
 */
public class CommonMathSparseTest {
	@Test
	public void test1() {
        FieldMatrix<Fraction> coefficients = createSparseFieldMatrix();
        
        Fraction[] constants = { new Fraction(1), new Fraction(1), new Fraction(1), new Fraction(1), new Fraction(1), new Fraction(1)};
        
        Fraction[] solution = new FieldLUDecompositionImpl<Fraction>(coefficients).getSolver().solve(constants);
		//for (int i = 0; i < 6; i++)
		//	System.out.print(solution[i] + ", ");
		// 1, 1, 1 / 5, 1, 14 / 5, 2 / 5,
		// 1.0, 1.0, 0.2, 1.0000000000000002, 2.8000000000000003, 0.39999999999999997, 
		
  		assertTrue(NumericUtil.equals(solution[4].doubleValue(), 2.8 ));
  		assertTrue(NumericUtil.equals(solution[5].doubleValue(), 0.4 ));
   }	

	@Test
	public void test2() {
		SparseRealMatrix coefficients = createSparseRealMatrix();
        
        double[] constants = { 1,1,1,1,1,1};
        
        double[] solution = new LUDecompositionImpl(coefficients).getSolver().solve(constants);
		//for (int i = 0; i < 6; i++)
		//	System.out.print(solution[i] + ", ");
		// 1, 1, 1 / 5, 1, 14 / 5, 2 / 5,
		// 1.0, 1.0, 0.2, 1.0000000000000002, 2.8000000000000003, 0.39999999999999997,
		
  		assertTrue(NumericUtil.equals(solution[4], 2.8 ));
  		assertTrue(NumericUtil.equals(solution[5], 0.4 ));
   }	
	
	protected Field<Fraction> field = FractionField.getInstance();
	
    private SparseFieldMatrix<Fraction> createSparseFieldMatrix() {
    	SparseFieldMatrix<Fraction> m = new SparseFieldMatrix<Fraction>(field, 6, 6);

    	try {
    		m.setEntry(0, 0, new Fraction(1.0));
    		m.setEntry(1, 1, new Fraction(1.0) );
    		m.setEntry(2, 2, new Fraction(15.0) );
    		m.setEntry(2, 5, new Fraction(-5.0));
    		m.setEntry(3, 3, new Fraction(15.0) );
    		m.setEntry(3, 4, new Fraction(-5.0));
    		m.setEntry(4, 5, new Fraction(5.0));
    		m.setEntry(4, 2, new Fraction(-5.0));
    		m.setEntry(5, 3, new Fraction(1.0));
    	} catch (Exception e) {
    		
    	}
    	return m;
    }

    private SparseRealMatrix createSparseRealMatrix() {
    	SparseRealMatrix m = new OpenMapRealMatrix(6, 6);

    	try {
    		m.setEntry(0, 0, 1.0);
    		m.setEntry(1, 1, 1.0 );
    		m.setEntry(2, 2, 15.0 );
    		m.setEntry(2, 5, -5.0);
    		m.setEntry(3, 3, 15.0 );
    		m.setEntry(3, 4, -5.0);
    		m.setEntry(4, 5, 5.0);
    		m.setEntry(4, 2, -5.0);
    		m.setEntry(5, 3, 1.0);
    	} catch (Exception e) {
    		
    	}
    	return m;
    }
}
