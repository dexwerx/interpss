package org.interpss.vstab.cpf.impl;

import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.util.VstabFuncOut;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.algorithm.impl.DefaultNrSolver;
import com.interpss.core.net.Bus;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;

public class CorrectorStepSolver extends DefaultNrSolver {
    private CPFAlgorithm cpf=null;
    CpfHelper cpfHelper=null;
    LoadIncPattern ldPtn=null;
    LoadIncrease ldInc=null;
    
	public CorrectorStepSolver(CPFAlgorithm cpfAlgo) {
		super(cpfAlgo.getAclfNetwork());
		cpf=cpfAlgo;
		cpfHelper=new CpfHelper(getAclfNet());
		ldPtn=VStabObjectFactory.createLdIncPattern(cpfAlgo.getAclfNetwork(), LoadIncScope.NETWORK, LoadIncType.CONST_PF, null);
		ldInc=VStabObjectFactory.createLoadIncrease(cpfAlgo.getAclfNetwork(), ldPtn);
	}
	@Override
	public SparseEqnMatrix2x2 formJMatrix() {
		cpfHelper.setSortNumOfContParam(cpf.getSortNumOfContParam());
		System.out.println("cpf sortNum="+cpf.getSortNumOfContParam());
		System.out.println("cpfHelper sortNum="+cpfHelper.getSortNumOfContParam());
		SparseEqnMatrix2x2 lfEqn=cpfHelper.formAugmJacobiMatrix();
//		VstabFuncOut.printJmatix(lfEqn, 5, 2);
		return lfEqn;
	}
	@Override
	public void setPowerMismatch(SparseEqnMatrix2x2 lfEqn) {
		// calculate bus power mismatch. The mismatch stored on 
		// the right-hand side of the sparse eqn
		
		if(!this.cpf.isLmdaContParam()){ // if lambda is not keep constant, it will change and therefore will affect the load.
			ldInc.increaseLoad(this.cpf.getCpfSolver().getLambda());
		}
		super.setPowerMismatch(lfEqn);
		System.out.println("-------power mismatch( deltaP, deltaQ)-------");
		VstabFuncOut.printBVector(getAclfNet(), lfEqn);
	}

	@Override
	public void updateBusVoltage(SparseEqnMatrix2x2 lfEqn) {
		 
		// update the bus voltage using the solution results store in the sparse eqn
		super.updateBusVoltage(lfEqn);
		
		// the solution result of the extra variable defined is stored at B(n+1)  
        this.cpf.getCpfSolver().getLambda().update(lfEqn);// is any factor needed for update?
   
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
