package org.interpss.numeric.sparse.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.base.AbstractSparseEqnDouble;

import edu.emory.mathcs.csparsej.tdouble.Dcs_compress;
import edu.emory.mathcs.csparsej.tdouble.Dcs_entry;
import edu.emory.mathcs.csparsej.tdouble.Dcs_load;
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

	int order = 2; //default amd(A+A')
	
	/*define the data structure*/
	
	//right hand side vector b
	double[] b;
	//solution result vector x;
	double[] x;
    //Sparse matrix object defined in CsparseJ
	Dcs A; 
	
	public SparseEqnDoubleCSparseJImpl(){
		/* allocate space 
		 * The matrix is initialized in triplet form (i,j,aij)
		 * by setting triplet=true in the cs_spalloc() method;
		 * */
		
		A = Dcs_util.cs_spalloc(0, 0, 1, true, true); 
		
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
	/**
	 * 
	 * @param file
	 * @param base -- by default, matrix index base =0, sometimes, the index base is 1;
	 * @return
	 * @throws FileNotFoundException
	 */
	public boolean loadTripletMatrixData(String file, int base) throws FileNotFoundException{
		A=Dcs_load.cs_load(new FileInputStream(new File(file)),base);
		if(A == null) {
			return false;
		}
		if(A.m != A.n){
			System.err.print("The input matrix is not a square matrix!");
		}
		setDimension(A.n);
		b= new double[A.n];
		x= new double[A.n];
		return true;
	}
	
    
	
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
        /*Consider the storage scheme of A
		  if A is in the triplet form, it needs to iterate the whole matrix 
		  for the worst case to get Aij and update it. 
		  Further consideration is that such an update operation is usually 
		  done after loading data into the matrix or after matrix initialization,
		  Therefore, we transform it to the compressed column form first.
		*/
		if(Dcs_util.CS_TRIPLET(A)){
			
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
		if(Dcs_util.CS_TRIPLET(A)){
			//transform it to the compressed column form first
			A= Dcs_compress.cs_compress(A);
		}
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

	@Override 
	public void setAij(double x, int i, int j) {
		try {
		    if(!Dcs_util.CS_TRIPLET(A))
			   throw new Exception("Trying to set a new Aij, but the underlying Csparse Matrix is not in triplet form!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		//ONLY works for triplet form storage scheme;
		Dcs_entry.cs_entry(A, i, j, x);
	}

	@Override 
	public void setBi(double bi, int i) {
		b[i]=bi;
	}
	
	@Override public void setB2Unity(int i) {
			b[i]=1.0;
	}

	@Override public void setB2Zero() {
		for(int k =0 ; k<b.length;k++){
			b[k]=0.0;
		}
	}

	
	@Override
	public void solveEqn() {
		/*
		 * NOTE:
		 1) partial pivoting is used by setting tol=1.0
		 2)since b vector is overwritten with the solution in the cs_lusol(), 
		   first set let x=b and use x in the lusol() instead of b;
		 */
		if (!Dcs_util.CS_CSC(A)){
			A= Dcs_compress.cs_compress(A);
		}
		x=b;
		Dcs_lusol.cs_lusol(order, A, x, 1.0);
		
	}
	
	@Override
	public boolean luMatrixAndSolveEqn( final double tolerance)  throws IpssNumericException {
		boolean ok=false;
		//if the matrix is small, use natural ordering;
		if(this.getDimension()<1000){
			ok=luAndSolveWithOrdering(0);
		}
		else{
			ok=luAndSolveWithOrdering(2);
		}
			
		return ok;
	}
	/**
	 * @param order
     *            0:natural, 1:Chol, 2:LU, 3:QR
	 *
	 */
	public boolean luAndSolveWithOrdering(int orderScheme) {
		if (!Dcs_util.CS_CSC(A)){
			A= Dcs_compress.cs_compress(A);
		}
		x=b;
		boolean ok=Dcs_lusol.cs_lusol(orderScheme, A, x, 1.0);
		return ok;
	}
	
}
