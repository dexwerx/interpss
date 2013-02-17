package org.interpss.numeric.sparse.base;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.datatype.Matrix_xy;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;

public class AbstractSparseEqnMatrix2x2 extends AbstractSparseEquation implements SparseEqnMatrix2x2{

	@Override
	public void addToA(Matrix_xy x, int i, int j) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector_xy getX(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setA(Matrix_xy x, int i, int j) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix_xy getA(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setB(Vector_xy bi, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToB(Vector_xy bi, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getZeroA_row() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setB(Complex c, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToB(Complex c, int i) {
		// TODO Auto-generated method stub
		
	}

}
