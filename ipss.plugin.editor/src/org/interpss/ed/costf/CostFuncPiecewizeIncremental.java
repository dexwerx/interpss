 /*
  * @(#)CostFuncPiecewizeIncremental.java   
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
	Stands for piecewize incremental heat rate curve. The curve is 
      represented by a series of points connected by straight line segments.
      The number of segments in the curve is determined by the order parameter.
      Each point requires a MW value and an incremental heat rate value.
      The input file then must contain points such as the table below:

          MW | Inc. Heat Rate
       -----------------------
         P1  |  IHR1
         P2  |  IHR2
         P3  |  IHR3
           This example has an order of 2 since the three points specify
           two segments.

      The user is cautioned that the first point's MW value ought to be equal 
      to the unit low limit and the last MW point equal to the unit high limit.
      In addition:

        P(I) < P(I+1)        
        IHR(I) < IHR(I+1)   
      
      for all points in the curve.
 */
public class CostFuncPiecewizeIncremental extends AbstractCostFunc {
	private double minPut = 0.0;
	private double[] ihrMwPointAry, ihrCostAry;

	public CostFuncPiecewizeIncremental(int order) {
		this.curveOrder = order;
		this.ihrMwPointAry = new double[order+1];
		this.ihrCostAry = new double[order+1];
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

	public void setMinPut(double c) {
		this.minPut = c;
	}	
	
	public void setIhrMwPoint(int order, double c) throws Exception {
		if (order <= this.curveOrder)
			this.ihrMwPointAry[order] = c;
		else
			throw new Exception("IhrMwPoint order out of range, order: " + order);
	}
	
	public void setIhrCost(int order, double c) throws Exception {
		if (order <= this.curveOrder)
			this.ihrCostAry[order] = c;
		else
			throw new Exception("IhrCost order out of range, order: " + order);
	}
	
	@Override
	public double incHeatRate(double unitMw) {
		double unitihr = 0.0;
	    int  j = 0;
	    do {
	      j++;
	    } while (ihrMwPointAry[j] <= unitMw && j < this.curveOrder);

	    double partmw = (unitMw - ihrMwPointAry[j-1] )/
	                       (ihrMwPointAry[j] - ihrMwPointAry[j-1] );
	    unitihr = ihrCostAry[j-1] + ( ihrCostAry[j] - ihrCostAry[j-1] ) * partmw;
		return unitihr;
	}
	
	@Override
	public double inverserIhr(double unitIhr, double pmax, double pmin) throws Exception  {
		if ( unitIhr >= maxIhr)
			return pmax;
		if ( unitIhr <= minIhr)
			return pmin;
		
		double unitmw = 0.0;
		int j = 0 ;
	    do {
	      j++;
	    } while (ihrCostAry[j] <= unitIhr && j < curveOrder);

	    double partihr = ( unitIhr - ihrCostAry[j-1] )/
	                          ( ihrCostAry[j] - ihrCostAry[j-1] );
	    unitmw = ihrMwPointAry[j-1] +
	                    ( ihrMwPointAry[j] - ihrMwPointAry[j-1] ) * partihr;
		return unitmw;
	}

	@Override
	public double productionCost(double unitMw) {
		double unitcost = 0.0;
	    unitcost = minPut * fuelCost;
	    for (int j = 1; j <= curveOrder; j++) {
	    	if (unitMw > ihrMwPointAry[j] && j < curveOrder ) { // Calculate area under complete segment
	            double segmentcost =  ( (ihrCostAry[j] + ihrCostAry[j-1] )/2.0 ) *
	                            ( ihrMwPointAry[j] - ihrMwPointAry[j-1] ) * fuelCost;
	            unitcost += segmentcost;
	    	}
	    	else { // {Calculate area under partial segment}
	            double partmw = (unitMw - ihrMwPointAry[j-1] )/
	                            (ihrMwPointAry[j] - ihrMwPointAry[j-1] );
	            double unitihr = ihrCostAry[j-1] + ( ihrCostAry[j] - ihrCostAry[j-1] ) * partmw;
	            double segmentcost =  ( (unitihr + ihrCostAry[j-1])/ 2.0 ) *
	                             ( unitMw - ihrMwPointAry[j-1] ) * fuelCost;
	            unitcost += segmentcost;
	    	}
	    }
	    return unitcost;
	}
	
	@Override
	public String toString() {
		String str = super.toString();
		str += "ihrMwPointAry, ihrCostAry : "; 
		for (int i = 0; i <= curveOrder; i++)
			str += ihrMwPointAry[i] + ", " + ihrCostAry[i] + ", "; 
		str += "\n"; 
		str += "minPut: " + minPut + "\n";
		return str;
	}	
}
