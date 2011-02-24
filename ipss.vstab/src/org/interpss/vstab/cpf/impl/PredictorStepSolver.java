package org.interpss.vstab.cpf.impl;
import java.util.Iterator;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.linear.ArrayRealVector;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.vstab.cpf.AbstractStepSolver;
import org.interpss.vstab.cpf.CPFAlgorithm;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.net.Bus;
/**
 * A Solver for predictor Step, to calculate the tangent vector and to find a good initial guess for 
 * the corrector step.
 * 
 * @author Tony Huang
 *
 */

public class PredictorStepSolver extends AbstractStepSolver{
	private boolean isCrossMPP=false;
	private boolean stepSizeCtrl=false;

	private CpfHelper cpfHelper=null;
	protected ArrayRealVector deltaX_Lambda=null;// all state variables are saved
	private ArrayRealVector deltaV_Lambda=null;// only Vmag and Lambda is save for continuation param judgment;
	private CPFAlgorithm cpf=null;
	
	private double stepSize=0;
	private double oldStepSize=0;
	/**
	 *  the constructor of the PredictorStepSolver class
	 * @param net
	 * @param msg
	 */
	public PredictorStepSolver(CPFAlgorithm cpfAlgo) {

		this.cpf=cpfAlgo;
		this.cpfHelper=cpfAlgo.getCpfHelper();
		this.deltaX_Lambda=new ArrayRealVector(cpf.getAclfNetwork().getNoBus()*2+1); // swing bus is included
	    this.deltaV_Lambda=new ArrayRealVector(cpf.getAclfNetwork().getNoBus()+1);
	    stepSize=cpfAlgo.getStepSize();
	}
	/**
	 * a step Solver, overrides the same method of AbstractStepSolver 
	 */
	@Override
    public boolean stepSolver() {
    	if(isStepSizeControl()) {
    		applyStepSizeControl();
    	}
    	else {
    		if(!calDeltaPredResult()) {
    			return false;
    		}
    		
    	}
    	updateBusVoltage();
    	return true;
    }
	/**
	 * To update voltage of all buses after successful solving, overrides the same method of AbstractStepSolver 
	 */
	@Override
    public void updateBusVoltage() {
		double maxStep=this.cpf.getMaxStepSize()/this.getDeltaVLambda().getLInfNorm();
		
		double x =Math.min(maxStep,this.stepSize);
		if(isStepSizeControl()) {
			x=this.stepSize-this.oldStepSize;
		}
        
		final double actualStep=x;
    	this.cpf.getAclfNetwork().forEachAclfBus(new IAclfBusVisitor() {
			public void visit(AclfBus bus) {
			  if((!bus.isSwing())&bus.isActive()) {
				 Vector_xy v=augmentedJacobi.getX(bus.getSortNumber());
				 double vang=bus.getVoltageAng();
				 double vmag=bus.getVoltageMag();
				 if(!bus.isGenPV()) {
					 vmag+=actualStep*vmag*v.y;// x(k+1)=x(k)+deltaX
				  }
				 
				 vang+=actualStep*v.x;
				 bus.setVoltage(vmag,vang);
			  }
		  }		
    	});
    	this.cpf.getCpfSolver().getLambda().update(augmentedJacobi, actualStep); //update lambda
    }
	/**
	 * calculate the tangent vector
	 * @return
	 */
    private boolean calDeltaPredResult() {

   	this.augmentedJacobi=cpfHelper.formAugmJacobiMatrix();

     //   set the [B] elements(right-hand side of Jacobian matrix) to [0,+-1] ,
     //   only the element corresponding to Continuous parameter is set to +1,or -1, depending on the slope of continuous parameter
   	
    int signOfcontPara=getContParaSign();  
    this.augmentedJacobi.setB(new Complex(signOfcontPara,0),this.cpf.getCpfSolver().getLambda().getPosition());

     // solve Jau*[dx,dLamda]T=[0,+-1]
     
    try {
		if (!this.augmentedJacobi.luMatrixAndSolveEqn(this.tolerance)) {
			return false;
		}
	} catch (IpssNumericException e) {
		e.printStackTrace();
	}
    
	// save tangent vector result to DeltaX_Lamba
 
    saveDeltaRslt2Vctr();
    
    return true;
    }
    /**
     * 
     * @return to determine the network state whether it is across the PV nose or not 
     */
    public boolean isCrossMaxPwrPnt() {
    	// Now only sign of Lambda is used for judgment.

    	if(!cpf.getCpfSolver().isLmdaContParam()) {
    		if(deltaV_Lambda.getEntry(deltaV_Lambda.getDimension()-1)<0)// Lambda parameter is at the last of deltaX_Lambda vector
    			return this.isCrossMPP=true;
    	}
    	return this.isCrossMPP=false;
    }
    private int getContParaSign() {
    	if(isCrossMaxPwrPnt()) {
    		return  -1;
    	}
    	else return +1;
    }
    private void saveDeltaRslt2Vctr() {
    	
    	int i=0;
    	for (Iterator localIterator = cpf.getAclfNetwork().getBusList().iterator(); localIterator.hasNext(); ) { 
    		Bus b = (Bus)localIterator.next();
            i=b.getSortNumber();// sort number is still ranging between 1->n,while SparseEqn is indexed 0->n-1
            Vector_xy dv=this.augmentedJacobi.getX(i);   
            this.deltaX_Lambda.setEntry(2*i, dv.x);  // dv.x->Vang;
            this.deltaX_Lambda.setEntry(2*i+1, dv.y); //dv.y->Vmag;
            this.deltaV_Lambda.setEntry(i, dv.y);
        }
    	i=this.cpf.getCpfSolver().getLambda().getPosition(); // lambda index 
    	double deltaL=this.augmentedJacobi.getX(i).x;
        this.deltaX_Lambda.setEntry(this.cpf.getAclfNetwork().getNoBus()*2, deltaL);
        this.deltaV_Lambda.setEntry(i, deltaL);
    }
    protected boolean isStepSizeControl() {
    	return this.stepSizeCtrl;
    }
    /**
     * 
     * @param specify "stepControl" parameter to enable step size control or not;
     */
    public void enableStepSizeControl(boolean stepControl) {
    	this.stepSizeCtrl=stepControl;
    }
    private void applyStepSizeControl() {
    	this.oldStepSize=stepSize;
    	this.stepSize*=0.5; // cut to the half of last step size
    	this.cpf.setStepSize(this.stepSize);// update the step size;
    	this.stepSizeCtrl=false; // disable its function by changing it back to FALSE
    }
    /**
     * 
     * @return a ArrayRealVector containing DeltaX(Vang and Vmag) and DeltaLambda and indexed by sort number
     */
    public ArrayRealVector getDeltaXLambda() {
    	return this.deltaX_Lambda;
    }
    /**
     * 
     * @return a ArrayRealVector containing DeltaVmag and DeltaLambda and indexed by sort number
     */
    public ArrayRealVector getDeltaVLambda() {
    	return this.deltaV_Lambda;
    }
    /**
     * 
     * @return the sort number of the continuation parameter for the next step;
     */
    public int getNextStepContParam(){
    	return calMaxDeltaX();
    }
    private int calMaxDeltaX(){ // only Vmag and Lambda are considered here
    	ArrayRealVector temp=(ArrayRealVector) deltaV_Lambda.mapAbs();
    	return temp.getMaxIndex();
    	
    }

}
