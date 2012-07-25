/*
  * @(#)DblBranchValue.java   
  *
  * Copyright (C) 2006-2011 www.interpss.com
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
  * @Date 04/15/2009
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.datatype;

import com.interpss.core.aclf.AclfBranch;

/**
 * A branch data object of type double
 * 
 * @author mzhou
 *
 */
public class DblAryBranchValue extends BranchValueBase {
	public double[] aryValue;
	
	public DblAryBranchValue(double[] x) {
		this.aryValue = x;
	}
	
	public DblAryBranchValue(String id, double[] x) {
		this.id = id;
		this.aryValue = x;
	}	
	
	public DblAryBranchValue(AclfBranch b, double[] x) {
		this.branch = b;
		this.id = b.getId();
		this.aryValue = x;
	}	

	public DblAryBranchValue(int size) {
		this.aryValue = new double[size];
	}
	
	public DblAryBranchValue(String id, int size) {
		this.id = id;
		this.aryValue = new double[size];
	}	
	
	public DblAryBranchValue(AclfBranch b, int size) {
		this.branch = b;
		this.id = b.getId();
		this.aryValue = new double[size];
	}	
}
