package org.interpss.vstab.cpf.impl;

import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.util.VstabFuncOut;

import com.interpss.common.datatype.Matrix_xy;
import com.interpss.common.datatype.Vector_xy;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.impl.DefaultNrSolver;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.sparse.SparseEqnMatrix2x2;

public class CorrectorStepSolver extends DefaultNrSolver {
	private Vector_xy contParaMismatch;
	private double valOfContPara=0;
	private LambdaParam lambda=null;
    private CPFAlgorithm cpf=null;
    CpfHelper cpfHelper=null;
    
	public CorrectorStepSolver(CPFAlgorithm cpfAlgo) {
		super(cpfAlgo.getAclfNet());
		cpf=cpfAlgo;
		this.lambda=cpfAlgo.getLambdaParam();
		cpfHelper=new CpfHelper(getAclfNet(), cpf.getMsgHub());
//		cpfHelper.setSortNumOfContParam(cpfAlgo.getSortNumOfContParam());
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
		super.setPowerMismatch(lfEqn);
		System.out.println("-------power mismatch-------");
		VstabFuncOut.printBVector(getAclfNet(), lfEqn);
	}

	@Override
	public void updateBusVoltage(SparseEqnMatrix2x2 lfEqn) {
		 
		// update the bus voltage using the solution results store in the sparse eqn
		super.updateBusVoltage(lfEqn);
		
		// the solution result of the extra variable defined is stored at B(n+1)  
        lambda.update(lfEqn);// is any factor needed for update?
        // out put the B vector for test
        VstabFuncOut.printBVector(getAclfNet(), lfEqn);
	}
	
	


}
