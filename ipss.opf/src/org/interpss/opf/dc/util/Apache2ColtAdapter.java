package org.interpss.opf.dc.util;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

public class Apache2ColtAdapter {
	
	public static DoubleMatrix2D trans(Array2DRowRealMatrix param2DMatrix){
		int row =param2DMatrix.getRowDimension();
		int col=param2DMatrix.getColumnDimension();
		DoubleMatrix2D matrix=new DenseDoubleMatrix2D(row,col);
		matrix.assign(param2DMatrix.getData());
		return matrix;
	}
	
	public static DoubleMatrix1D trans(ArrayRealVector paramRealVector){
		DoubleMatrix1D matrix =new DenseDoubleMatrix1D(paramRealVector.getDimension());
		matrix.assign(paramRealVector.toArray());
		return matrix;
	}
}
