package org.interpss.numeric.sparse.impl;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.interpss.numeric.sparse.base.AbstractSparseEqnDouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_compress;
import edu.emory.mathcs.csparsej.tdouble.Dcs_entry;
import edu.emory.mathcs.csparsej.tdouble.Dcs_lusol;
import edu.emory.mathcs.csparsej.tdouble.Dcs_util;
import edu.emory.mathcs.csparsej.tdouble.Dcs_common.Dcs;
/**
 * This is a sparse equation implementation based on CsparseJ, for details, 
 * please refer to the following website
 *   https://sites.google.com/site/piotrwendykier/software/csparsej
 *   https://github.com/rwl/CSparseJ
 *   
 * Sparse matrix  defined in CsparseJ is in compressed-column format
	 
	 int m - # of rows
	 int n - # of columns
	 int[] p - column pointer
	 int[] i - row indices
	 double[] x - numerical values, size nzmax
         
         
 * @author Tony Huang
 * @version 0.1
 */
public class SparseEqnDoubleCSparseJImpl extends AbstractSparseEqnDouble {
	double default_tolerance = 1.0E-20;
	int order = 1; //default amd(A+A')
	
	/*define the data structure*/
	
	//right hand side vector b
	double[] b;
	//solution result vector x;
	double[] x;
    //Sparse matrix object defined in CsparseJ
	Dcs A; 
	
	public SparseEqnDoubleCSparseJImpl(){
		A = Dcs_util.cs_spalloc(0, 0, 1, true, true); /* allocate space */
		
	}
	
	public SparseEqnDoubleCSparseJImpl(int n){
		super.setDimension(n);
		A = Dcs_util.cs_spalloc(n, n, n*n, true, true);
		b= new double[n];
		x= new double[n];
	}
	//*****************************************
	//part-1 data adapter
	//*****************************************
	
    
	
	
	//*****************************************
	//part-2 sparse matrix operation implementation
	//*****************************************
	@Override
	public void setDimension(int n) {
		super.setDimension(n);
        A.m=n;
        A.n=n;
	}
	
	@Override 
	public void addToAij(double x, int i, int j) {
        //Consider the storage scheme of A
		//if in the triplet form
		if(Dcs_util.CS_TRIPLET(A)){
			//transform it to the compressed column form first
			A= Dcs_compress.cs_compress(A);
		}
        for(int k=A.p[j]; k<A.p[j+1];k++){ // iterate through the column j
        	if(A.i[k]==i){ //find the entry in row i
        		//xij= xij_0+x;
        		A.x[k]=A.x[k]+x;
        	}
        }
         
	}

	@Override 
	public void addToBi(double bi, int i) {
		b[i]=b[i]+bi;
	}

	@Override 
	public double getAij(int i, int j) {
		double aij=0;
		for(int k=A.p[j]; k<A.p[j+1];k++){ // iterate through the column j
        	if(A.i[k]==i){ //find the entry in row i
        		aij=A.x[k];
        	}
        }
		return aij;
	}

	@Override 
	public double getXi(int i) {
		return x[i];
	}
    /**
     * 
     */
	@Override 
	public void setAij(double x, int i, int j) {
		try {
		    if(!Dcs_util.CS_TRIPLET(A))
			   throw new Exception("Trying to set a new Aij, but the underlying Csparse Matrix is not in triplet form!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		//ONLY works for triplet form matrix;
		Dcs_entry.cs_entry(A, i, j, x);
	}

	@Override 
	public void setBi(double bi, int i) {
		b[i]=bi;
	}
	
	@Override
	public void solveEqn() {
		/*
		 * NOTE:
		 1) partial pivoting is used by setting tol=1.0
		 2)since b is overwritten with the solution in the cs_lusol(), 
		   first set let x=b and use x in the lusol() instead of b;
		 */
		if (!Dcs_util.CS_CSC(A)){
			A= Dcs_compress.cs_compress(A);
		}
		x=b;
		Dcs_lusol.cs_lusol(order, A, x, 1.0);
		
	}
	
}
