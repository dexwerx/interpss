/*
 * @(#)SparseEqnDoubleCommonMathImpl.java   
 *
 * Copyright (C) 2006-2010 www.interpss.com
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
 * @Date 12/10/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.numeric.sparse.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.LUDecomposition;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.interpss.numeric.sparse.base.AbstractSparseEqnDouble;

/**
 * Sparse Equation of data type double for solving the [A]X=B problem. 
   using the apache common math lib
 */

public class SparseEqnDoubleCommonMathImpl extends AbstractSparseEqnDouble {
	RealMatrix A = null;
	double[] B = null;
	double[] X = null;
	
	public SparseEqnDoubleCommonMathImpl() {
	}

	public SparseEqnDoubleCommonMathImpl(int n) {
		this.setDimension(n);
	}
	
	public void setDimension(int n) {
		super.setDimension(n);
		this.A = new Array2DRowRealMatrix( n, n );
		this.B = new double[n];
	}
	
	public void addToAij( final double x, final int i, final int j ) {
		double x0 = this.A.getEntry(i, j);
		this.A.setEntry(i, j, x0+x);
	}

	public void setAij( final double x, final int i, final int j ) {
		this.A.setEntry(i, j, x);
	}
		
	public double getAij( final int i, final int j ) {
		return this.A.getEntry(i, j);
	}

	public double getXi( final int i ) {
		return this.X[i];
	}
	
	@Override
	public void setBi( final double bi, final int i ) {
		this.B[i] = bi;
	}

	@Override
	public void addToBi( final double bi, final int i ) {
		this.B[i] += bi;
	}
	
	@Override
	public void solveEqn() {
		LUDecomposition lu = new LUDecompositionImpl(A);
		this.X = lu.getSolver().solve(B);
	}
	public String toString(){
		   
		   StringBuffer sb=new StringBuffer();
		   DecimalFormat format = new DecimalFormat();
		   format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		   format.setMinimumIntegerDigits(1);
		   format.setMaximumFractionDigits(3);
		   format.setMinimumFractionDigits(1);
		   for(int i=0;i<A.getRowDimension();i++) {
			   for(int j=0;j<A.getColumnDimension();j++) {
				   String s=format.format(A.getEntry(i, j));
				   sb.append(s);
				   int padding = Math.max(1,5-s.length()); // At _least_ 1 space
		           for (int k = 0; k < padding; k++) sb.append(" ");
		                    
			   }
			   sb.append("\n");
		   }

		   return sb.toString();
	}
}