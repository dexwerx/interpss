 /*
  * @(#)CostFuncPiecewizeIO.java   
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

/*
	Stands for piecewize Input/Output curve. Here the unit's heat rate curve
      is represented as a series of points connected by straight line segments.
      The number of segments in the curve is determined by the order parameter.
      Each point requires a MW value and a heat rate value.
      The input file then must contain points such as the table below:

          MW | Inc. Heat Rate
       -----------------------
         P1  |  HR1
         P2  |  HR2
         P3  |  HR3
         P4  |  HR4
           This example has an order of 3 since the four points specify
           three segments.

      The user is cautioned that the first point's MW value ought to be equal 
      to the unit low limit and the last MW point equal to the unit high limit.
      In addition:

        P(I) < P(I+1)        
        HR(I) < HR(I+1)   
      
      for all points in the curve.
 */
public class CostFuncPiecewizeIO  extends AbstractCostFunc {
	private double[] ioMwPointAry, ioCostAry;

	public CostFuncPiecewizeIO(int order) {
		this.curveOrder = order;
		this.ioMwPointAry = new double[order+1];
		this.ioCostAry = new double[order+1];
	}
	
	@Override
	public double getIhrmax(double pmax) {
		double max = 0.0;
	    return max;
	}

	@Override
	public double getIhrmin(double pmin) {
		double min = 0.0;
	    return min;
	}	

	public void setIoMwPoint(int order, double c) throws Exception {
		if (order <= this.curveOrder)
			this.ioMwPointAry[order] = c;
		else
			throw new Exception("IoMwPoint order out of range, order: " + order);
	}
	
	public void setIoCost(int order, double c) throws Exception {
		if (order <= this.curveOrder)
			this.ioCostAry[order] = c;
		else
			throw new Exception("IoCost order out of range, order: " + order);
	}
	
	@Override
	public double incHeatRate(double unitMw) {
		double unitihr = 0.0;
	    int j = 0;
	    do {
	      j++;
	    } while ( ioMwPointAry[j] <= unitMw && j < curveOrder);

	    unitihr = (ioCostAry[j] - ioCostAry[j-1] ) /
	                          ( ioMwPointAry[j] - ioMwPointAry[j-1] );
	    return unitihr;
	}
	
	@Override
	public double inverserIhr(double unitIhr, double pmax, double pmin) throws Exception {
		if ( unitIhr >= maxIhr)
			return pmax;
		if ( unitIhr <= minIhr)
			return pmin;
		
		if ( unitIhr >= maxIhr)
			return pmax;
		if ( unitIhr <= minIhr)
			return pmin;
		
		int j = 0;
		double segmentihr = 0.0;
		do {
	    	j++;
	    	if ( j == curveOrder )
	    		return ioMwPointAry[j];
	    	segmentihr =(ioCostAry[j] - ioCostAry[j-1]) /
	                           (ioMwPointAry[j] - ioMwPointAry[j-1]);
	    } while ( segmentihr < unitIhr);

	    return ioMwPointAry[j-1];
	}

	@Override
	public double productionCost(double unitMw) {
		double unitcost = 0.0;
		for ( int j = 1; j <= curveOrder; j++) {
			if (ioMwPointAry[j] > unitMw) {
	            double partmw = (unitMw-ioMwPointAry[j-1] ) /
	                              (ioMwPointAry[j]-ioMwPointAry[j-1] );
	            unitcost = ioCostAry[j-1] +
	                             ( ioCostAry[j]-ioCostAry[j-1] ) * partmw;
	            return unitcost * fuelCost;
			}
	        
			if ( j == curveOrder)  //  {Unit is at or above pmax}
	            return ioCostAry[j] * fuelCost;
		}		
		return unitcost;
	}
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "ioMwPointAry, ioCostAry : "; 
		for (int i = 0; i <= curveOrder; i++)
			str += ioMwPointAry[i] + ", " + ioCostAry[i] + ", "; 
		str += "\n"; 
		return str;
	}	
}
