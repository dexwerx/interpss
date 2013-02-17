package org.interpss.numeric.sparse.base;

import org.apache.commons.math3.complex.Complex;
import org.interpss.numeric.datatype.Matrix_xy;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;

public class BaseSparseEqnMatrix2x2CSparseJImpl extends BaseSparseEqnDblCSparseJImpl implements SparseEqnMatrix2x2{

	
	public BaseSparseEqnMatrix2x2CSparseJImpl(){
		super();
	}
	public BaseSparseEqnMatrix2x2CSparseJImpl(int n){
		super(2*n);
	}
	
	/**
	 * set matrix dimension
	 * 
	 * @param n dimension
	 */
	@Override public void setDimension( final int n ) 	{
		super.setDimension(2*n);
	}
	
	/**
	 * add the matrix element m to A[i,j]
	 * 
	 * @param m matrix element to add
	 * @param i row
	 * @param j column
	 */
	@Override public void addToA( final Matrix_xy m, final int i, final int j )	{
		if ( m.xx != 0.0 ) 
			super.addToAij( m.xx, 2*i,   2*j );
		if ( m.xy != 0.0 ) 
			super.addToAij( m.xy, 2*i,   2*j+1 );
		if ( m.yx != 0.0 ) 
			super.addToAij( m.yx, 2*i+1, 2*j );
		if ( m.yy != 0.0 ) 
			super.addToAij( m.yy, 2*i+1, 2*j+1 );
	}

	/**
	 * get X[i] after the solution
	 * 
	 * @param i row 
	 */
	@Override public Vector_xy getX( final int i ) {
		return new Vector_xy( getXi(2*i), getXi(2*i+1) );
	}

	/**
	 * set the matrix element m to A[i,j]
	 * 
	 * @param m matrix element to set
	 * @param i row
	 * @param j column
	 */
	@Override public void setA( final Matrix_xy m, final int i, final int j ) {
		if ( m.xx != 0.0 ) 
			super.setAij( m.xx, 2*i,   2*j );
		if ( m.xy != 0.0 ) 
			super.setAij( m.xy, 2*i,   2*j+1 );
		if ( m.yx != 0.0 ) 
			super.setAij( m.yx, 2*i+1, 2*j );
		if ( m.yy != 0.0 ) 
			super.setAij( m.yy, 2*i+1, 2*j+1 );
	}

	/**
	 * get the matrix element A[i,j]
	 * 
	 * @param i row
	 * @param j column
	 */
	@Override public Matrix_xy getA(final int i, final int j ) {
		Matrix_xy m = new Matrix_xy();
		m.xx = super.getAij(2*i,   2*j );
		m.xy = super.getAij(2*i,   2*j+1 );
		m.yx = super.getAij(2*i+1, 2*j );
        m.yy = super.getAij(2*i+1, 2*j+1 );
		return m;
	}
	
	/**
	 * set B[i]
	 * 
	 * @param c the value to be set
	 * @param i
	 */
	@Override public void setB( final Complex c, final int i ) {
		setBi(c.getReal(), 2*i);
		setBi(c.getImaginary(), 2*i+1);
	}

	/**
	 * add to B[i]
	 * 
	 * @param c the value to be added
	 * @param i
	 */
	@Override public void addToB( final Complex c, final int i ) {
		addToBi(c.getReal(), 2*i);
		addToBi(c.getImaginary(), 2*i+1);
	}
	
	/**
	 * set B[i]
	 * 
	 * @param bi the value to be set
	 * @param i
	 */
	@Override public void setB( final Vector_xy bi, final int i ) {
		setBi(bi.x, 2*i);
		setBi(bi.y, 2*i+1);
	}
	
	/**
	 * add to B[i]
	 * 
	 * @param c the value to be added
	 * @param i
	 */
	@Override public void addToB( final Vector_xy bi, final int i ) {
		addToBi(bi.x, 2*i);
		addToBi(bi.y, 2*i+1);
	}	
	
	@Override public int getZeroA_row() {
		throw new RuntimeException("The getZeroA_row() does not apply to the sparse eqn");
	}

}
