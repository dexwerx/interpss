package org.interpss.vstab.eigen;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

import com.interpss.common.datatype.Matrix_xy;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.JacobianMatrixType;
import com.interpss.core.net.Bus;
import com.interpss.core.sparse.SparseEqnMatrix2x2;
/*
 * still some problem for this conversation ,
 * cau'z the dimension and index of complex matrix is not so clear 
 *
 */
public class JacobiMatrixCalculator {

	protected  IPSSMsgHub msg;
	protected  AclfNetwork _net=null;
	protected  RealMatrix fullJacobi=null;
	protected  RealMatrix subJQV=null;


	public JacobiMatrixCalculator(AclfNetwork net, IPSSMsgHub msg) {
		this._net=net;
		this.msg = msg;
	}
	/**
	 * 
	 * @param net
	 *
	 */
	
	private  void transSparse2RealFmt (AclfNetwork net,IPSSMsgHub msg){
		
		SparseEqnMatrix2x2 S=net.formJMatrix(JacobianMatrixType.FULL_POLAR_COORDINATE);   
		 // get sortIndex
	   int[] sortNumberToMatrixIndex = new int[net.getNoBus()+1];
	   int[] sortPQNumberToMatrixIndex = new int[net.getNoBus()+1];
		 // get the number of non-swing buses and PQ buses
       int n = 0;  //record  for non-swing ,that is both PV and PQ 
       int m = 0;  // record only for PQ 
       int mPQ=0,nPQ=0;
       for (Bus bus : net.getBusList()) {
        AclfBus aclfBus = (AclfBus)bus;
        if (!aclfBus.isSwing()) {
            sortNumberToMatrixIndex[bus.getSortNumber()] = n++;  // your matrix index range [0 ... n-1)
            if (!aclfBus.isGenPV()) {
                sortPQNumberToMatrixIndex[bus.getSortNumber()] = m++;  // PQ submatrix index range [0 ... m-1)
         
            }
        }
    }
   // print("n=" +n+"  m="+m +"\n");
    //initialize of the conventional NR jacobi matrix
    RealMatrix H = new Array2DRowRealMatrix(n,n);
    RealMatrix N = new Array2DRowRealMatrix(n,m);
    RealMatrix K = new Array2DRowRealMatrix(m,n);
    subJQV=  new Array2DRowRealMatrix(m,m);
    fullJacobi =new Array2DRowRealMatrix (n+m,n+m);   // Jacobi matrix in classical format.   
    //int index=0;  index for Matrix rows 
      
  for (Bus busi : net.getBusList()) {
       AclfBus aclfBusi = (AclfBus)busi;
       if (!aclfBusi.isSwing()) {
             for (Bus busj : net.getBusList()) {
                 AclfBus aclfBusj = (AclfBus)busj;             
                 if (!aclfBusj.isSwing()) {        
                     int i = busi.getSortNumber();
                     int j = busj.getSortNumber();
                     Matrix_xy elem = S.getElement(i, j);

	               // the following variant is chosen like PQ bus, but it is also suitable for PV bus
	                  double dPdVang = elem.xx;
	                  double dPdVmag = elem.xy;
	                  double dQdVang = elem.yx;
	                  double dQdVmag = elem.yy;
	                   
	                    int m1 = sortNumberToMatrixIndex[i];  // get the bus Index in the net 
                       int n1 = sortNumberToMatrixIndex[j];
                  
                        if (!aclfBusi.isGenPV()) { 
                       	// print("pq bus :"+aclfBusi.getId());
                         mPQ = sortPQNumberToMatrixIndex[i]; // JUST FOR PQ sort 
                          }
                        if (!aclfBusj.isGenPV()) {
                         nPQ = sortPQNumberToMatrixIndex[j]; // JUST FOR PQ sort
                          }
               
                         H.setEntry(m1, n1, dPdVang);// n-1*n-1 ,suitable of PQ & PV bus
                  
                        if(!aclfBusj.isGenPV()){
                           N.setEntry(m1,nPQ,dPdVmag); //n-1*m
                           } //end of this -if
                  
                         // matrix element corresponding to PQ bus 
                         if(!aclfBusi.isGenPV() ){
           	             	 K.setEntry(mPQ,n1,dQdVang);  // m*n-1
	                             if(!aclfBusj.isGenPV()){
	                                subJQV.setEntry(mPQ,nPQ,dQdVmag);//m*m
	                               } 
	                        }
                   }// end of if-busj
                 
	             }//end of for busj
            
	        }//end  if-busi
   
		 }  //end of for busi

	   fullJacobi.setSubMatrix(H.getData(), 0, 0);
	   fullJacobi.setSubMatrix(N.getData(),0,n);
	   fullJacobi.setSubMatrix(K.getData(),n,0);
	   fullJacobi.setSubMatrix(subJQV.getData(),n,n);
	 
	 }//end this method
	public RealMatrix getFullJacobiRealFmt(AclfNetwork net){
		transSparse2RealFmt(net,msg);
	 // the sign of sparseMatrix is opposite to usual, so here  its sign is changed by default .
		return fullJacobi.scalarMultiply(-1);
	}
	
	public  RealMatrix getSubJQVMatrix(AclfNetwork net){
		transSparse2RealFmt(net,msg);
		return this.subJQV;
			
	}
	public double getHii(String aNonSwingBusID){// the diagnose element of SubJptheta with bus of "BusID"
		double Hii=0;
		Matrix_xy elem =null;
		AclfBus acBus=null;
		try{
		acBus=(AclfBus) this._net.getBus(aNonSwingBusID);
		}catch(Exception e){
			e.printStackTrace();
		}
		SparseEqnMatrix2x2 S=_net.formJMatrix(JacobianMatrixType.FULL_POLAR_COORDINATE);
		if(acBus.isActive()&&!acBus.isSwing()){ // both PQ and PV
			
			elem = S.getElement(acBus.getSortNumber(),acBus.getSortNumber());
			Hii=elem.xx;
			
		}
		return Hii;
	}


	public double getLii(String aPQBusID){// the diagnose element of SubJqv with bus of "BusID"
		double Lii=0;
		Matrix_xy elem =null;
		AclfBus acBus=null;
		try{
		acBus=(AclfBus) this._net.getBus(aPQBusID);
		}catch(Exception e){
			e.printStackTrace();
		}
		SparseEqnMatrix2x2 S=_net.formJMatrix(JacobianMatrixType.FULL_POLAR_COORDINATE);
		if(acBus.isActive()&&acBus.isGenPQ()){ // only for PQ
			
			elem = S.getElement(acBus.getSortNumber(),acBus.getSortNumber());
			Lii=elem.yy;
			
		}
		return Lii;
	}
	
	
	
}
