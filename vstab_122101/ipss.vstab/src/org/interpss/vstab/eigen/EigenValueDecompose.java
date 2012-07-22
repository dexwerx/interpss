package org.interpss.vstab.eigen;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;


public class EigenValueDecompose {
	
   protected EigenvalueDecomposition evd=null;
   protected RealMatrix diagD=null;
   protected RealMatrix eigenVectors=null;
   
   public  void EigenValueDecompose(Array2DRowRealMatrix matrix){
	   
	   Matrix m_JamaFmt=new Matrix(matrix.getData());
	   evd=new EigenvalueDecomposition(m_JamaFmt);
	   diagD=new Array2DRowRealMatrix(evd.getD().getArray());
	   eigenVectors=new Array2DRowRealMatrix(evd.getV().getArray());   
   }
   public RealMatrix getDiagD(){
	   return this.diagD;
   }
   public RealMatrix getEigenVectors(){
	   return this.eigenVectors;
   }

}
