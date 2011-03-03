package org.interpss.vstab.cpf.impl;
import java.util.Iterator;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.linear.ArrayRealVector;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;
import org.interpss.vstab.cpf.CPFAlgorithm;

import com.interpss.common.util.IpssLogger;
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

public class PredictorStepSolver {
	public final double DEFAULT_LU_SOLVER_TOLEARNCE=9.95e-21;
	private double tolerance=DEFAULT_LU_SOLVER_TOLEARNCE;
	private boolean isCrossMPP=false;
	private boolean stepSizeCtrl=false;
	private CPFAlgorithm cpf=null;
	private CpfHelper cpfHelper=null;
	protected ArrayRealVector deltaX_Lambda=null;// all state variables are saved
	private ArrayRealVector deltaV=null;// only Vmag  is save for continuation param judgment;
	private double deltaLambda=0;
	private double stepSize=0;
	private double oldStepSize=0;
	private SparseEqnMatrix2x2 augmentedJacobi;
	/**
	 *  the constructor of the PredictorStepSolver class
	 * @param CPFAlgorithm cpfAlgo
	 * 
	 */
	public PredictorStepSolver(CPFAlgorithm cpfAlgo) {

		this.cpf=cpfAlgo;
		this.deltaX_Lambda=new ArrayRealVector(cpf.getAclfNetwork().getNoBus()*2+1); // swing bus is included
	    this.deltaV=new ArrayRealVector(cpf.getAclfNetwork().getNoBus());
	    stepSize=cpfAlgo.getStepSize();
	}
	/**
	 * Solver operation, overrides the same method of AbstractStepSolver 
	 */

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
    	// point out the next step continuation parameter
    	return true;
    }
	/**
	 * To update voltage of all buses after successful solving, overrides the same method of AbstractStepSolver 
	 */

    public void updateBusVoltage() {
		double maxStep=this.cpf.getMaxStepSize()/this.getDeltaV().getLInfNorm();
		double x =Math.min(maxStep,this.stepSize);
		this.cpf.setStepSize(x);
		
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
	 * calculate the tangent vector [delta_X,delta_Lambda]
	 * @return
	 */
    private boolean calDeltaPredResult() {
	this.cpfHelper=new CpfHelper(cpf);
    this.cpfHelper.setSortNumOfContParam(this.cpf.getCpfSolver().getSortNumOfContParam());
    
   	this.augmentedJacobi=cpfHelper.formAugmJacobiMatrix();

     //   set the [B] elements(right-hand side of Jacobian matrix) to [0,+-1] ,
     //   only the element corresponding to Continuous parameter is set to +1,or -1, depending on the slope of continuous parameter
   	
    int signOfcontPara=getContParaSign();  
    this.augmentedJacobi.setB(new Complex(signOfcontPara,0),this.cpf.getCpfSolver().getLambda().getPosition());
    System.out.print("");
     // solve Jau*[dx;dLamda]=[0;+-1]
     
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
    		if(deltaLambda<0)// Lambda parameter is at the last of deltaX_Lambda vector
    			return this.isCrossMPP=true;
    	}
    	return this.isCrossMPP=false;
    }
    private int getContParaSign() {
    	if(isCrossMaxPwrPnt()&&this.cpf.getCpfSolver().isLmdaContParam()) {
    		return  -1;
    	}
    	else if(!this.cpf.getCpfSolver().isLmdaContParam()){
    		return -1;
    	}
    	else return +1;
    }
    /**
     *  save the network variables (bus Vang and Vmag) with the bus sort number as index;
     *  and this is consistent with the power flow solution result storage;
     */
    private void saveDeltaRslt2Vctr() {
    	
    	int i=0;
    	for (Iterator localIterator = cpf.getAclfNetwork().getBusList().iterator(); localIterator.hasNext(); ) { 
    		Bus b = (Bus)localIterator.next();
            i=b.getSortNumber();// sort number is still ranging between 1->n,while SparseEqn is indexed 0->n-1
            Vector_xy dv=this.augmentedJacobi.getX(i);   
            this.deltaX_Lambda.setEntry(2*i, dv.x);  // dv.x->Vang;
            if(((AclfBus)b).isGenPV()||((AclfBus)b).isSwing()){
            	dv.y=0;
            }
            this.deltaX_Lambda.setEntry(2*i+1, dv.y); //dv.y->Vmag;
            this.deltaV.setEntry(i, dv.y);
        }
    	i=this.cpf.getCpfSolver().getLambda().getPosition(); // lambda index 
    	this.deltaLambda=this.augmentedJacobi.getX(i).x;
        this.deltaX_Lambda.setEntry(this.cpf.getAclfNetwork().getNoBus()*2, this.deltaLambda);
        
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
    	
    	this.stepSize=(this.stepSize<this.cpf.getMinStepSize())?this.cpf.getMinStepSize():this.stepSize;
    	
    	this.cpf.setStepSize(this.stepSize);// update the step size;
    	this.stepSizeCtrl=false; // disable this function by changing it back to FALSE
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
    public ArrayRealVector getDeltaV() {
    	return this.deltaV;
    }



}
