package org.interpss.vstab.cpf.impl;

import org.interpss.numeric.sparse.SparseEqnMatrix2x2;
import org.interpss.vstab.cpf.CPFAlgorithm;

import com.interpss.common.util.IpssLogger;


public class LambdaParam {
	/**
	 * A class about Lambda Parameter, the load (and may generation in the future) increase index
	 * mainly including the operations involving in the J-matrix
	 *  
	 *     n - The Lambda parameter position in the J-matrix
	 * 
	 * @author Tony Huang
	 *
	 */
	private int n;
	private double val=0;
	private double lastValue=0;
    public LambdaParam(int position,double value) {
    	this.n=position;
    	val=value;
    	
    }
	/**
	 * @return the sortNumber
	 */
	public int getPosition() {
		return n;
	}
	/**
	 * @param n the n to set
	 */
	public void setPosition(int sortNum) {
		this.n = sortNum;
	}
	/**
	 * @return the value of Lambda Parameter
	 */
	public double getValue() {
		return val;
	}
	/**
	 * @param valOfLambdaParam the valOfLambdaParam to set
	 */
	public void setValue(double valOfLambdaParam) {
		this.val = valOfLambdaParam;
	}
	public void update(SparseEqnMatrix2x2 lfEqn) {
		System.out.println("before update lambda="+this.val);
		this.val+=lfEqn.getX(n).x;
		System.out.println("deltaL="+lfEqn.getX(n).x+"   ,after update, lambda="+this.val);
	}
	public void update(SparseEqnMatrix2x2 lfEqn, double stepSize) {
//		IpssLogger.getLogger().info("before update lambda="+this.val);
		lastValue=this.val;
//		double maxDeltaVal=0.01;//CPFAlgorithmImpl.DEFAULT_MAX_STEP_SIZE;// still need to test
		double dVal=0;
//		if(Math.abs(stepSize*lfEqn.getX(n).x)>maxDeltaVal){
//			dVal=maxDeltaVal;
//			if(stepSize*lfEqn.getX(n).x<0){
//				dVal=-maxDeltaVal;
//			}
//			
//		}
//		else dVal=stepSize*lfEqn.getX(n).x;
		
		dVal=stepSize*lfEqn.getX(n).x;
		this.val+=dVal;
		IpssLogger.getLogger().info("calculating deltaL="+lfEqn.getX(n).x+";  actual chosen deltaL="+dVal+";  after update lambda="+this.val);
	}
	
	public void update(double deltaL){
		IpssLogger.getLogger().info("before update: lambda="+this.val);
		this.lastValue=this.val;
		this.val+=deltaL;
		IpssLogger.getLogger().info("After update:  lambda="+this.val);

	}
	public void backToLastState(){
		this.val=this.lastValue;
	}
	
	
}
