 /*
  * @(#)Test_IEEECommonFormat.java   
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

import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SparseRealMatrix;
import org.interpss.CorePluginObjFactory;
import org.interpss.CorePluginTestSetup;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.numeric.datatype.Matrix_xy;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;
import org.junit.Test;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.numeric.sparse.csj.SparseEqnMatrix2x2CSparseJImpl;

public class UCTE2000SparseMatrixCasesTest extends CorePluginTestSetup {
	//@Test 
	public void testCase1() throws Exception {
		AclfNetwork net = CorePluginObjFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testData/ieee_format/UCTE_2000_WinterOffPeak.ieee")
				.getAclfNet();
		
		SparseEqnMatrix2x2 eqn = net.formJMatrix();
		
		RealMatrix m = sparseMatrix2Ary(eqn);

		double[] b = new double[m.getRowDimension()];
		for ( int i = 0; i < m.getRowDimension(); i++)
			b[i] = 1.0;

	  	System.out.println("Common Math ... ");
		long starttime = System.currentTimeMillis() ;
		LUDecomposition lu = new LUDecomposition(m);
		RealVector result = lu.getSolver().solve(new ArrayRealVector(b));
	  	System.out.println("time for full matrix : " + (System.currentTimeMillis() - starttime)*0.001);
		
//		int cnt = 0;
//		for (double d : result)
//			System.out.println(cnt++ + ", " + d);
		
		int n =  eqn.getDimension()/2;
		for (int i = 0; i < n; i++) 
			eqn.setB(new Vector_xy(1.0,1.0), i+1);
		
		
	  	System.out.println("Interpss ... ");
		starttime = System.currentTimeMillis() ;
		eqn.luMatrixAndSolveEqn(1.0e-20);
	  	System.out.println("time for spase matrix : " + (System.currentTimeMillis() - starttime)*0.001);
		
		int cnt = 0;
		for (int i = 0; i < n; i++) { 
			Vector_xy xy = eqn.getX(i+1);
			//System.out.println(cnt++ + ", " + xy.x);
			//System.out.println(cnt++ + ", " + xy.y);
	  		assertTrue(Math.abs(result.getEntry(cnt++) - xy.x ) < 0.0001);
	  		assertTrue(Math.abs(result.getEntry(cnt++) - xy.y ) < 0.0001);
		}
		
		/*
		J-matrix Dimension:2508
		time for full matrix : 54.321
		time for sparse matrix : 0.197
		 */
	}
	
	@Test 
	public void testCase2() throws Exception {
		AclfNetwork net = CorePluginObjFactory
				.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
				.load("testData/ieee_format/UCTE_2000_WinterOffPeak.ieee")
				.getAclfNet();
		
		SparseEqnMatrix2x2 eqn = net.formJMatrix();
		
		SparseEqnMatrix2x2CSparseJImpl csj= createCSparseJMatrix(eqn);

		
		for ( int i = 0; i < csj.getDimension(); i++)
			csj.setB2Unity(i);

	  	System.out.println("CSparseJ ... ");
		long starttime = System.currentTimeMillis() ;
		csj.luMatrixAndSolveEqn(0.0001);
	  	System.out.println("time for CspareJ : " + (System.currentTimeMillis() - starttime)*0.001);
		

		
		int n =  eqn.getDimension()/2;
		for (int i = 0; i < n; i++) 
			eqn.setB(new Vector_xy(1.0,1.0), i);
		
		
	  	System.out.println("Interpss ... ");
		starttime = System.currentTimeMillis() ;
		eqn.luMatrixAndSolveEqn(1.0e-20);
	  	System.out.println("time for InterPSS spase matrix : " + (System.currentTimeMillis() - starttime)*0.001);
		
		int cnt = 0;
		for (int i = 0; i < n; i++) { 
			Vector_xy xy = eqn.getX(i);
			//System.out.println(cnt++ + ", " + xy.x);
			//System.out.println(cnt++ + ", " + xy.y);
	  		assertTrue(Math.abs(csj.getX(i).x - xy.x ) < 0.0001);
	  		assertTrue(Math.abs(csj.getX(i).y - xy.y ) < 0.0001);
		}
		
		/*
		J-matrix Dimension:2508
		time for full matrix : 54.321
		time for sparse matrix : 0.197
		 */
	}
	
	
	
	private SparseRealMatrix sparseMatrix2Ary(SparseEqnMatrix2x2 eqn){
		int n = eqn.getDimension();
		System.out.println("J-matrix Demension:" + n);
		//RealMatrix m = new Array2DRowRealMatrix( n, n );
		SparseRealMatrix m = new OpenMapRealMatrix( n, n );
		int n_2 = n / 2;
		for(int i=0; i< n_2; i++) { // index 1-N
			for(int j=0; j < n_2; j++) {//index 1-N
				Matrix_xy mxy=eqn.getA(i+1, j+1);
				if(mxy.xx != 0.0)
					m.setEntry(2*i,   2*j,   mxy.xx);
				if(mxy.xy != 0.0)
					m.setEntry(2*i,   2*j+1, mxy.xy);
				if(mxy.yx != 0.0)
					m.setEntry(2*i+1, 2*j,   mxy.yx);
				if(mxy.yy != 0.0)
					m.setEntry(2*i+1, 2*j+1, mxy.yy);
			}
		}
		return m;
	}
	
	private SparseEqnMatrix2x2CSparseJImpl createCSparseJMatrix(SparseEqnMatrix2x2 eqn){
		int n = eqn.getDimension();
		
		SparseEqnMatrix2x2CSparseJImpl csj = new SparseEqnMatrix2x2CSparseJImpl(n/2);
		int n_2 = n / 2;
		for(int i=0; i< n_2; i++) { // index 1-N
			for(int j=0; j < n_2; j++) {//index 1-N
				csj.setA(eqn.getA(i, j),i, j);
				
			}
		}
		return csj;
		
	}
	
}

