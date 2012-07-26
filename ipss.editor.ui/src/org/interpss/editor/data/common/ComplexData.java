 /*
  * @(#)ZData.java   
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

package org.interpss.editor.data.common;

import org.apache.commons.math3.complex.Complex;
import org.interpss.db.BaseDataBean;

/**
*	For persist Complex data object.
*/

public class ComplexData extends BaseDataBean {
	private static final long serialVersionUID = 1;

	private double re = 0.0;
	private double im = 0.0;

    public ComplexData() { }	
    public ComplexData(double re, double im) { this.re = re; this.im = im;}
    public ComplexData(Complex c) { this.re = c.getReal(); this.im = c.getImaginary();}

    public double getRe() {
		return re;
	}
	public void setRe(double re) {
		this.re = re;
	}
	public double getIm() {
		return im;
	}
	public void setIm(double im) {
		this.im = im;
	}	
	
	public Complex createComplex() {
		return new Complex(this.re, this.im);
	}
}