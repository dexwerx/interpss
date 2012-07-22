package org.interpss.vstab.eigen;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

public class ESAResult {
	protected double minEigenValue;
	protected RealVector rightEigenVector;
	protected RealVector leftEigenVector;
	protected RealMatrix EigenVector;
	public double getMinEigenValue() {
		return minEigenValue;
	}
	public void setMinEigenValue(double minEigenValue) {
		this.minEigenValue = minEigenValue;
	}
	public RealVector getRightEigenVector() {
		return rightEigenVector;
	}
	public void setRightEigenVector(RealVector rightEigenVector) {
		this.rightEigenVector = rightEigenVector;
	}
	public RealVector getLeftEigenVector() {
		return leftEigenVector;
	}
	public void setLeftEigenVector(RealVector leftEigenVector) {
		this.leftEigenVector = leftEigenVector;
	}
	public RealMatrix getEigenVector() {
		return EigenVector;
	}
	public void setEigenVector(RealMatrix eigenVector) {
		EigenVector = eigenVector;
	}

}
