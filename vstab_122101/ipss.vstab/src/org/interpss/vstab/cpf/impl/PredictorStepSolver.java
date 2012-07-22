package org.interpss.vstab.cpf.impl;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.linear.ArrayRealVector;
import org.interpss.vstab.cpf.AbstractStepSolver;
import org.interpss.vstab.cpf.CPFAlgorithm;

import com.interpss.common.datatype.Vector_xy;
import com.interpss.common.exp.InterpssException;
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

/*
 *  all //** are by Mike for code refactor
 */

public class PredictorStepSolver extends AbstractStepSolver{
	protected boolean isCrossMPP=false;
	protected boolean stepSizeCntrl=false;
	private final double DEFAULT_STEP_SIZE=0.2;
	private double stepSize=DEFAULT_STEP_SIZE;
	private double oldStepSize=0;
	private CpfHelper cpfHelper=null;
	private ArrayRealVector deltaX_Lambda=null;
	private LambdaParam lambda=null;
	private CPFAlgorithm cpf=null;
	/**
	 *  the constructor of the PredictorStepSolver class
	 * @param net
	 * @param msg
	 */
//**	public PredictorStepSolver(CPFAlgorithm cpfAlgo,IPSSMsgHub msg) {
	public PredictorStepSolver(CPFAlgorithm cpf, LambdaParam lambda) {
//**		super(cpfAlgo.getAclfNet(), msg);
		//super(aclfNet);
		this.cpf=cpf;
		this.lambda= lambda;
//**		cpfHelper=new CpfHelper(net,msg);
		cpfHelper=new CpfHelper(cpf.getAclfNetwork());
		this.deltaX_Lambda=new ArrayRealVector(cpf.getAclfNetwork().getNoBus()*2+1); // swing bus is included
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
		double x = this.stepSize;
		if(isStepSizeControl()) {
			x=this.stepSize-this.oldStepSize;
		}

		final double actualStep=x;
    	this.cpf.getAclfNetwork().forEachAclfBus(new IAclfBusVisitor() {
			public void visit(AclfBus bus) {
			  if((!bus.isSwing())&bus.isActive()) {
				 Vector_xy v=augmentedJacobi.getBVect_xy(bus.getSortNumber());
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
    	lambda.update(augmentedJacobi, actualStep); //update lambda
    }
	/**
	 * calculate the tangent vector
	 * @return
	 */
    private boolean calDeltaPredResult() {
//    cpfHelper.setSortNumOfContParam(getSortNumofContParam());
   	this.augmentedJacobi=cpfHelper.formAugmJacobiMatrix();

     //   set the [B] elements(right-hand side of Jacobian matrix) to [0,+-1] ,
     //   only the element corresponding to Continuous parameter is set to +1,or -1, depending on the slope of continuous parameter
   	
    int contParaSign=getContParaSign();  
    augmentedJacobi.setBi(new Complex(contParaSign,0),lambda.getPosition());

     // solve Jau*[dx,dLamda]T=[0,+-1]
     
    try {
		if (!augmentedJacobi.luMatrixAndSolveEqn(this.tolerance)) {
			return false;
		}
	} catch (InterpssException e) {
		e.printStackTrace();
	}
    
	// save tangent vector result to DeltaX_Lamba
 
    saveDeltaRslt2Vctr();
    
    return true;
    }
    

    public boolean isCrossMaxPwrPnt() {
    	// only sign of Lambda is used for judgment.

    	if(cpf.getSortNumOfContParam()!=lambda.getPosition()) {
    		if(deltaX_Lambda.getEntry(deltaX_Lambda.getDimension()-1)<0)// Lambda parameter is at the last of deltaX_Lambda vector
    			return this.isCrossMPP=true;
    	}
    	return this.isCrossMPP=false;
    }
    private int getContParaSign() {
    	if(isCrossMaxPwrPnt()) {
    		return  +1;
    	}
    	else return -1;
    }
    private void saveDeltaRslt2Vctr() {
    	
    	int i=0;
    	for (Bus b : this.cpf.getAclfNetwork().getBusList()) { 
    	//	Bus b = (Bus)localIterator.next();
            i=b.getSortNumber();
            Vector_xy dv=this.augmentedJacobi.getBVect_xy(i);   
            this.deltaX_Lambda.setEntry(2*i-2, dv.x);
            this.deltaX_Lambda.setEntry(2*i-1, dv.y);
        }
    	i=this.cpf.getAclfNetwork().getNoBus()+1; // lambda index 
    	double deltaL=this.augmentedJacobi.getBVect_xy(i).x;
        this.deltaX_Lambda.setEntry(this.cpf.getAclfNetwork().getNoBus()*2, deltaL);
    }
    protected boolean isStepSizeControl() {
    	return this.stepSizeCntrl;
    }
    public void enableStepSizeControl(boolean stepControl) {
    	this.stepSizeCntrl=stepControl;
    }
    private void applyStepSizeControl() {
    	this.oldStepSize=stepSize;
    	this.stepSize*=0.5; // cut to the half of last step size
    	
    }
    public ArrayRealVector getDeltaXLambda() {
    	return this.deltaX_Lambda;
    }
    
//    public CPFAlgorithm getCpfAlgo() {
//    	return this.cpf;
//    }

}
