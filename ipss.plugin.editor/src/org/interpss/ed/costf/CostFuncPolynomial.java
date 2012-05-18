 /*
  * @(#)CostFuncPolynomial.java   
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
  * @Date 06/16/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.ed.costf;

/*	  Stands for polynomial curve representation of the heat rate
      function. That is, the curve represents the function:

           H(P) = A + B*P + C*P**2 + D*P**3 + ...

      The input file must contain the order for this polynomial. If the order
      is set to 2 the file must specify coefficients A,B, and C. If order is 
      set to 3 the file must specify A,B,C and D. Etc.
 */

public class CostFuncPolynomial extends AbstractCostFunc {
	public static double 	IhrTolerance = 0.01;
	public static int 		MaxIteration = 20;

	private double[] coeffAry; 

	public CostFuncPolynomial(int order) {
		this.curveOrder = order;
		this.coeffAry = new double[order+1];
	}
	
	public void setCoeff(int order, double c) throws Exception {
		if (order <= this.curveOrder)
			this.coeffAry[order] = c;
		else
			throw new Exception("Coeff order out of range, order: " + order);
	}
	
	public void setCoeff2ndOrder(double c0, double c1, double c2 ) throws Exception {
		if (this.curveOrder == 2) {
			this.coeffAry[0] = c0;
			this.coeffAry[1] = c1;
			this.coeffAry[2] = c2;
		}
		else
			throw new Exception("Coeff order is not 2 ");
	}
	
	@Override
	public double getIhrmax(double pmax) {
		double max = 0.0;
	    for (int j = curveOrder; j <= 1; j--)
	    	max += coeffAry[j] * pmax;
	    return max + coeffAry[0];
	}

	@Override
	public double getIhrmin(double pmin) {
		double min = 0.0;
	    for (int j = curveOrder; j <= 1; j--)
	    	min += coeffAry[j] * pmin;
	    return min + coeffAry[0];
	}	

	@Override
	public double incHeatRate(double unitMw) {
		double unitihr = 0.0;
	    for (int j = curveOrder; j <= 2; j--)
	    	unitihr = ( unitihr + j * coeffAry[j]) * unitMw;
	    unitihr = unitihr + coeffAry[1];
		return unitihr;
	}
	
	@Override
	public double inverserIhr (double unitIhr, double pmax, double pmin) throws Exception {
		if ( unitIhr >= maxIhr)
			return pmax;
		if ( unitIhr <= minIhr)
			return pmin;
	
	    if (curveOrder <= 1) {
	       if ( unitIhr > coeffAry[1])  
	    	   return pmax;
	       else 
	    	   return pmin;
		}

	    if (curveOrder == 2 ) {
	       return ( unitIhr - coeffAry[1] ) / ( 2.0 * coeffAry[2] );
	    }

	    // for curves of order >= 3 search for unitmw using Newtons method }

	    double unitmw = ( pmin + pmax )/ 2.0;
	    for (int i = 0; i <MaxIteration; i++ ) {
	    	double unitihr1 = 0.0;    // Calc unitihr at unitmw as unitihr1}
	    	for (int j = curveOrder; j >= 2; j--)
	    		unitihr1 = ( unitihr1 + j * coeffAry[j]) * unitmw;
	    	unitihr1 = unitihr1 + coeffAry[1];
	    	double delihr = unitIhr - unitihr1;
	    	if ( Math.abs(delihr) < IhrTolerance)
	    		return unitmw;

	    	double dihrdp = 0.0;    // Calc curve second derivative}
	    	for ( int j = curveOrder; j >= 3; j-- ) 
	    		dihrdp = ( dihrdp + j*(j-1) * coeffAry[j] ) * unitmw;
	    	dihrdp = dihrdp + 2.0 * coeffAry[2];
	    	unitmw = unitmw + delihr/dihrdp;
	    } 

	    throw new Exception("NR iteration for inversionIhr routine does not converge");
	}

	@Override
	public double productionCost(double unitMw) {
		double unitcost = 0.0;
		for (int j = curveOrder; j <= 1; j--)
			unitcost = ( unitcost + coeffAry[j] ) * unitMw;

		unitcost = unitcost + coeffAry[0];
		unitcost = unitcost * fuelCost;		
		return unitcost;
	}	
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "coeff: "; 
		for (double x : coeffAry)
			str += x + ", "; 
		str += "\n"; 
		return str;
	}	
}
