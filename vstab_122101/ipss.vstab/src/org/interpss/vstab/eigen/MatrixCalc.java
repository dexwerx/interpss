package org.interpss.vstab.eigen;

import Jama.Matrix;
import org.apache.commons.math.linear.*;
import org.apache.commons.math.util.MathUtils;


public class MatrixCalc {
	    public static double maxAbs(RealMatrix a){
		
    	// 
	    	  double max =0;
			  for  (int i =0; i< a.getRowDimension();i++){
				   for (int j =0;j< a.getColumnDimension();j++){
					   if (Math.abs(a.getEntry(i, j))>max) max=Math.abs(a.getEntry(i, j));
				    }
			  }
			   return max;
	   } //end of max method
	   
	   
	  public  static double getMinAbs(RealMatrix a){
		  double min =Math.abs(a.getEntry(0, 0));
		  for (int i =0; i< a.getRowDimension();i++){
			   for(int j =0;j< a.getColumnDimension();j++){
				   if (Math.abs(a.getEntry(i, j))<min) min =Math.abs(a.getEntry(i, j));		   
			   }
		   }
		   return min;
	   }  
	  public  static int getLMinIdx(RealMatrix a){
		  double min =Math.abs(a.getEntry(0, 0));
		  double  t;
		  int col=0; 
		  for (int i =0; i< a.getRowDimension();i++){
			        t=Math.abs(a.getEntry(i, i));
				   if (t<min) {min =t;	col =i;}	   
			       
		   }
		   return col;
	   }
	  public static  RealMatrix getDiag(RealMatrix a){
		  int rows=a.getRowDimension(); // assume it is a n*n matrix 
		  RealMatrix tempM=new Array2DRowRealMatrix(rows,1); // return a n*1 matrix saving the diag element ;
		
		  for (int i =0; i< a.getRowDimension();i++){
				   tempM.setEntry(i, 0,a.getEntry(i, i));		   
		   }
		   return tempM;
	   }
	  
      public static double sumOfElement(RealMatrix m){
		 double sum=0;
		    for (int i= 0;i<m.getRowDimension();i++){
				 for (int j= 0;j<m.getColumnDimension();j++){
					 sum+=m.getEntry(i, j);
				   }
		     }
		 return sum;
	 }
  	public static double getEigValueMin(RealMatrix jacobi){
		 
		 double eig_Min =99;// chosen by ramdom ,just make sure it is large enough
		 int col =0;
		 double[] realEigenValues=null;
		 EigenDecomposition eigDcp=null;
		 
		 try{
	     eigDcp  =new EigenDecompositionImpl(jacobi,MathUtils.SAFE_MIN);
	     realEigenValues=eigDcp.getRealEigenvalues();
	     
		 // search the zero eigen value and its index 
		 eig_Min = Math.abs(realEigenValues[0]);

		 for (int i=1;i<realEigenValues.length;i++){
		    if (eig_Min > Math.abs(realEigenValues[i])) { 
		       eig_Min =Math.abs(realEigenValues[i]); 
		       col =i;
		    } //end of if
		 } //end of for
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return realEigenValues[col];
	}

	    
	 public static void printMatrix(RealMatrix m){
		   
			 for (int i= 0;i<m.getRowDimension();i++){
				 
				 for (int j= 0;j<m.getColumnDimension();j++){
					 System.out.print(m.getEntry(i, j)+"  ");
				 } 
				 System.out.print("\n");
			 }  // end of for i
		}  // end of printMatrix
	 
}
