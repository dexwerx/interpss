package org.interpss.vstab.cpf.impl;

import org.apache.commons.math.complex.Complex;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.util.VstabFuncOut;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.algo.impl.DefaultNrSolver;
import com.interpss.core.net.Bus;

public class CorrectorStepSolver extends DefaultNrSolver {
    private CPFAlgorithm cpf=null;
    private CpfHelper cpfHelper=null;
    private LoadIncrease ldInc=null;
    private double incSize=0;

    
	public CorrectorStepSolver(CPFAlgorithm cpfAlgo) {
		super(cpfAlgo.getAclfNetwork());
		this.cpf=cpfAlgo;
		this.cpfHelper=cpf.getCpfHelper();
		this.ldInc=cpfAlgo.getLoadIncrease();
	}
	@Override
	public SparseEqnMatrix2x2 formJMatrix() {
		cpfHelper.setSortNumOfContParam(cpf.getCpfSolver().getSortNumOfContParam());

		SparseEqnMatrix2x2 lfEqn=cpfHelper.formAugmJacobiMatrix();

		return lfEqn;
	}
	/**
	 * 	calculate bus power mismatch. The mismatch stored on 
		the right-hand side of the sparse eqn
		both load increase and gen dispatch are considered
	 */
	@Override
	public void setPowerMismatch(SparseEqnMatrix2x2 lfEqn) {
   
		// to increase load 
		if(!this.cpf.getCpfSolver().isLmdaContParam()){ // if lambda is not keep constant, it will change and therefore will affect the load.
			if(this.cpf.getCpfSolver().getLambda().getValue()>1){ // we assume 100% increasement as a warming level
				IpssLogger.getLogger().warning("Warn:loading index Lambda="+this.cpf.getCpfSolver().getLambda().getValue()+
						", seems to be too large, out of normal range.");
			}

		}

		ldInc.increaseLoad(this.cpf.getCpfSolver().getLambda().getValue());
		// generation dispatching to meet the power mismatch
		this.cpf.getGenDispatch().genDispByResvProp(ldInc.getDeltaSumOfLoad());

		super.setPowerMismatch(lfEqn);
		System.out.println("-------power mismatch( deltaP, deltaQ)-------");
		VstabFuncOut.printBVector(getAclfNet(), lfEqn);
	}

	@Override
	public void updateBusVoltage(SparseEqnMatrix2x2 lfEqn) {
		if(!this.cpf.getCpfSolver().isLmdaContParam()){
			Vector_xy bi=lfEqn.getX(this.cpf.getCpfSolver().getSortNumOfContParam());
			lfEqn.setB(new Complex(bi.x,0), this.cpf.getCpfSolver().getSortNumOfContParam());
		}
		// update the bus voltage using the solution results store in the sparse eqn
		super.updateBusVoltage(lfEqn);
		
		// the solution result of the extra variable defined is stored at B(n+1)  
        this.cpf.getCpfSolver().getLambda().update(lfEqn, 1);// is a factor needed for update?
   
        // output the B vector for test
        System.out.println("-------Delta X( theta, Vmag)-------");
        VstabFuncOut.printBVector(getAclfNet(), lfEqn);
        System.out.println("-------After update-------");
        for(Bus b:this.getAclfNet().getBusList()){
        	AclfBus bus=(AclfBus) b;
        	System.out.println("busId: "+bus.getId()+", ang ="+bus.getVoltageAng()+"mag="+bus.getVoltageMag());
        }
	}

	


}
