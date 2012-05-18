package org.interpss.ed.sample;

import org.interpss.ed.IBCoeffMatrix;

public class SampleBCoeffMatrixImpl implements IBCoeffMatrix {
	public double b00;
	public double [] b0;
	public double [][] b;

	public SampleBCoeffMatrixImpl(int ngen) {
		this.b0 = new double[ngen]; 
		this.b = new double[ngen][ngen]; 
	}
	
	@Override
	public double getB(int i, int j) {
		return b[i][j];
	}

	@Override
	public double getB0(int i) {
		return b0[i];
	}

	@Override
	public double getB00() {
		return b00;
	}
}
