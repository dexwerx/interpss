package org.interpss.vstab.eigen.impl;

import org.apache.commons.math.linear.EigenDecomposition;
import org.apache.commons.math.linear.EigenDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.util.MathUtils;
import org.interpss.vstab.eigen.ESAResult;
import org.interpss.vstab.eigen.EigenAnalysis;
import org.interpss.vstab.eigen.JacobiMatrixCalculator;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.aclf.AclfNetwork;

public class EigenAnalysisImpl implements EigenAnalysis {

	private RealMatrix jacobi=null;
    protected ESAResult result=null;
    protected AclfNetwork _net=null;
    
    private double minEigenValue;
    private int columnOfMinEigen=0;
    private RealMatrix eigVectors=null;
	protected RealVector rightMinEigenVector;
	protected RealVector leftMinEigenVector;
    protected JacobiMatrixCalculator jacobiCalc = null;
    


    public EigenAnalysisImpl(AclfNetwork net, IPSSMsgHub msg){
    	this._net=net;
        this.jacobiCalc= new JacobiMatrixCalculator(net, msg);
    }

	@Override
	public void runEigenStrAnalysis() {
		calJacobiMatrix();
		calMinEigenValue();
		calRightMinEigVector();
		calLefMinEigVector();
		saveESAResult();
	}
	@Override
	public ESAResult getESAResult() {
		return result;
	}
    
    public RealMatrix getJacobi() {
		return jacobi;
	}

	public void setJacobi(RealMatrix jacobi) {
		this.jacobi = jacobi;
	}

	private double getMinEigenValue() {
		return minEigenValue;
	}

	private void setMinEigenValue(double minEigenValue) {
		this.minEigenValue = minEigenValue;
	}

	private int getColumnOfMinEigen() {
		return columnOfMinEigen;
	}

	private RealVector getLeftMinEigenVector() {
		return leftMinEigenVector;
	}
	private RealVector getRightMinEigenVector() {
		return rightMinEigenVector;
	}

	private void saveESAResult(){
		result.setMinEigenValue(getMinEigenValue());
		result.setEigenVector(eigVectors);
		result.setLeftEigenVector(getLeftMinEigenVector());
		result.setRightEigenVector(getRightMinEigenVector());
		
	}
	private void calJacobiMatrix(){
		if(_net!=null){
		jacobi=jacobiCalc.getFullJacobiRealFmt(_net);
		}
		else{
			System.out.print("Error: no network data is loaded yet, or not properly loaded,please check the loading");
			return;
		}
	}
	public  void calMinEigenValue(){
		 
		 double eig_Min =99;// chosen by ramdom ,just make sure it is large enough
		 columnOfMinEigen=0;
		 double[] realEigenValues=null;
		 EigenDecomposition eigDcp=null;
		 
		 try{
	     eigDcp  =new EigenDecompositionImpl(jacobi,MathUtils.SAFE_MIN);
	     realEigenValues=eigDcp.getRealEigenvalues();
	     
		 // search the zero eigen value and its index 
		 eig_Min = realEigenValues[0];

		 for (int i=1;i<realEigenValues.length;i++){
		    if (eig_Min <realEigenValues[i]) { 
		       eig_Min =Math.abs(realEigenValues[i]); 
		       columnOfMinEigen =i;
		    } //end of if
		 } //end of for
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		setMinEigenValue(realEigenValues[columnOfMinEigen]);
	}
	private RealMatrix getEigenVectors(){
		     System.out.println(jacobi.getColumnDimension()+"\n"+
		    		 jacobi.getRowDimension()+"\n"+jacobi.getData());
			 EigenDecomposition eigDcp  =new EigenDecompositionImpl(jacobi,MathUtils.SAFE_MIN);
		     eigVectors= eigDcp.getV();
		     return eigVectors;
	}
		
	
	private  void calRightMinEigVector(){
		if(getEigenVectors()==null){
			System.out.print("Error: EigenVectors is null");
				
			}
		else this.rightMinEigenVector=getEigenVectors().transpose().getColumnVector(getColumnOfMinEigen());
		
	}
	private void calLefMinEigVector(){
		if(getEigenVectors()==null){
			System.out.print("Error: EigenVectors is null");
				
			}
		else this.leftMinEigenVector=getEigenVectors().getColumnVector(getColumnOfMinEigen());
	}
	
	
	

	}


